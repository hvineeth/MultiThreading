package unisexbathroom;

import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class UniSexBathRoom {

    private int menInBathRoom = 0;
    private int womenInBathRoom = 0;

    private final Lock lock = new ReentrantLock();
    private final Condition oppositeGender = lock.newCondition();
    private final Semaphore bathroomSemaphore = new Semaphore(1);

    public void men() throws InterruptedException {
        lock.lock();
        try {
            while (womenInBathRoom > 0) {
                oppositeGender.await();
            }
            menInBathRoom++;

        } finally {
            lock.unlock();
        }

        use();

        lock.lock();
        try {
            menInBathRoom--;
            if (menInBathRoom == 0) {
                oppositeGender.signalAll();
            }
        } finally {
            lock.unlock();
        }
    }

    public void women() throws InterruptedException {
        lock.lock();
        try {
            while (menInBathRoom > 0) {
                // Wait until no women are in the bathroom
                oppositeGender.await();
            }
            womenInBathRoom++;
        } finally {
            lock.unlock();
        }

        use();

        lock.lock();
        try {
            womenInBathRoom--;
            if (womenInBathRoom == 0) {
                oppositeGender.signalAll();
            }
        } finally {
            lock.unlock();
        }
    }

    private static void use() {
        System.out.println("Usage started by: " + Thread.currentThread().getName());
        sleep();
        System.out.println("Usage ended by: " + Thread.currentThread().getName());
    }

    private static void sleep() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}