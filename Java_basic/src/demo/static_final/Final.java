package demo.static_final;

class Parent {
    public final void show(){
        System.out.println("Parent show");
    }
}
class Child extends Parent {
    //public void show(){}//Lỗi show() in demo.static_final.Child cannot override show() in demo.static_final.Parent
    //overridden method is final
}
final class Car{
}
//class Motorbike extends Car{ }// Lỗi Cannot inherit from final 'demo.static_final.Car'
public class Final extends Parent {
    public static void main(String[] args) {
         final int a=0;
        //a=1;// lỗi cannot assign a value to final variable a
    }
}
