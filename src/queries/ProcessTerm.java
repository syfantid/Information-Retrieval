package queries;

import com.sun.org.apache.xpath.internal.SourceTree;

import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ProcessTerm implements Runnable {

    String term;
    ConcurrentHashMap<Integer, Double> accumulator;
    HashMap<Integer,Double> freq;

    public ProcessTerm(String term, ConcurrentHashMap<Integer, Double> accumulator, HashMap<Integer,Double> freq) {
        this.term = term;
        this.accumulator = accumulator;
        this.freq=freq;


    }

    @Override
    public void run() {
        ReadFile rf = new ReadFile(this.term);
        try {
            HashMap<Integer, Integer> hm = rf.getFile();
            //Test case compute TF x IDF after
            for (Map.Entry<Integer, Integer> entry : hm.entrySet()) {
                Double tf= entry.getValue()/freq.get(entry.getKey());
                Double idf = rf.getIDF();
                Double TF_IDF= (tf*idf);
                if (accumulator.containsKey(entry.getKey())) {
                    accumulator.put(entry.getKey(), accumulator.get(entry.getKey()) + TF_IDF);
                } else {

                    accumulator.put(entry.getKey(), TF_IDF);
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }

}
