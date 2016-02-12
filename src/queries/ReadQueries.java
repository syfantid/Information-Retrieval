package queries;


import java.io.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

/**
 * Main class that reads the queries and prints the top k document ids with their score
 */
public class ReadQueries {
    public static void main(String args[]) throws Exception {
        String path = "test/test.txt";//Path of the query file
        int threadCount = 4; //Number of threads can be added to arguments
        BufferedReader br = null;
        ArrayList<Query> queries;//Arraylist containing all the Query objects
        ArrayList<String> terms;//Arraylist containg the terms of a Query
        long startTime = System.currentTimeMillis(); // Start time of the queries
        System.out.println("Starting query process");
        if (new File(path).exists()) { //checks if file exists
            queries = new ArrayList<>();
            br = new BufferedReader(new FileReader(path));
            int no_of_queries=Integer.parseInt(br.readLine());
            String line;
            while ((line = br.readLine()) != null) {

                String[] splitted = line.split("\\t"); //Splits the query to ID, TOK K value and Terms
                if (splitted.length < 3) {
                    throw new Exception("The queries file format isn't correct. Each row must be separated with tab");
                }
                String[] words = splitted[2].split(" ");//Splits the terms of the query
                terms = new ArrayList<>();
                for (String s : words) {
                    terms.add(s.toLowerCase());//Stores the query terms to an ArraList
                }
                //Creates and stores the Query object of the read query
                queries.add(new Query(Integer.parseInt(splitted[0]), Integer.parseInt(splitted[1]), terms));
            }
            br.close();


            //HashMap with the max frequencies of each document
            HashMap<Integer, Double> freqs = Frequencies.getFreq();

            //Arraylist with ConcurrentHashMap (thread safe). Every hashmap represents a collection of accumulators
            ArrayList<ConcurrentHashMap<Integer, Double>> listAcc = new ArrayList<>();

            /*
            Threadpool that handles the thread. Each thread handles a term (not a query) and its file. The score of each
            document for a specific term is computed in ProcessTerm
             */
            ExecutorService executor = Executors.newFixedThreadPool(threadCount);
            for (int i = 0; i < queries.size(); i++) {
                listAcc.add(i, new ConcurrentHashMap<>());//Adds a new collection of accumulators
                for (String s : queries.get(i).getTerms()) {
                    executor.submit(new ProcessTerm(s, listAcc.get(i), freqs));//every term a thread that starts here
                }
            }

            //Shutdown the thread
            executor.shutdown();

            //Wait till the thread ends with a maximum of 1 Day
            executor.awaitTermination(1, TimeUnit.DAYS);

            //Prints the top k results (Documents IDs) of the each query
            for (int i = 0; i < queries.size(); i++) {
                Query q = queries.get(i);
                System.out.println("\n\nQuery: " + (i + 1) + "\nTOP " + q.getTop_k() + " Documents");
                //Java 8 Streams - Gets every query's collection of accumulators and stores to a list the top_k Doc ID's
                //with the highest score
                List<Map.Entry<Integer, Double>> list = listAcc.get(i).entrySet()
                        .stream()
                        .sorted(Comparator.comparing(Map.Entry::getValue, Comparator.reverseOrder()))
                        .limit(q.getTop_k())
                        .collect(Collectors.toList());
                System.out.println(list.toString());
            }

            System.out.println("Ending Query Process ...");
            Long elapsedTime = System.currentTimeMillis() - startTime; // The elapsed time
            System.out.println("Inversion duration is seconds: " + elapsedTime/1000.00 + "\n");

        } else {
            throw new FileNotFoundException("The queries with the file not found. Please Specify the file path again");

        }


    }
}
