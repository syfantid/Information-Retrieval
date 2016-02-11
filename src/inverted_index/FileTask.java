package inverted_index;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.concurrent.BlockingQueue;

/**
 * Reads file line by line and loads it to memory to be processed
 */
public class FileTask implements Runnable {

    private final BlockingQueue<String> queue;
    private final String path;

    /**
     * Class constructor
     * @param queue Gets the available queue of lines at the moment the object is created
     * @param filename The name of the file to be processed
     */
    public FileTask(BlockingQueue<String> queue, String filename) {
        this.queue = queue;
        this.path = "data2//" + filename;
    }

    @Override
    public void run() {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(path));
            String line;
            while ((line = reader.readLine()) != null) {
                // Blocks if the queue is full
                try {
                    queue.put(line);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (reader != null) {
                    reader.close(); // Close file
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
