package queries;

import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


/**
 * The processing of a query's term. Each thread runs this
 */
public class ProcessTerm implements Runnable {

    String term;
    ConcurrentHashMap<Integer, Double> accumulator;
    HashMap<Integer, Freq> freqs;

    /**
     * Constructor of the runnable methos that is the task of a thread
     * @param term the term name (word)
     * @param accumulator Collection of accumulators
     * @param freqs Max frequencies of each doc
     */
    public ProcessTerm(String term, ConcurrentHashMap<Integer, Double> accumulator, HashMap<Integer, Freq> freqs) {
        this.term = term;
        this.accumulator = accumulator;
        this.freqs = freqs;
    }

    /**
     * Run method, it runs every time a thread is starts and it computes the score of the term for every document.
     */
    @Override
    public void run() {
        ReadFile rf = new ReadFile(this.term);
        try {
            HashMap<Integer, Integer> hm = rf.getFile(); // Getting the information about the term from the file
            // for each entry of the file that represents the DocID and the frequency of the term
            for (Map.Entry<Integer, Integer> entry : hm.entrySet()) {
                Double tf = entry.getValue() / freqs.get(entry.getKey()).getMaxFreq(); // Computes TF
                Double idf = rf.getIDF(); // Gets the IDF that is stored in the ReadFile Object
                Double TF_IDF = (tf * idf); // The TF_IDF
                // Compute is perfectly atomic in Java 8
                accumulator.compute(entry.getKey(), (k, v) -> (v == null) ? TF_IDF/freqs.get(entry.getKey()).getLd()
                        : v + TF_IDF/freqs.get(entry.getKey()).getLd());
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }

}
