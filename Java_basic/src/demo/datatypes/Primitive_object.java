package demo.datatypes;

public class Primitive_object {
    public static void main(String[] args) {
        //Kiểu primitive
        int a=5;
        int b=5;
        System.out.println(a==b);//so sánh giá trị
        // Kiểu Object (Wrapper class)
        Integer x=Integer.valueOf(500);
        Integer y=Integer.valueOf(500);
        System.out.println(x==y);// So sánh địa chỉ ô nhớ tham chiếu đến
        System.out.println(x.equals(y));// So sánh giá trị

    }

}
