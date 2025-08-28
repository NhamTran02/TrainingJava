package factory_pattern;

public class SquareFactory extends ShapeFactory {
    @Override
    Shape createShape() {
        return new Square();
    }
}
