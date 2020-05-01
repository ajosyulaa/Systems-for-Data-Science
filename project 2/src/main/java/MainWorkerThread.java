import java.io.*;
import java.util.concurrent.atomic.AtomicInteger;

class MainWorkerThread extends Thread{
    int num_workers;
    AtomicInteger flag;
    WordCount wordcount;

    public MainWorkerThread(WordCount wordcount) {
        num_workers = wordcount.num_workers;
        flag = wordcount.flag; 
        this.wordcount = wordcount;
    }

    public void run() {
        System.out.println("Main Worker Thread Started");
        try {
            int i =0;
            while (i<num_workers) {
                wordcount.createWorker();
                System.out.println("Worker " + i + " Started");
                i++;
            }
            while(flag.get() != 2) {
                if(flag.get()==1) {
                    flag.set(0);
                    wordcount.createWorker();
                }

            }
            System.out.println("GG MainWorkerThread");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}