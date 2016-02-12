package queries;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;


/**
 * Gets the max frequency of each document already saved in documents.txt with a static method
 */
public class Frequencies {


    static HashMap<Integer,Double> getFreq() throws IOException {
        String path="documents.txt";
        HashMap<Integer,Double> freq = new HashMap<>(); //Hashmap to store the ID of the document and its frequency
        if (new File(path).exists())//checki if file exists
        {
            BufferedReader br= new BufferedReader(new FileReader(path));
            String line;
            try {
                while((line= br.readLine())!=null)
                {
                    String[] splitted = line.split(" ");//split the line via space
                    freq.put(Integer.parseInt(splitted[0]), Double.parseDouble(splitted[1]));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            br.close();
            return freq;

        }

        return null;



    }
}
