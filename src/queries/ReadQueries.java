package queries;

import java.io.*;
import java.util.ArrayList;

/**
 * Created by sakris on 2/10/2016.
 */
public class ReadQueries {
    public static void main(String args[]) throws Exception {
        String path= "data/queries.txt";

        BufferedReader br= null;
        ArrayList<Query> queries;
        ArrayList<String> terms;

        if (new File(path).exists())
        {
            queries= new ArrayList<>();
            terms= new ArrayList<>();
            br = new BufferedReader(new FileReader(path));
            int no_of_queries = Integer.parseInt(br.readLine());
            String line;
            while((line = br.readLine())!=null)
            {
                String[] splitted= line.split("\\t");
                if (splitted.length<3)
                {
                    throw new Exception("The queries file format isn't correct. Each row must be separated with tab");
                }
                String[] words= splitted[2].split(" ");
                terms= new ArrayList<>();
                for (String s:words) {
                    terms.add(s);
                }
                queries.add(new Query(Integer.parseInt(splitted[0]),Integer.parseInt(splitted[1]),terms));
            }

            System.out.println("Number of queries:" + no_of_queries);
            for(Query term:queries)
            {
                System.out.println("ID: " + term.getId());
                System.out.println("TOP_K: " + term.getTop_k());
                System.out.println("Terms: " + term.getTerms().toString());
            }
        }
        else
        {
            throw new FileNotFoundException("The queries with the file not found. Please Specify the file path again");

        }
    }
}
