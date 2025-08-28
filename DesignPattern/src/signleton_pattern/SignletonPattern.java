package signleton_pattern;

import java.util.Random;

public class SignletonPattern {
    private final int index;
    private static SignletonPattern instance;

    private SignletonPattern(int index) {
        this.index = index;
    }

    public static synchronized SignletonPattern getInstance() {// dùng synchronized để tránh lỗi trong multi thre
        if (instance == null) {
            int ramdom=new Random().nextInt(1,10);
            instance = new SignletonPattern(ramdom);
        }
        return instance;
    }
    public void Hello() {
        System.out.println("Hello user "+ index);
    }
}
