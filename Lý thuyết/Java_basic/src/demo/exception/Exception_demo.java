package demo.exception;

import java.io.FileReader;
import java.io.IOException;

class CustomException extends java.lang.Exception {
    public CustomException(String message) {
        super(message);
    }
}

public class Exception_demo {
    //throws
    public static void CheckAge(int age) throws CustomException {
        if (age < 18) {
            throw new CustomException("Age must be greater than 18");
        }
        System.out.println("Age is " + age);
    }
    public static void main(String[] args){
        //checked Exception
        try{
            FileReader fileReader=new FileReader("D:\\test\\test1.txt");
        }
        catch(java.lang.Exception e){
            System.out.println(e);
        }
        //Unchecked exception
        try{
            int x=1/0;
        }
        catch(ArithmeticException e){
            System.out.println(e);
        }
        //custom exception
        try {
            Exception_demo.CheckAge(10);
        } catch (CustomException e) {
            System.out.println(e);
        }

        // Try-catch ở đây phải thêm khối finally để đóng thủ công FileReader
        FileReader fileReader = null;
        try {
            fileReader = new FileReader("D:\\test\\test1.txt");
        }
        catch (java.lang.Exception e) {
            System.out.println(e);
        }
        finally {
            try{
                if(fileReader != null){
                    fileReader.close();
                }
            }
            catch (IOException e){
                System.out.println(e);
            }
        }
        //Try-with-resource: tài nguyên đc khai báo trong ngoặc đơn của try
        try(FileReader fileReader1=new FileReader("test.txt")){
            fileReader1.read();
        }
        catch(IOException e){
            System.out.println(e);
        }// tự động đóng FileReader tại đây để tránh rò rỉ bộ nhớ
    }
}
