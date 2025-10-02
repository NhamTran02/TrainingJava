package demo.memory;

class Person{
    String name;

    Person(String name){
        this.name = name;
    }
}
public class Stack_heap {
    public static void main(String[] args) {
        //primitive lưu trên stack
        int a=0;
        int b=a;
        b=1;
        System.out.println(a);//0
        System.out.println(b);//1
        System.out.println("----------");
        //Object lưu trong heap, tham chiếu trong stack
        Person person=new Person("Nam");//person lưu trong stack, Person("Nam") lưu trong heap
        Person person1=person;//person1 copy tham chiếu, cùng trỏ tới object
        person1.name="Nham";//thay đổi qua person1
        System.out.println("----------");
        Person person2 = new Person("Nham");
        Person person3 = new Person("Nham");
        System.out.println("Two different objects:");
        System.out.println((person2== person3));         // false (khác tham chiếu)
        System.out.println(person2.equals(person3)); // false (chưa override equals)
    }
}
