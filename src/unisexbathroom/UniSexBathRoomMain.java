package unisexbathroom;

import java.util.ArrayList;
import java.util.List;

public class UniSexBathRoomMain {
    public static void main(String[] args) {
        List<Thread> threads = new ArrayList<Thread>();
        UniSexBathRoom uniSexBathRoom = new UniSexBathRoom();
        for(int i = 0; i < 20; i++){
            Thread thread;
            if(i % 2 == 0){
                thread = new Thread((Runnable) () -> {
                    try {
                        uniSexBathRoom.men();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }, "men-" + i);
            }else{
                thread = new Thread((Runnable) () -> {
                    try {
                        uniSexBathRoom.women();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }, "women-" + i);
            }
            threads.add(thread);
        }

        threads.forEach(Thread::start);
    }
}
