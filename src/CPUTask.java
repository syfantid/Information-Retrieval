import java.util.concurrent.BlockingQueue;

/**
 * Created by Sofia on 2/10/2016.
 */
public class CPUTask implements Runnable {

    private final BlockingQueue<String> queue;

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
        while(true) {
            try {
                // block if the queue is empty
                line = queue.take();
                // do things with line
            } catch (InterruptedException ex) {
                break; // FileTask has completed
            }
        }
        // poll() returns null if the queue is empty
        while((line = queue.poll()) != null) {
            // do things with line;
        }
    }
}
