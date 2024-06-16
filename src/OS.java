import java.util.concurrent.locks.ReentrantLock; 
import java.util.concurrent.locks.Condition; 
import java.util.concurrent.locks.Lock; 
import java.util.LinkedList; 
import java.util.Queue; 

public class OS implements OS_sim_interface { 

    private int nxtPid = 0; // Variable to store the next process ID
    private final Lock lock = new ReentrantLock(); // Lock object for synchronization
    private int readyPrcrs; // Variable to store the number of available processors
    private final Queue<Integer> prcsQueue = new LinkedList<>(); // Queue to store process IDs waiting to execute
    private final Condition readyPrcCond = lock.newCondition(); // Condition for signaling when processors are available

    @Override
    public void set_number_of_processors(int nProcessors) { 
        lock.lock(); 
        try {
            this.readyPrcrs = nProcessors; // Setting the number of available processors
        } finally {
            lock.unlock(); 
        }
    }

    @Override
    public int reg(int priority) { 
        lock.lock(); 
        try {
            int currentPid = nxtPid; // Getting the current process ID
            nxtPid++; // Incrementing the process ID for the next registration
            return currentPid; // Returning the current process ID
        } finally {
            lock.unlock(); 
        }
    }

    @Override
    public void start(int ID) { 
        lock.lock(); 
        try {
            if (readyPrcrs <= 0) { // If no processors are available
                prcsQueue.add(ID); // Add the process to the queue
                while (prcsQueue.peek() != ID || readyPrcrs <= 0) { // Wait until it's its turn and processors are available
                    readyPrcCond.await(); // Wait for a signal indicating availability of processors
                }
                prcsQueue.remove(); // Remove the process from the queue once it starts
            }
            if (readyPrcrs > 0) { // If processors are available
                readyPrcrs--; // Decrement the count of available processors
            }
        } catch (InterruptedException e) { // Handling interruption exceptions
            Thread.currentThread().interrupt(); // Interrupting the thread
        } finally {
            lock.unlock(); 
        }
    }

    @Override
    public void schedule(int ID) {
        lock.lock(); 
        try {
            readyPrcrs++; // Increment the count of available processors
            prcsQueue.add(ID); // Add the process to the queue
            readyPrcCond.signal(); // Signal that a processor is available
            while (prcsQueue.peek() != ID || readyPrcrs <= 0) { // Wait until it's its turn and processors are available
                readyPrcCond.await(); // Wait for a signal indicating availability of processors
            }
            prcsQueue.remove(); // Remove the process from the queue once it starts
            readyPrcrs--; // Decrement the count of available processors
        } catch (InterruptedException e) { // Handling interruption exceptions
            Thread.currentThread().interrupt(); // Interrupting the thread
        } finally {
            lock.unlock(); 
        }
    }

    @Override
    public void terminate(int ID) { 
        lock.lock(); 
        try {
            readyPrcrs++; // Increment the count of available processors
            readyPrcCond.signal(); // Signal that a processor is available
        } finally {
            lock.unlock(); 
        }
    }

    public int getAvailableProcessors() { //Helper function
        return readyPrcrs; // Returns the count of available processors
    }
}
