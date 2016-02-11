package queries;

import java.io.*;
import java.util.HashMap;

/**
 * Created by sakris on 2/11/2016.
 */
public class ReadFile {

    String dPath = "index/";
    BufferedReader br;
    String termPath;
    double IDF;

    public ReadFile(String term) {
        this.termPath = dPath + term + ".txt";
        System.out.println("Term: " + this.termPath);
    }

    public double getIDF() {
        return IDF;
    }

    public HashMap<Integer, Integer> getFile() throws FileNotFoundException {

        if (new File(this.termPath).exists()) {
            HashMap<Integer, Integer> docFreq = new HashMap<>();
            this.br = new BufferedReader(new FileReader(this.termPath));
            String line;
            try {
                while ((line = br.readLine()) != null) {
                    String[] splitted = line.split(" ");
                    for (int i = 0; i < splitted.length - 1; i++) {
                        String[] commaSplit = splitted[i].split(",");
                        docFreq.put(Integer.parseInt(commaSplit[0]), Integer.parseInt(commaSplit[1]));
                    }
                    this.IDF = Double.parseDouble(splitted[splitted.length - 1]);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return docFreq;

        } else {
            return null;
        }
    }
}
