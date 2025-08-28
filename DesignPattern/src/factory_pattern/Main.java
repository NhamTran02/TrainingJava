package factory_pattern;

public class Main {
    public static void main(String[] args) {
        ShapeFactory factory;
        factory = new CircleFactory();
        Shape circle = factory.createShape();
        circle.draw();
        factory = new SquareFactory();
        Shape square = factory.createShape();
        square.draw();
    }
}
