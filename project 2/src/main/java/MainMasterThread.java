import java.io.*;
import java.net.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

class MainMasterThread extends Thread{
    int num_workers;
    String[] input_filenames;
    int port;
    ConcurrentHashMap<String, Integer> filenames_pid;
    ConcurrentHashMap<String, String> input_output;
    AtomicInteger flag;
    AtomicInteger active_workers;
    AtomicInteger isDone_all;

    public MainMasterThread(WordCount wordcount) {
        num_workers = wordcount.num_workers;
        input_filenames = wordcount.input_filenames;
        port = 5000;
        filenames_pid = new ConcurrentHashMap<String, Integer>(input_filenames.length);
        for (int i =0; i< input_filenames.length; i++)
            filenames_pid.put(input_filenames[i], -1);
        flag = wordcount.flag;
        active_workers = new AtomicInteger(0);
        isDone_all = new AtomicInteger(0);
        input_output = wordcount.input_output;
    }

    public void run() {
        try {
            System.out.println("Main master thread started");
            ServerSocket main_socket = new ServerSocket(port); 
            int i =0; 
            while (i < num_workers) {
                Socket sub_socket = main_socket.accept();
                new MasterThread(sub_socket, filenames_pid, input_output, flag, active_workers, isDone_all).start();
                i++;
            }
            System.out.println("all master threads created");
            active_workers.set(num_workers);

            //Fault Tolerance

            while(flag.get()!=2) {
                if ((active_workers.get() < num_workers) && (filenames_pid.containsValue(-1))) {
                    Socket sub_socket = main_socket.accept();
                    new MasterThread(sub_socket, filenames_pid, input_output, flag, active_workers, isDone_all).start();
                    active_workers.incrementAndGet();
                }
                if (isDone_all.get() == num_workers) {
                    flag.set(2);
                    System.out.println("GG MainMasterThread");
                }

            }
            main_socket.close();
            System.out.println("Main Socket Closed");
        } catch(IOException e) {
            e.printStackTrace();
        }
    }
} 