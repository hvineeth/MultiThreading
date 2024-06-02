import readerwriter.ReaderWriter;

import java.util.ArrayList;
import java.util.List;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
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