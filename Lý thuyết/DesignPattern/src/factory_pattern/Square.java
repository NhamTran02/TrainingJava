package factory_pattern;

public class Square implements Shape {
    @Override
    public void draw() {
        System.out.println("Square");
    }

    @Override
    public void color(String color) {
        System.out.println("Square Color "+ color);
    }
}
