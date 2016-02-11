package queries;

import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by sakris on 2/11/2016.
 */
public class ProcessTerm implements Runnable {

    String term;
    ConcurrentHashMap<Integer, Double> accumulator;

    public ProcessTerm(String term, ConcurrentHashMap<Integer, Double> accumulator) {
        this.term = term;
        this.accumulator = accumulator;

    }

    @Override
    public void run() {
        ReadFile rf = new ReadFile(this.term);
        try {
            HashMap<Integer, Integer> hm = rf.getFile();
            //Test case compute TF x IDF after
            for (Map.Entry<Integer, Integer> entry : hm.entrySet()) {
                if (accumulator.containsKey(entry.getKey())) {
                    accumulator.put(entry.getKey(), accumulator.get(entry.getKey()) + entry.getValue());
                } else {

                    accumulator.put(entry.getKey(), (double) entry.getValue());
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }

}
