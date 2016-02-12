package inverted_index;

import java.io.*;
import java.util.concurrent.*;

/**
 * Loads file to memory, processes it and produces the Inverted Index files
 */
public class ReadFile {

    // Thread Safe HashMap that contains the ni values for each term (inverted index level)
    private static ConcurrentHashMap<String,Integer> n_is = new ConcurrentHashMap<>();
    // Thread Safe HashMap that contains the frequencies for each term for each document (document level)
    private static ConcurrentHashMap<String,Integer> frequencies;
    // The maximum term frequency of the document (document level)
    private static int maxFreq;
    // Locks used for synchronization; Each locks is assigned to a term (term-level lock)
    private static ConcurrentHashMap<String,Object> locks = new ConcurrentHashMap<>();

    /**
     * Increments the n_i of a term (atomically)
     * @param term The term, whose n_i is to be incremented
     */
    public static void incrementNis(String term) {
        // Only one thread can have access to the critical area on a term level
        // Different threads working on different terms can access n_is concurrently
        // Compute is perfectly atomic in Java 8
        n_is.compute(term, (k, v) -> (v == null) ? 1 : v + 1);
    }

    /**
     * Increments the TF of a term (atomically)
     * @param term The term, whose frequency is to be incremented
     */
    public static void incrementFrequencies(String term) {
        locks.putIfAbsent(term, new Object()); // Initialize lock for the term
        synchronized (locks.get(term)) {
            if (frequencies.containsKey(term)) {
                int newFrequency = frequencies.get(term) + 1;
                frequencies.put(term, newFrequency); // Increment frequency by 1
                if (newFrequency > maxFreq) { // Check if the frequency of the term is larger than the max doc frequency
                    maxFreq = newFrequency;
                }
            } else {
                frequencies.put(term, 1); // Put the term in the list
                incrementNis(term);
            }
        }
    }

    /**
     * Appends term's IDF to file
     * @param term The terms, whose IDF is to be appended
     * @param IDF The IDF value
     */
    private static void writeIDFToFile(String term, double IDF) {
        if(term.matches("[a-zA-Z]+")) { // Contains only letters
            String path = "index//" + term + ".txt";
            try {
                FileWriter fw = new FileWriter(path, true); // True for appending the data
                fw.write(String.valueOf(IDF)); // Appends the IDF to the term file
                fw.close();
            } catch (IOException e) {
                // Do nothing
            }
        }
    }

    /**
     * Appends (docID,frequency) to the frequencies' list (file format) of each term
     * @param term The tersm, whose (docID,frequency) is to be appended
     * @param docID The document ID where the term was found
     * @param freq The frequency of the term in document docID
     */
    private static void writeToFile(String term, int docID, int freq) {
        if(term.matches("[a-zA-Z]+")) { // Contains only letters
            String path = "index//" + term + ".txt";
            try {
                FileWriter fw = new FileWriter(path, true); // True for appending the data; Otherwise, creates file
                fw.write(docID + "," + freq + " "); // Appends the docID and frequency to the term file
                fw.close();
            } catch (IOException e) {
                //Do nothing
            }
        }
    }

    /**
     * Writes the max frequency of a document to a file
     * @param docID The document, whose maximum frequency is to be written
     * @param maxFreq The maximum frequency
     */
    private static void writeToDocFile(int docID, int maxFreq) {
        String path = "documents.txt";
        try {
            FileWriter fw = new FileWriter(path,true);
            fw.write(docID + " " + maxFreq + "\n");
            fw.close();
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Deletes all files from a directory
     * @param dir The directory
     */
    private static void deleteFiles(File dir) {
        for (File file: dir.listFiles()) {
            file.delete();
        }
    }

    /**
     * Creates a thread pool and synchronizes threads to produce results
     * @param args The first argument given is the number of threads
     */
    public static void main(String[] args) {

        final int threadCount; // Number of threads available
        if (args.length > 0) {
            threadCount = Integer.parseInt(args[0]);
        } else {
            throw new IllegalArgumentException("Please pass the available number of threads as an argument.");
        }

        // Create directory named index if it doesn't exist, where the II files will be saved
        File dir = new File("index");
        dir.mkdir();
        // Delete all previous contents of index if it already exists
        deleteFiles(new File("index//")); // Cleaning up the index folder before we create a new index
        //Delete the documents' statistics file if it already exists
        File f = new File("documents.txt"); // File to append document statistics (id,maxFreq,L_d)
        f.delete(); // Delete if it exists already

        int id = 1;
        String filename = "1.txt";

        long startTime = System.currentTimeMillis(); // Start time of the inversion
        System.out.println("Starting Inversion Process ...");

        while (new File("data//" + filename).exists()) { // While there are more files to be processed
            // Thread safe BlockingQueue with a capacity of 200 lines; Used to load file to memory
            BlockingQueue<String> queue = new ArrayBlockingQueue<>(200);
            frequencies = new ConcurrentHashMap<>(); // Contains the TFs for the terms pf this document
            maxFreq = 1;

            // Create thread pool with the given size
            ExecutorService service = Executors.newFixedThreadPool(threadCount);

            // Give each thread a processing task
            for (int i = 0; i < (threadCount - 1); i++) {
                service.submit(new CPUTask(queue));
            }

            /* Wait till FileTask (reading the file) completes; We use only one thread for reading the file, because
            with multiple threads you're going to have the threads causing multiple seeks as each gains control
            of the disk head, thus you won't speedup the whole process as multiple seeks cannot happen simultaneously.
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
            writeToDocFile(id,maxFreq);

            // Next document
            id++;
            filename = id + ".txt";
        }

        // Append IDFs to term files
        for(String term : n_is.keySet()) {
            writeIDFToFile(term, Math.log((id-1)/n_is.get(term)));
        }

        System.out.println("Ending Inversion Process ...");
        long elapsedTime = System.currentTimeMillis() - startTime; // The elapsed time
        System.out.println("Inversion duration is seconds: " + elapsedTime/1000 + "\n");
    }
}
