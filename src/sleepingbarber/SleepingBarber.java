package sleepingbarber;

import readerwriter.ReaderWriter;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class SleepingBarber {

    public int waitingCustomers = 0;
    public Lock lock = new ReentrantLock();
    public Condition barber = lock.newCondition();
    public Condition cutting = lock.newCondition();
    public static int WAITING_CUSTOMERS = 5;
    public boolean isBarberFree = true;
    public boolean isCuttingCompleted;

    public void allocateCustomer() throws InterruptedException {
        lock.lock();
        try {
            System.out.println("seat requested by: " + Thread.currentThread().getName());
            if (waitingCustomers == WAITING_CUSTOMERS) {
                System.out.println("seat rejected for: " + Thread.currentThread().getName());
                return;
            }
            waitingCustomers++;
            System.out.println("seated : " + Thread.currentThread().getName());

            while (!isBarberFree) {
                cutting.await();
            }

            System.out.println("cutting started for: " + Thread.currentThread().getName());
            waitingCustomers--;
            barber.signal();
            isBarberFree = false;

        } finally {
            lock.unlock();
        }

        doCutting();
        lock.lock();
        try{
            isBarberFree = true;
            cutting.signal();
        }
        finally {
            lock.unlock();
        }
    }

    public void startBarber() throws InterruptedException {
        while(true) {
            lock.lock();
            try {
                while (waitingCustomers == 0 && isBarberFree) {
                    barber.await();
                }
            } finally {
                lock.unlock();
            }
        }
    }

    private static void doCutting() {
        sleep();
        System.out.println("cutting completed");
    }


    private static void sleep() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
    public static void main(String[] args) throws InterruptedException {
        SleepingBarber sleepingBarber = new SleepingBarber();

        Thread barber = new Thread(() -> {
            try {
                sleepingBarber.startBarber();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }, "barber");
        System.out.println("barber started");
        barber.start();
        List<Thread> threads = new ArrayList<Thread>();
        ReaderWriter readerWriter = new ReaderWriter();
        for(int i = 0; i < 10; i++){
            Thread thread = new Thread(() -> {
                try {
                    sleepingBarber.allocateCustomer();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }, "customer-"+i);
            threads.add(thread);
        }
        threads.forEach(Thread::start);

    }
}
