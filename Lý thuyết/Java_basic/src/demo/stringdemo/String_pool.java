package demo.stringdemo;

public class String_pool {
    public static void main(String[] args){
        // Cả s1 và s2 đều tham chiếu đến cùng một đối tượng "Java"
        String s1="java";
        String s2="java";
        // Tạo đối tượng String mới bằng từ khóa new
        String s3=new String("java");

        // so sánh địa chỉ ô nhớ
        System.out.println(s1==s2);// true
        // So sánh s1 với s3, chúng có nội dung giống nhau nhưng tham chiếu khác nhau
        System.out.println(s1==s3);//false (s3 là object mới trong heap)
        // so sánh nội dung s1 với s3
        System.out.println(s1.equals(s3));//true
    }

}
