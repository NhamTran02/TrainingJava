package demo.overriding_overloading;

// class cha
class Animal {
    String name;

    public Animal(String name) {
        this.name = name;
    }
    public void makeSound(){
        System.out.println("MakeSound");
    }
    //method overloading
    public void eat() {
        System.out.println(name+" eating");
    }
    public  void eat(String food) {
        System.out.println(name+" eating "+food);
    }
}
class Dog extends Animal{
    public Dog(String name) {
        super(name);
    }

    @Override
    public void makeSound() {
        System.out.println("Gau gau");
    }
    //overloading
    public void sleep(){
        System.out.println(name+" sleeping");
    }
    public void sleep(int hours){
        System.out.println(name+" sleeping "+hours+" minutes");
    }
}
public class OverridingAndOverloading{
    public static void main(String[] args) {
        Animal animal = new Animal("Generic");
        animal.makeSound();
        animal.eat();
        animal.eat("grass");

        //object Dog
        Dog dog = new Dog("Dog");
        dog.makeSound();//overriding
        dog.eat("bone");//overriding từ animal
        dog.sleep();
        dog.sleep(5);//overloading
    }

}
