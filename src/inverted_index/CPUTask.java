package inverted_index;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The processing of a file line
 * Created by Sofia on 2/10/2016.
 */
public class CPUTask implements Runnable {

    private final BlockingQueue<String> queue;
    private ConcurrentHashMap<String,Integer> frequencies; // Contains the frequencies of each term in the document

    /**
     * Class constructor
     * @param queue Gets the available queue of lines at the moment the object is created
     */
    public CPUTask(BlockingQueue<String> queue, ConcurrentHashMap<String,Integer> frequencies) {
        this.queue = queue;
        this.frequencies = frequencies;
    }

    @Override
    public void run() {
        String line;
        String[] words;
        while(true) {
            try {
                // Block if the queue is empty
                line = queue.take();
                line = line.replaceAll("\\p{Punct}+"," ").toLowerCase(); // Removes all punctuation, converts to lower
                words = line.split(" ");
                for (String word : words) {
                    if (word.length() > 1) { // We consider 1-letter words to be stopwords e.g.a,s (from 's) etc.
                        ReadFile.incrementFrequencies(word);
                    }
                }
            } catch (InterruptedException ex) {
                break; // FileTask has completed
            }
        }
        // If a thread is interrupted we have to empty the queue
        // poll() returns null if the queue is empty
        while((line = queue.poll()) != null) {
            line = line.replaceAll("\\p{Punct}+"," ").toLowerCase(); // Removes all punctuation
            words = line.split(" ");
            for (String word : words) {
                if (word.length() > 1) { // We consider 1-letter words to be stopwords e.g.a,s (from 's) etc.
                    ReadFile.incrementFrequencies(word);
                }
            }
        }
    }
}
