package demo.oop;
//có cả method thường và method abstract
abstract class Animal {
    private int a;
    String name;

    public Animal(int a) {
        this.a = a;
    }

    public int getA() {
        return a;
    }

    public void setA(int a) {
        this.a = a;
    }

    // abstract method (không có body)
    public abstract void makeSound();

    public void eat() {
        System.out.println(name + " is eating");
    }
}
// Class con bắt buộc override makeSound()
class Dog extends Animal {

    //Child đc thừa hưởng thuộc tính a và ghi đè phương thức makeSound
    public Dog(int a) {
        super(a);// gọi constructor của cha
    }

    @Override
    public void makeSound() {
        System.out.println(name+" gaugau" );
    }

    //method riêng
    public void sleep(){
        System.out.println(name+" sleep");
    }

}

class Cat extends Animal {
    public Cat(int a) {
        super(a);
    }

    @Override
    public void makeSound() {
        System.out.println(name + " meomeo" );
    }
}

public class OOP {
    public static void main(String[] args) {
        Animal p = new Animal(5) {
            @Override
            public void makeSound() {}
        };
        System.out.println(p.getA());// dùng getter
        p.setA(10);
        System.out.println(p.getA());// dùng setter
        //int b=a;//Lỗi Cannot resolve symbol 'a' vì a private không thấy từ class khác
        System.out.println("----");
        // Object Dog
        Dog d = new Dog(10);
        d.name = "Buddy";   // gán tên
        d.makeSound();      // Buddy gaugau
        d.sleep();          // Buddy sleep

        System.out.println("----");

        // Object Cat
        Cat c = new Cat(8);
        c.name = "Kitty";
        c.makeSound();      // Kitty meomeo

        System.out.println("----");

        // Polymorphism: biến kiểu Animal nhưng tham chiếu tới Dog
        Animal polyDog = new Dog(20);
        polyDog.name = "Max";
        polyDog.makeSound();  // Max gaugau  (gọi phương thức override)
        polyDog.eat();        // Max is eating
        // polyDog.sleep();   // lỗi, vì Animal không có sleep()

        System.out.println("----");

        //Polymorphism: biến kiểu Animal nhưng tham chiếu tới Cat
        Animal polyCat = new Cat(15);
        polyCat.name = "Tom";
        polyCat.makeSound();  // Tom meomeo
        polyCat.eat();        // Tom is eating

        //Animal a=new Animal(5);//Lỗi không thể tạo object abstract class

    }
}
