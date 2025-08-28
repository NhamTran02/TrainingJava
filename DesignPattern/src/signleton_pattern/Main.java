package signleton_pattern;

public class Main {
    public static void main(String[] args) {
        Thread thread1=new Thread(()->{
            SignletonPattern.getInstance().Hello();
        });
        Thread thread2=new Thread(()->{
            SignletonPattern.getInstance().Hello();
        });

        thread1.start();
        thread2.start();

    }
}
