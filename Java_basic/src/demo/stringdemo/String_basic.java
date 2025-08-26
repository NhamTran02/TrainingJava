package demo.stringdemo;

public class String_basic {
    public static void main(String[] args) {
        String s = "Abc";
        // 1 số phương thức hữu ích trong String
        s.concat("def");// không thay đổi s vì String có tính bất biến, mà tạo object mới "abcdef"
        System.out.println(s);//abc
        String s1=s.toUpperCase();
        System.out.println(s1);
        String s2=s.toLowerCase();
        System.out.println(s2);
        String s3= s.replace("c","1");
        System.out.println(s3);
    }
}
