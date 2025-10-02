package thread;

import java.util.concurrent.*;

class Thread1 extends Thread{
    @Override
    public void run(){
        System.out.println("Luồng đc kế thừa bằng Thread");
    }
}

class Thread2 implements Runnable{
    @Override
    public void run() {
        System.out.println("Luồng đc tạo bằng cách triển khai Runnable");
    }
}

public class Thread_demo {
    public static void main(String[] args){
        Thread t1 = new Thread(new Thread1());
        Thread t2 = new Thread(new Thread2());
        Thread t3 = new Thread(()->{
            System.out.println("Luồng đc tạo bằng labda");
        });

        t1.start();
        t2.start();
        t3.start();

        System.out.println("\n=== 4. Callable + Future (có trả về kết quả) ===");
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Callable<Integer> callable = ()->{
            System.out.println("Callable đang chạy trong: " + Thread.currentThread().getName());
            Thread.sleep(2000);
          return 0;
        };
        Future<Integer> future = executor.submit(callable);

        System.out.println("Task done? "+ future.isDone());
        Integer result = null; // get() sẽ block cho đến khi task hoàn thành
        try {
            result = future.get();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }

        System.out.println("Kết quả từ Callable: " + result);
        System.out.println("Task done? " + future.isDone());
        executor.shutdown();

        System.out.println("\n=== 5. Thread Pool (ExecutorService) ===");
        ExecutorService pool = Executors.newFixedThreadPool(3);
        for (int i = 0; i < 5; i++) {
            int id=i;
            pool.execute(()->{
                System.out.println("Task: " +id+" đang chạy "+ Thread.currentThread().getName());
            });
        }
        pool.shutdown();
    }
}
