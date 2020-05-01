import java.io.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

//import javax.jws.soap.SOAPBinding.Style;

import java.util.*;
import java.util.concurrent.TimeUnit;


public class WordCount implements Master {
    int num_workers;
    String[] input_filenames;
    Collection<Process> active_processes;
    AtomicInteger flag;
    ConcurrentHashMap<String, String> input_output;
    HashMap<String, Integer> wordcount_sorted = new LinkedHashMap<String, Integer>();
    PrintStream out;

    public WordCount(int workerNum, String[] filenames) throws IOException {
        num_workers = workerNum;
        input_filenames = filenames;
        active_processes = new LinkedList<Process>();
        flag = new AtomicInteger(0);
        input_output = new ConcurrentHashMap<>();
        // out = new PrintStream(new File("results.txt"));
    }

    public void setOutputStream(PrintStream out) {
        this.out = out;
    }

    public static void main(String[] args) throws Exception {
        System.out.println("main() wordCount Started");
        String[] filenames = {"C:/Users/EE10B/Desktop/project-2-group-9-master/project-2-group-9-master/src/test/resources/simple.txt","C:/Users/EE10B/Desktop/project-2-group-9-master/project-2-group-9-master/src/test/resources/random.txt" ,"C:/Users/EE10B/Desktop/project-2-group-9-master/project-2-group-9-master/src/test/resources/war-and-peace.txt", "C:/Users/EE10B/Desktop/project-2-group-9-master/project-2-group-9-master/src/test/resources/king-james-version-bible.txt"}; 
        WordCount wordcount;
        wordcount = new WordCount(4, filenames);
        wordcount.run();
    }

    public void reduce() {

        Map<String, Integer> wordcount_map = new HashMap<String, Integer>();
        for (String value: input_output.values()) {
            // System.out.println(value);
            try {
            BufferedReader br = new BufferedReader(new FileReader(value));
            String line= br.readLine();
            
            while(line != null) {
                // System.out.println(line);
                String[] words = line.split(" ");
                if(wordcount_map.containsKey(words[0]))
                    wordcount_map.compute(words[0],(k,v) -> (v+1));
                else
                    wordcount_map.put(words[0],1);
                // System.out.println(words[0] +" "+ wordcount_map.get(words[0]));
                line = br.readLine();
            }
            br.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            } 
            
        }

        List<Map.Entry<String, Integer>> temp = new LinkedList<Map.Entry<String, Integer>>(wordcount_map.entrySet());
        Collections.sort(temp, new Comparator<Map.Entry<String, Integer>>() {
            public int compare(Map.Entry<String, Integer> a, Map.Entry<String, Integer> b) {
                // System.out.println(b.getValue() + " " + a.getValue());
                return (b.getValue().compareTo(a.getValue()));
            }
        });

        for(Map.Entry<String, Integer> temp2: temp) {
            //System.out.println(temp2.getKey() + " " + temp2.getValue());
            wordcount_sorted.put(temp2.getKey(), temp2.getValue());
            
        }

    }

    public void run() {
        
        MainMasterThread main_master_thread = new MainMasterThread(this);
        MainWorkerThread main_worker_thread = new MainWorkerThread(this);
        main_master_thread.start();
        try {
			TimeUnit.SECONDS.sleep(2);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
        main_worker_thread.start();
        while(true){
            if (flag.get() ==2)
                break;
        }
        System.out.println("after while in in wordcount");
        if(flag.get() == 2) {
            try {
            main_worker_thread.join();
            main_master_thread.join();
            } catch(InterruptedException e) {
                e.printStackTrace();
            }
        }
        reduce();
        
        for (String key: wordcount_sorted.keySet()) {
            //System.out.println(key + " " + wordcount_sorted.get(key));
            if (key!=null)
                out.println(wordcount_sorted.get(key) +" : "+ key);
            
        }
        // setOutputStream(out);
         
    }

    public Collection<Process> getActiveProcess() {
        return active_processes;
    }

    public void createWorker() throws IOException {
        //ClassLoader loader = Test.class.getClassLoader();
        /*ProcessBuilder pb = new ProcessBuilder("java", "WorkerThread");
        Process compile = pb.start();
        active_processes.add(compile);
        */

        String[] command = {"java", "-cp", WordCount.class.getProtectionDomain().getCodeSource().getLocation().getPath(),"WorkerThread"};
        ProcessBuilder pb = new ProcessBuilder(command);
        Process compile = pb.start();
        active_processes.add(compile);

        /*
        ClassLoader loader = Test.class.getClassLoader();
        System.out.println("in createWorker()");
    	ProcessBuilder compileBuilder = new ProcessBuilder("java", loader.getResource("WorkerThread.class").toString());
    	Process compile = compileBuilder.start();
        active_processes.add(compile);
        */
    }
}

