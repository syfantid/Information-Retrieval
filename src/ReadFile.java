import java.io.File;
import java.util.concurrent.*;

/**
 * Loads file to memory and processes it
 * Created by Sofia on 2/10/2016.
 */
public class ReadFile {
    public static void main(String[] args) {

        final int threadCount; // Number of threads available
        if (args.length > 0) {
            threadCount = Integer.parseInt(args[0]);
        } else {
            throw new IllegalArgumentException("Please pass the available number of threads as an argument.");
        }

        int id = 1;
        String filename = "1.txt";
        while (new File("\\data", filename).exists()) { // While there are more files to be processed
            // Thread safe BlockingQueue with a capacity of 200 lines; Used to load file to memory
            BlockingQueue<String> queue = new ArrayBlockingQueue<>(200);

            // Create thread pool with the given size
            ExecutorService service = Executors.newFixedThreadPool(threadCount);

            // Give each thread a processing task
            for (int i = 0; i < (threadCount - 1); i++) {
                service.submit(new CPUTask(queue));
            }

            /* Wait till FileTask (reading the file) completes; We use only one thread for reading the file, because with
            multiple threads you're going to have the threads causing multiple seeks as each gains control of the disk head,
            thus you won't speedup the whole process as multiple seeks cannot happen simultaneously.
            */
            try {
                service.submit(new FileTask(queue, "1.txt")).get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }

            service.shutdownNow();  // Interrupt CPUTasks

            // Wait till CPUTasks terminate
            try {
                service.awaitTermination(365, TimeUnit.DAYS); // Maximum termination time, in case they don't terminate
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            id++;
            filename = id + ".txt";
        }
    }
}
