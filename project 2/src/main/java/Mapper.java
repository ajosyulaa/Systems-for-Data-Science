import java.io.*;
import java.net.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.ConcurrentHashMap;

class Mapper extends Thread {
    Socket sub_socket;
    int pid;
    ConcurrentHashMap<Integer,String> filenames;
    AtomicInteger HeartBeat_Mapper;

    public Mapper(Socket sub_socket, int pid,ConcurrentHashMap<Integer,String> filenames, AtomicInteger HeartBeat_Mapper) {
        this.sub_socket = sub_socket;
        this.pid = pid;
        this.filenames = filenames;
        this.HeartBeat_Mapper = HeartBeat_Mapper;
    }

    public void run() {
        //input and output to stream
        try {
            System.out.println("Mapper Thread Started");
            // PrintStream ps = new PrintStream(sub_socket.getOutputStream());
            // ps.println("PID " + pid);
            while(true) {
                
                if (HeartBeat_Mapper.get()==1) {
                    String s = filenames.get(0);    
                    
                    String dir_name = s.substring(0, s.length()-4) + "_out.txt";                                       
                    File dir = new File(dir_name);
                    dir.createNewFile();

                    //input and output to file
                    FileWriter fw = new FileWriter(dir, true);
                    BufferedWriter bw = new BufferedWriter(fw);
                    PrintWriter pw = new PrintWriter(bw);

                    BufferedReader br2 = new BufferedReader(new FileReader(s));
                    String line ;
                    while((line = br2.readLine())!=null) {
                        String[] words = line.split(" ");
                        for (String word:words){
                            if(word.length()>0)
                                pw.println(word + " 1");
                        }
                    }

                    pw.close();
                    bw.close();
                    fw.close();
                    br2.close();
                    
                    filenames.put(1, dir_name);
                    HeartBeat_Mapper.set(0);

                    
                }
            }
        } catch(IOException e) {
            e.printStackTrace();
        }
    }
}