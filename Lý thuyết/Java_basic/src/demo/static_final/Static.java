package demo.static_final;

class Counter{
    static int count = 0;// biến static
    Counter(){
        count ++;// mỗi lần tạo object -> tăng count
    }

    // phương thức static: không phụ thuộc object nào
    static int square(int count){
        return count * count;
    }

}
public class Static {
    public static void main(String[] args) {
        new Counter();
        new Counter();
        new Counter();
        System.out.println(Counter.count);// Output:3 (dùng chung cho tất cả object)
        // gọi trực tiếp bằng tên class
        System.out.println(Counter.square(2));

    }
}
