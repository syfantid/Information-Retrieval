package queries;

import java.io.*;
import java.util.HashMap;

/**
 * Reads a Term file and stores its content into a HashMap
 */
public class ReadFile {

    String dPath = "index/";
    BufferedReader br;
    String termPath;
    double IDF;


    /**
     * Just creates the path to the term file
     * @param term the name of the term- the word
     */
    public ReadFile(String term) {
        this.termPath = dPath + term + ".txt";
        System.out.println("Term: " + this.termPath);
    }

    /**
     * Getter of the IDF
     * @return the IDF of a term.
     */
    public double getIDF() {
        return IDF;
    }

    /**
     * Gets the contents of the term file. It stores the ID of the doc and the frequency of the term in the doc in a
     * hashmap. Also it gets the idf of the term.
     * @return The hashmap that contains the ID of the document and the frequency of the term in this document
     * @throws FileNotFoundException
     */

    public HashMap<Integer, Integer> getFile() throws FileNotFoundException {

        if (new File(this.termPath).exists()) {//check if file exists
            HashMap<Integer, Integer> docFreq = new HashMap<>();
            this.br = new BufferedReader(new FileReader(this.termPath));
            String line;
            try {
                while ((line = br.readLine()) != null) {
                    String[] splitted = line.split(" ");//Splits to tuples of (DOC_ID,FREQUENCY)
                    for (int i = 0; i < splitted.length - 1; i++) {
                        String[] commaSplit = splitted[i].split(",");//split the DOC_ID and FREQUENCY to be stored in
                        //HashMap
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
