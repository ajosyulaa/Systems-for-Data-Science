import java.io.*;
import java.net.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

class HeartBeatMasterThread extends Thread {
    AtomicInteger isAlive;
    AtomicInteger isDone;
    Socket sub_socket;
    ConcurrentHashMap<String, String> input_output;
    ConcurrentHashMap<String, Integer> filenames_pid;
    String filename;
    int pid;

    public HeartBeatMasterThread(Socket sub_socket,  AtomicInteger isAlive,AtomicInteger isDone,  ConcurrentHashMap<String, String> input_output, ConcurrentHashMap<String, Integer> filenames_pid) {
        this.isAlive = isAlive;
        this.isDone = isDone;
        this.sub_socket = sub_socket;
        this.input_output =  input_output;
        this.filenames_pid =  filenames_pid;
    }

    public void run() {
        
        try {
            System.out.println("------------Heartbeat Master Thread Started");
            InputStreamReader instream = new InputStreamReader(sub_socket.getInputStream());
            BufferedReader br = new BufferedReader(instream);
            PrintStream ps = new PrintStream(sub_socket.getOutputStream());
            ps.println("started_heartbeatmaster");
            System.out.println("HBM:started_heartbeatmaster");
            ps.println("master_heartbeat");
            System.out.println("HBM:master_heartbeat");

            while(true) {          
                String msg = br.readLine();
                System.out.println("Received: " + msg);
                if(msg != null) {
                    String arr[] = msg.split(" ");
                    // System.out.println("HBW: " + msg[0]);
                    
                    if(arr[0].equals("worker_heartbeat")){
                        TimeUnit.SECONDS.sleep(2);
                        ps.println("master_heartbeat");
                        System.out.println("Received: master_heartbeat");
                    }

                    // if (arr[0].equals("PID")) {
                    //     System.out.println("Received: " + msg);
                    //     pid = Integer.parseInt(arr[1]);
                    //     //System.out.println("PID is " + pid);
                    //     for (String key: filenames_pid.keySet()) {
                    //         if (filenames_pid.get(key) == -1) {
                    //             filenames_pid.replace(key, pid);
                    //             filename = key;
                    //             ps.println("map " + filename);
                    //             System.out.println("HBM: map " + filename);
                    //             break;
                    //         }
                    //     }    
                    // }

                    // if (arr[0].equals("mapping_done_by_worker")) {
                    //     System.out.println("Received: mapping done message received");
                    //     input_output.put(arr[1], arr[2]);
                    //     filenames_pid.put(arr[1], 1);
                    // }
                }
                if(isDone.get()==1){
                    System.out.println("---------breaking HeartBeatMasterThread while");
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            isAlive.set(0);
        } catch (InterruptedException e) {
            e.printStackTrace();           
        }
    }
}