package readerwriter;

import java.util.ArrayList;
import java.util.List;

public class ReaderWriterWithConditionMain {
    public static void main(String[] args) {
        List<Thread> threads = new ArrayList<Thread>();
        ReaderWriterWithCondition readerWriter = new ReaderWriterWithCondition();
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
