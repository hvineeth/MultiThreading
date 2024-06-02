package readerwriter;

import java.util.ArrayList;
import java.util.List;

public class readerWriterMain {
    public static void main(String[] args) {
        List<Thread> threads = new ArrayList<Thread>();
        ReaderWriter readerWriter = new ReaderWriter();
        for(int i = 0; i < 20; i++){
            Thread thread;
            if(i % 3 == 0){
                thread = new Thread((Runnable) () -> {
                    try {
                        readerWriter.write();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }, "writer-" + i);
            }else{
                thread = new Thread((Runnable) () -> {
                    try {
                        readerWriter.read();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }, "reader-" + i);
            }
            threads.add(thread);
        }

        threads.forEach(Thread::start);
    }
}
