package queries;

import com.sun.org.apache.xpath.internal.SourceTree;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.*;

/**
 * Created by sakris on 2/10/2016.
 */
public class ReadQueries {
    public static void main(String args[]) throws Exception {
        String path = "test/test.txt";
        int threadCount = 4;

        BufferedReader br = null;
        ArrayList<Query> queries;
        ArrayList<String> terms;

        if (new File(path).exists()) {
            queries = new ArrayList<>();
            br = new BufferedReader(new FileReader(path));
            int no_of_queries = Integer.parseInt(br.readLine());
            String line;
            while ((line = br.readLine()) != null) {
                String[] splitted = line.split("\\t");
                if (splitted.length < 3) {
                    throw new Exception("The queries file format isn't correct. Each row must be separated with tab");
                }
                String[] words = splitted[2].split(" ");
                terms = new ArrayList<>();
                for (String s : words) {
                    terms.add(s.toLowerCase());//all files are with lowercase titles
                }
                queries.add(new Query(Integer.parseInt(splitted[0]), Integer.parseInt(splitted[1]), terms));
            }
            br.close();

            /*System.out.println("Number of queries:" + no_of_queries);
            for(Query term:queries)
            {
                System.out.println("ID: " + term.getId());
                System.out.println("TOP_K: " + term.getTop_k());
                System.out.println("Terms: " + term.getTerms().toString());
            }*/


            //System.out.println("Words:"+ words.toString());

            //Hashmap with frequencies
            HashMap<Integer,Double> freqs= Frequencies.getFreq();
            ArrayList<ConcurrentHashMap<Integer, Double>> listAcc = new ArrayList<>();
            ExecutorService executor = Executors.newFixedThreadPool(threadCount);
            for (int i = 0; i < queries.size(); i++) {

                listAcc.add(i, new ConcurrentHashMap<>());
                for (String s : queries.get(i).getTerms()) {
                    executor.submit(new ProcessTerm(s, listAcc.get(i),freqs));
                }
            }
            executor.shutdown();

            executor.awaitTermination(1, TimeUnit.DAYS);
            for (int i = 0; i < no_of_queries; i++) {
                System.out.println(listAcc.get(i).toString());
            }

            /*String testTerm= "test";
            ReadFile test = new ReadFile(testTerm);
            HashMap<Integer,Integer> hashTest = test.getFile();
            System.out.println(hashTest.toString());*/
        } else {
            throw new FileNotFoundException("The queries with the file not found. Please Specify the file path again");

        }


    }
}
