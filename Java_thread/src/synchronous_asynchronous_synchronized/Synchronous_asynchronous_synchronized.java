package synchronous_asynchronous_synchronized;

import java.util.concurrent.CompletableFuture;

public class Synchronous_asynchronous_synchronized {
    public static void task(String name, int miliseconds) {
        System.out.println(name + " started ");
        try {
            Thread.sleep(miliseconds);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println(name + " finished ");
    }

    public static void synchronousDemo() {
        System.out.println("Synchronous demo");
        task("task 1", 3000);
        task("task 2", 1000);
        System.out.println("Synchronous demo finished");
    }

    public static void asyncDemo() {
        System.out.println("Async demo");
        CompletableFuture<Void> t1 = CompletableFuture.runAsync(() -> {
            task("task 1", 2000);
        });
        CompletableFuture<Void> t2 = CompletableFuture.runAsync(() -> {
            task("task 2", 1000);
        });
        // Chờ tất cả hoàn tất
        CompletableFuture.allOf(t1,t2).join();
        System.out.println("Async demo finished");
    }

    static class Count{
        private int count;
        //kh synchronized -> race condition
        public void incrementUnsafe() {
            count++;
        }
        //synchronized -> thread-safe
        public synchronized void incrementSafe() {
            count++;
        }
        public int getCount() {
            return count;
        }
    }

    public static void synchronizedDemo() throws InterruptedException {
        Count count = new Count();
        Runnable unSafe = () -> {
            for (int i = 0; i < 1000; i++) {
                count.incrementUnsafe();

            }
        };

        Thread thread1 = new Thread(unSafe);
        Thread thread2 = new Thread(unSafe);
        thread1.start();
        thread2.start();
        thread1.join();
        thread2.join();
        System.out.println("Unsafe: "+ count.getCount());

        Count count1 = new Count();
        Runnable safe = () -> {
            for (int i = 0; i < 1000; i++) {
                count1.incrementSafe();
            }
        };
        Thread thread3 = new Thread(safe);
        Thread thread4 = new Thread(safe);
        thread3.start();
        thread4.start();
        thread3.join();
        thread4.join();
        System.out.println("Safe: "+ count1.getCount());


    }

    public static void main(String[] args) throws InterruptedException {
//        synchronousDemo();
//        asyncDemo();
        synchronizedDemo();

    }
}
