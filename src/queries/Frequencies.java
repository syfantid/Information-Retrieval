package queries;

import java.io.*;
import java.util.HashMap;


/**
 * Gets the max frequency of each document already saved in documents.txt with a static method
 */
public class Frequencies {


    static HashMap<Integer,Double> getFreq() {
        String path="documents.txt";
        HashMap<Integer,Double> freq = new HashMap<>(); // Hashmap to store the ID of the document and its frequency
        if (new File(path).exists()) // check if file exists
        {
            BufferedReader br= null;
            try {
                br = new BufferedReader(new FileReader(path));
                String line;
                try {
                    while((line= br.readLine())!=null)
                    {
                        String[] splitted = line.split(" "); // split the line via space
                        freq.put(Integer.parseInt(splitted[0]), Double.parseDouble(splitted[1]));
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                br.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return freq;

        }

        return null;



    }
}
