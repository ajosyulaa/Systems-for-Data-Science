import java.io.*;
import java.net.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.lang.management.ManagementFactory;

class WorkerThread {
    int pid;
    int port;
    AtomicInteger worker_flag;
    public WorkerThread() {
        port = 5000;
        worker_flag = new AtomicInteger(0);
    }

    public static void main(String[] args) throws Exception {
        // System.out.println("in main of worker thread");
        WorkerThread worker = new WorkerThread();
        worker.run();
    }

    public void run() {
        try{            
            Socket worker_socket = new Socket("localhost", port);
            System.out.println("worker_socket created");
            pid = Integer.parseInt(ManagementFactory.getRuntimeMXBean().getName().split("@")[0]);
            HeartBeatWorkerThread obj = new HeartBeatWorkerThread(worker_socket, pid, worker_flag);
            obj.start();
            while(true) {
                if (worker_flag.get()==1) {
                    obj.join();
                    break;
                }
            }
            worker_socket.close();
        }catch(IOException e) {
            e.printStackTrace();
        }catch (InterruptedException e) {
            e.printStackTrace();;            
        }
    }
}
