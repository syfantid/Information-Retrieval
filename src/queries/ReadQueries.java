package queries;


import java.io.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

/**
 * Main class that reads the queries and prints the top k document ids with their score
 */
public class ReadQueries {
    public static void start(int threads,String queriesPath) {
        String path = queriesPath; // Path of the query file
        int threadCount = threads; // Number of threads can be added to arguments
        BufferedReader br;
        ArrayList<Query> queries; // Arraylist containing all the Query objects
        ArrayList<String> terms; // Arraylist containing the terms of a Query

        long startTime = System.currentTimeMillis(); // Start time of the queries
        System.out.println("Starting query process");

        if (new File(path).exists()) { // Checks if file exists
            queries = new ArrayList<>();
            try {
                br = new BufferedReader(new FileReader(path));
                String line;
                br.readLine(); // The number of queries; not needed for our approach
                while ((line = br.readLine()) != null) {
                    String[] splitted = line.split("\\t"); // Splits the query to ID, TOP K value and Terms
                    if (splitted.length < 3) {
                        throw new Exception("The queries file format isn't correct. Each row must be separated with tab");
                    }
                    String[] words = splitted[2].split(" "); // Splits the terms of the query
                    terms = new ArrayList<>();
                    for (String s : words) {
                        terms.add(s.toLowerCase()); // Stores the query terms to an ArrayList
                    }
                    // Creates and stores the Query object of the read query
                    queries.add(new Query(Integer.parseInt(splitted[0]), Integer.parseInt(splitted[1]), terms));
                }
                br.close();

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }


            // HashMap with the max frequencies of each document
            HashMap<Integer, Double> freqs = Frequencies.getFreq();

            // Arraylist with ConcurrentHashMap (thread safe). Every hashmap represents a collection of accumulators
            ArrayList<ConcurrentHashMap<Integer, Double>> listAcc = new ArrayList<>();

            /*
            Thread-pool that handles the thread. Each thread handles a term (not a query) and its file. The score of
            each document for a specific term is computed in ProcessTerm using multiple threads
             */
            ExecutorService executor = Executors.newFixedThreadPool(threadCount);
            for (int i = 0; i < queries.size(); i++) {
                listAcc.add(i, new ConcurrentHashMap<>()); // Adds a new collection of accumulators (one for each query)
                for (String s : queries.get(i).getTerms()) {
                    executor.submit(new ProcessTerm(s, listAcc.get(i), freqs)); // Thread for each term starts here
                }
            }

            // Shutdown the thread
            executor.shutdown();

            // Wait till the thread ends with a maximum of 1 Day
            try {
                executor.awaitTermination(1, TimeUnit.DAYS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            // Prints the top k results (Documents IDs) of the each query
            for (int i = 0; i < queries.size(); i++) {
                Query q = queries.get(i);
                System.out.println("\n\nQuery: " + (i + 1) + "\nTOP " + q.getTop_k() + " Documents");
                // Java 8 Streams - Gets every query's collection of accumulators and stores to a list the top_k Doc
                // ID's with the highest score
                List<Map.Entry<Integer, Double>> list = listAcc.get(i).entrySet()
                        .stream()
                        .sorted(Comparator.comparing(Map.Entry::getValue, Comparator.reverseOrder()))
                        .limit(q.getTop_k())
                        .collect(Collectors.toList());
                System.out.println(list.toString());
            }

            System.out.println("Ending Query Process ...");
            Long elapsedTime = System.currentTimeMillis() - startTime; // The elapsed time
            System.out.println("Queries duration is seconds: " + elapsedTime/1000.00 + "\n");

        } else {
            System.out.println("The queries' file not found. Please Specify the file path again");

        }


    }
}
