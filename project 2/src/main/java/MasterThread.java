import java.io.*;
import java.net.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

class MasterThread extends Thread {
    Socket sub_socket;
    int pid;
    String filename;
    AtomicInteger isAlive;
    AtomicInteger isDone;
    AtomicInteger flag;
    AtomicInteger active_workers;
    ConcurrentHashMap<String, Integer> filenames_pid;
    ConcurrentHashMap<String, String> input_output;
    AtomicInteger isDone_all;

    public MasterThread(Socket sub_socket, ConcurrentHashMap<String, Integer> filenames_pid, ConcurrentHashMap<String, String> input_output, AtomicInteger flag, AtomicInteger active_workers, AtomicInteger isDone_all) {
        this.sub_socket = sub_socket;
        this.filenames_pid = filenames_pid;
        this.input_output = input_output;
        isAlive = new AtomicInteger(1);
        this.flag = flag;
        this.active_workers = active_workers;
        this.isDone = new AtomicInteger(0);
        this.isDone_all = isDone_all;
    }

    public void run() {
        try {
            System.out.println("Master Thread Started");
            InputStreamReader instream = new InputStreamReader(sub_socket.getInputStream());
            BufferedReader br = new BufferedReader(instream);
            PrintStream ps = new PrintStream(sub_socket.getOutputStream());

            String msg = br.readLine();
            System.out.println(msg);
            String[] arr1 = msg.split(" ");
            if (arr1[0].equals("PID")) {
                pid = Integer.parseInt(arr1[1]);
                System.out.println("started_heartbeatmaster");
                for (String key: filenames_pid.keySet()) {
                    if (filenames_pid.get(key) == -1) {
                        filenames_pid.replace(key, pid);
                        filename = key;
                        ps.println("map " + filename);
                        System.out.println("MasterThread: master_heartbeat " + filename); 
                        break;
                    }
                }
                // ps.println("master_heartbeat" + filename);
                // new HeartBeatMasterThread(sub_socket, isAlive, isDone, input_output, filenames_pid).start();
            }

   
            while(true) {

                if(isDone.get() == 0) {
                    int count = 0;
                    for (String key: filenames_pid.keySet()) {
                        if ((filenames_pid.get(key) != -1) & (filenames_pid.get(key) != pid)){
                            count++;
                            System.out.println("-------------count incremented");
                        }
                            
                    }
                    if(count==filenames_pid.size()) {
                        System.out.println("--------------isDone is set to one");
                        isDone.set(1);
                        isDone_all.incrementAndGet();
                        System.out.println("isDoneAll:" + isDone_all.get());
                        System.out.println("MasterThread: no more files");
                        ps.println("no more files");

                        break;
                    }

                }
                msg = br.readLine();
                System.out.println("MasterThread: " + msg);
                String arr[] = msg.split(" ");

               
                if (arr[0].equals("mapping_done_by_worker")) {
                    //System.out.println("Received: " + msg);
                    // pid = Integer.parseInt(arr[1]);
                    //System.out.println("PID is " + pid);
                    System.out.println(arr[1]);
                    System.out.println(arr[2]);
                    input_output.put(arr[1], arr[2]);
                    filenames_pid.put(arr[1], 1);
                    for (String key: filenames_pid.keySet()) {
                        if (filenames_pid.get(key) == -1) {
                            filenames_pid.replace(key, pid);
                            filename = key;
                            ps.println("map " + filename);
                            System.out.println("MasterThread: map " + filename); 
                            break;
                        }
                    }
                      
                }

                if(arr[0].equals("worker_heartbeat")){
                    ps.println("master_heartbeat");
                    System.out.println("Received: master_heartbeat");

                    TimeUnit.SECONDS.sleep(2);
                }
                //  fault -tolerance
                if((isAlive).get()==0) {
                    filenames_pid.put(filename, -1);
                    flag.set(1);
                    active_workers.decrementAndGet();
                    break;
                }
            }
            sub_socket.close();
        } catch(IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

    }
}    