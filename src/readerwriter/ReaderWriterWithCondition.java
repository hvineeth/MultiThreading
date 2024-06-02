package readerwriter;

import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ReaderWriterWithCondition {
    public int reader =0;
    public int writer = 0;

    public Lock lock = new ReentrantLock();
    public Condition eligibleForCS = lock.newCondition();
    public Semaphore cs = new Semaphore(1);
    public Semaphore eligibleReader = new Semaphore(4);

    public void read() throws InterruptedException {
        // This ensures all reads block here until the first reads got the critical section (lock then acquire)

        lock.lock();
        try {
            while(writer>0){
                eligibleForCS.await();
            }
            reader++;
        }finally{
            lock.unlock();
        }


        reading();

        lock.lock();
        try {
            reader--;
            if(reader==0){
                eligibleForCS.signalAll();
            }
        }finally{
            lock.unlock();
        }
    }

    public void write() throws InterruptedException {
        lock.lock();
        try {
            while(reader>0 || writer ==1){
                eligibleForCS.await();
            }
            writer++;
        }finally{
            lock.unlock();
        }

        writing();

        lock.lock();
        try {
            writer--;
            if(writer==0){
                eligibleForCS.signal(); // as at this point only one will be there and next only one can acquire. we can use signal instead of signalAll
            }
        }finally{
            lock.unlock();
        }
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
