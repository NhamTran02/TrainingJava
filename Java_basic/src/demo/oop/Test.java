package demo.oop;

interface A{
    void show();
}
interface B{
    void show(int i);
}
abstract class X {
    abstract void show();
}

interface Y{
    int show();
}

class C implements A,B{
    @Override
    public void show() {
        System.out.println("show");
    }

    @Override
    public void show(int i) {
        System.out.println("show"+ i);
    }
}
interface ab{
    void in();
}
abstract class ab1{
    void in(){
        System.out.println("in");
    }
}

//class Z extends X implements Y{// Lỗi do nó kh biết gọi phương thức của X hay của Y
//    @Override
//    public int show() {
//        return 0;
//    }
//}


public class Test {
    public static void main(String[] args) {
        C c = new C();
        c.show(3);
    }
}
