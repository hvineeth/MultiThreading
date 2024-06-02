package uberriderproblem;


import java.util.ArrayList;
import java.util.List;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class UberRider   {
    public int democrats = 0;
    public int republicans = 0;
    public Lock lock = new ReentrantLock();
    public Condition ride = lock.newCondition();
    public Semaphore democratSemaphore = new Semaphore(0);
    public Semaphore republicanSemaphore = new Semaphore(0);
    public Barrier barrier = new Barrier(4);

    public void seatDemocrat() throws InterruptedException, BrokenBarrierException {
        boolean rideLeader = false;
        lock.lock();
        try{

            if(!(newDemocratFormedRide()) ){
                democrats++;
                lock.unlock();
                democratSemaphore.acquire(); // Only ride leader manages critical. No need of acquiring lock here.
            }

            if(democrats == 3){
                rideLeader = true;
                democrats-=3;
                democratSemaphore.release(3);
            }

            if(democrats == 1 && republicans==2){
                rideLeader = true;
                democrats-=1;
                republicans-=2;
                democratSemaphore.release();
                republicanSemaphore.release(2);
            }

            seated();
            barrier.await();
            if(rideLeader){
                startRide();
            }

        } finally {
            if(rideLeader){
                lock.unlock();
            }
        }


    }

    public void seatRepublican() throws InterruptedException, BrokenBarrierException {
        boolean rideLeader = false;
        lock.lock();
        try{

            if(!(newRepublicanFormedRide())){
                republicans++;
                lock.unlock();
                republicanSemaphore.acquire(); // when you want to release based on condition use semaphore instead of condition wait();
            }

            if(republicans == 3){
                rideLeader = true;
                republicans-=3;
                republicanSemaphore.release(3);
            }

            if(democrats == 2 && republicans==1){
                rideLeader = true;
                democrats-=2;
                republicans-=1;
                democratSemaphore.release(2);
                republicanSemaphore.release();
            }

            seated();
            barrier.await();
            if(rideLeader){
                startRide();
            }

        }finally {
            if(rideLeader){
                lock.unlock();
            }
        }

    }

    public void seated(){
        System.out.println("seated : " + Thread.currentThread().getName());
    }

    public void startRide(){
        System.out.println("ride Start by : " + Thread.currentThread().getName());
    }

    public boolean newDemocratFormedRide(){
        return democrats == 3 || (democrats == 1 && republicans == 2);
    }

    public boolean newRepublicanFormedRide(){
        return republicans == 3 || (democrats == 2 && republicans == 1);
    }

    public static void main(String[] args) {
        UberRider uberRider = new UberRider();
        List<Thread> threads = new ArrayList<Thread>();
        for(int i = 0; i < 20; i++){
            Thread thread;
            if(i % 2 == 0){
                thread = new Thread((Runnable) () -> {
                    try {
                        uberRider.seatDemocrat();
                    } catch (InterruptedException | BrokenBarrierException e) {
                        throw new RuntimeException(e);
                    }
                }, "democrat-" + i);
            }else{
                thread = new Thread((Runnable) () -> {
                    try {
                        uberRider.seatRepublican();
                    } catch (InterruptedException | BrokenBarrierException e) {
                        throw new RuntimeException(e);
                    }
                }, "republican-" + i);
            }
            threads.add(thread);
        }

        threads.forEach(Thread::start);
    }
}
