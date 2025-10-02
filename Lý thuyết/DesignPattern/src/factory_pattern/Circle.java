package factory_pattern;

public class Circle implements Shape {
    @Override
    public void draw() {
        System.out.println("Circle");
    }

    @Override
    public void color(String color) {
        System.out.println("Circle Color"+ color);
    }
}
