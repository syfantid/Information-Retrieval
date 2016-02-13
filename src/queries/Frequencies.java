package queries;

import java.io.*;
import java.util.HashMap;


/**
 * Gets the max frequency of each document and the LD, that are already saved in documents.txt, with a static method
 */
public class Frequencies {


    static HashMap<Integer,Freq> getFreq() {
        String path="documents.txt";
        HashMap<Integer,Freq> freqs = new HashMap<>(); // Hashmap to store the ID of the document and its frequency
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
                        freqs.put(Integer.parseInt(splitted[0]), new Freq(Double.parseDouble(splitted[1]),
                                Double.parseDouble(splitted[2])));
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

            return freqs;

        }

        return null;



    }
}
