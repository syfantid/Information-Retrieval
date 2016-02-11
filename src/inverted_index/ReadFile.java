package inverted_index;

import java.io.*;
import java.util.HashMap;
import java.util.concurrent.*;

/**
 * Loads file to memory and processes it
 * Created by Sofia on 2/10/2016.
 */
public class ReadFile {

    // Thread Safe HashMap that contains the ni values for each term (inverted index level)
    private static ConcurrentHashMap<String,Integer> n_is = new ConcurrentHashMap<>();
    // Thread Safe HashMap that contains the frequencies for each term for each document (document level)
    private static ConcurrentHashMap<String,Integer> frequencies;
    private static int maxFreq; // The maximum term frequency of the document (document level)
    private static float Ld; // The length of the document vector (to be used for normalization)
    private static ConcurrentHashMap<String,Object> locks = new ConcurrentHashMap<>();
    private static ConcurrentHashMap<String,Object> locks2 = new ConcurrentHashMap<>();


    // Only one thread can have access to the critical area
    public static void incrementNis(String term) {
        locks2.putIfAbsent(term,new Object());
        synchronized (locks2.get(term)) {
            if (n_is.containsKey(term)) {
                int old = n_is.get(term);
                n_is.put(term, old + 1); // Increment n_i
            } else {
                n_is.put(term, 1); // Put the term in the list
            }
        }
    }

    public static void incrementFrequencies(String term) {
        locks.putIfAbsent(term, new Object()); // Initialize lock for the term
        synchronized (locks.get(term)) {
            //System.out.println("Term locked: " + term + "\n");
            if (frequencies.containsKey(term)) {
                int newFrequency = frequencies.get(term) + 1;
                frequencies.put(term, newFrequency); // Increment n_i
                if (newFrequency > maxFreq) {
                    maxFreq = newFrequency;
                }
            } else {
                frequencies.put(term, 1); // Put the term in the list
                incrementNis(term);
            }
            //System.out.println("Term unlocked: " + term + "\n");
        }
    }

    private static void writeToFile(String term, int docID, int freq) {
        String path = "index//" + term + ".txt";
        try {
            FileWriter fw = new FileWriter(path,true); // True for appending the data
            fw.write(docID + "," + freq + " "); // Appends the docID and frequency to the term file
            fw.close();
        } catch(IOException e) {
        }
    }

    private static void writeToDocFile(int docID, float Ld) {
        String path = "documents.txt";
        try {
            FileWriter fw = new FileWriter(path,true);
            fw.write(docID + " " + maxFreq + " " + Ld + "\n");
            fw.close();
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    private static void deleteFiles(File dir) {
        for (File file: dir.listFiles()) {
            file.delete();
        }
    }

    public static void main(String[] args) {

        final int threadCount; // Number of threads available
        if (args.length > 0) {
            threadCount = Integer.parseInt(args[0]);
        } else {
            throw new IllegalArgumentException("Please pass the available number of threads as an argument.");
        }

        deleteFiles(new File("index//")); // Cleaning up the index folder before we create a new index
        File f = new File("documents.txt");
        f.delete(); // Delete if it exists already

        int id = 1;
        String filename = "1.txt";

        long startTime = System.currentTimeMillis(); // Start time of the inversion
        System.out.println("Starting Inversion Process ...");

        while (new File("data//" + filename).exists()) { // While there are more files to be processed
            // Thread safe BlockingQueue with a capacity of 200 lines; Used to load file to memory
            BlockingQueue<String> queue = new ArrayBlockingQueue<>(200);
            frequencies = new ConcurrentHashMap<>();
            maxFreq = 1;

            // Create thread pool with the given size
            ExecutorService service = Executors.newFixedThreadPool(threadCount);

            // Give each thread a processing task
            for (int i = 0; i < (threadCount - 1); i++) {
                service.submit(new CPUTask(queue,frequencies));
            }

            /* Wait till FileTask (reading the file) completes; We use only one thread for reading the file, because with
            multiple threads you're going to have the threads causing multiple seeks as each gains control of the disk head,
            thus you won't speedup the whole process as multiple seeks cannot happen simultaneously.
            */
            try {
                service.submit(new FileTask(queue, filename)).get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }

            service.shutdownNow();  // Interrupt CPUTasks

            // Wait till CPUTasks terminate
            try {
                service.awaitTermination(365, TimeUnit.DAYS); // Maximum termination time, in case they don't terminate
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            // End of document processing; Produce results
            // Update the term lists
            for(String term : frequencies.keySet()) {
                writeToFile(term, id, frequencies.get(term));
            }

            // Update the document "statistics" file
            float Ld = 0; // The length of the document vector
            // TODO: 2/10/2016  Calculate the document vector
            writeToDocFile(id,Ld);

            // Next document
            id++;
            filename = id + ".txt";
        }

        // Append IDFs to term files
        for(String term : n_is.keySet()) {
            writeToFile(term, (id-1)/n_is.get(term), 0); // 0 is dummy
        }

        System.out.println("Ending Inversion Process ...");
        long elapsedTime = System.currentTimeMillis() - startTime; // The elapsed time
        System.out.println("Inversion duration is seconds: " + elapsedTime/1000 + "\n");
    }
}
