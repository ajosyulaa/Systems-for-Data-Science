import java.io.*;
import java.net.*;
import java.util.concurrent.atomic.AtomicInteger;
// import java.util.concurrent.TimeUnit;
import java.util.concurrent.ConcurrentHashMap;

class HeartBeatWorkerThread extends Thread {
    Socket sub_socket;
    int pid;
    AtomicInteger worker_flag;
    ConcurrentHashMap<Integer,String> filenames;
    AtomicInteger HeartBeat_Mapper;


    public HeartBeatWorkerThread(Socket sub_socket, int pid, AtomicInteger worker_flag){
        this.sub_socket = sub_socket;
        this.pid = pid;
        this.worker_flag = worker_flag;
        filenames = new ConcurrentHashMap<Integer,String>();
        HeartBeat_Mapper = new AtomicInteger(0);
    }

    public void run(){

        try {
            InputStreamReader instream = new InputStreamReader(sub_socket.getInputStream());
            BufferedReader br = new BufferedReader(instream);
            PrintStream ps = new PrintStream(sub_socket.getOutputStream());
            Mapper mapper_obj = new Mapper(sub_socket, pid, filenames, HeartBeat_Mapper);
            
            ps.println("PID " + pid);
            mapper_obj.start();
    
            while(true) {
                String msg = br.readLine();
                String[] words = msg.split(" ");
                if ((msg.equals("master_heartbeat")) & (HeartBeat_Mapper.get()==0)) {
                    ps.println("mapping_done_by_worker " + filenames.get(0)  + " " + filenames.get(1));
                }
                else if(msg.equals("master_heartbeat"))
                    ps.println("worker_heartbeat");

                if (msg.equals("no more files")) {
                    // System.out.println("GG");
                    worker_flag.set(1); 
                    mapper_obj.join();
                }
                if (words[0].equals("map")) {
                    filenames.put(0,words[1]);
                    HeartBeat_Mapper.set(1);
                    ps.println("worker_heartbeat");
                }

            }
        } catch(IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();           
        }
    }
}