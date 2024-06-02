package readerwriter;

import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ReaderWriter {
    public int reader =0;

    public Lock lock = new ReentrantLock();
    public Semaphore cs = new Semaphore(1);
    public Semaphore eligibleReader = new Semaphore(4);
    
    public void read() throws InterruptedException {
        // This ensures all reads block here until the first reads got the critical section (lock then acquire)
        lock.lock();
            reader++;
            if(reader == 1){
                cs.acquire();
            }
        lock.unlock();

        eligibleReader.acquire();  // Always use generic acquire after unlock the lock
        reading();
        eligibleReader.release();

        lock.lock();
        reader--;
        if(reader == 0)
            cs.release();
        lock.unlock();
    }
    
    public void write() throws InterruptedException {
        cs.acquire();
        writing();
        cs.release();
    }

    private static void writing() {
        System.out.println("writing started by: " + Thread.currentThread().getName());
        sleep();
        System.out.println("writing end by: " + Thread.currentThread().getName());
    }

    private void reading() {
        System.out.println("reading started by: " + Thread.currentThread().getName());
        sleep();
        System.out.println("reading end by: " + Thread.currentThread().getName());
    }

    private static void sleep() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
