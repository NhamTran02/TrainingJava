package demo.datatypes;

public class Wrapper_boxing_unboxing {
    public static void main(String[] args) {
        // Autoboxing & Unboxing
        int x=5;
        Integer a = x; // Autoboxing: int -> Integer
        int b = a; // Unboxing: Integer -> int
        System.out.println(a==b);// Java tự động unboxing chuyển từ Integer về kiểu int để so sánh đc 2 giá trị
        System.out.println("Autoboxing + Unboxing: " + b);
    }
}
