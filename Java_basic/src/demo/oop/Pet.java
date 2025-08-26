package demo.oop;

//
interface Pet {
    void play();//method abstract
}
interface Guard{
    void guardHouse();
}
//class có thể implements nhiều interface
class Horse extends Animal implements Pet,Guard{

    public Horse(int a) {
        super(a);
    }

    @Override
    public void play() {
        System.out.println(name + " is playing fetch");
    }

    @Override
    public void guardHouse() {
        System.out.println(name + " is guarding the child");
    }

    @Override
    public void makeSound() {
        System.out.println(name + " says: Hí Hí");
    }

    public static void main(String[] args) {
        Horse horse = new Horse(5);
        horse.name="horse";
        horse.makeSound();
        horse.eat();
        horse.play();
        horse.guardHouse();
    }
}
