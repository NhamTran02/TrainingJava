package array;

public class Array_demo {
    public static void main(String[] args) {
        //Khai báo và khởi tạo vs kích thước
        int[] arr= new int[5];
        //khai báo và khởi tạo trực tiếp
        int[] arr1={1,2,3,4};
        //Object array
        int[] arr2=new int[]{5,6,7,8};
        //mảng đa chiều
        int[][] arr3={{1,2,3},{4,5,6},{7,8,9}};
        //for
        for (int i = 0; i < arr1.length; i++) {
            System.out.println(arr1[i]);
        }
        System.out.println("---------");
        //for-each
        for (int i:arr2){
            System.out.println(i);
        }
    }
}
