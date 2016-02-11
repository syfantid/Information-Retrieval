package inverted_index;

import java.util.concurrent.BlockingQueue;

/**
 * The processing of a file line
 */
public class CPUTask implements Runnable {

    private final BlockingQueue<String> queue; // The queue that contains the lines of the document

    /**
     * Class constructor
     * @param queue Gets the available queue of lines at the moment the object is created
     */
    public CPUTask(BlockingQueue<String> queue) {
        this.queue = queue;
    }

    @Override
    public void run() {
        String line;
        String[] words;
        while(true) {
            try {
                // Block if the queue is empty
                line = queue.take();
                // Removes all punctuation and non-letters and converts to lower case
                line = line.replaceAll("\\p{Punct}+"," ").toLowerCase();
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
            // Removes all punctuation and non-letters and converts to lower case
            line = line.replaceAll("\\p{Punct}+"," ").toLowerCase();
            words = line.split(" ");
            for (String word : words) {
                if (word.length() > 1) { // We consider 1-letter words to be stopwords e.g.a,s (from 's) etc.
                    ReadFile.incrementFrequencies(word);
                }
            }
        }
    }
}
