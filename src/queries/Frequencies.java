package queries;

import java.io.*;
import java.util.DoubleSummaryStatistics;
import java.util.HashMap;

public class Frequencies {


    static HashMap<Integer,Double> getFreq() throws IOException {
        String path="documents.txt";
        HashMap<Integer,Double> freq = new HashMap<>();
        if (new File(path).exists())
        {
            BufferedReader br= new BufferedReader(new FileReader(path));
            String line;
            try {
                while((line= br.readLine())!=null)
                {
                    String[] splitted = line.split(" ");
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
