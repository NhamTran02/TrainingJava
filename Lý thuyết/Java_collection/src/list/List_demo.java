package list;


import java.util.*;

public class List_demo {
    public static void main(String[] args){
        //ArrayList
        List<String> list = new ArrayList<>();
        list.add("a");
        list.add("b");
        list.add("a");//cho phép trùng lặp
        list.add("c");
        System.out.println(list);
        list.remove(1);
        System.out.println(list);
        System.out.println(list.size());
        System.out.println(list.get(1));
        System.out.println("----------");
        //LinkedList
        List<String> linkedList = new LinkedList<>();
        linkedList.add("a");
        linkedList.add("b");
        linkedList.add("c");
        System.out.println(linkedList);
        linkedList.add(1,"x");
        System.out.println(linkedList);
        System.out.println("---------");
        //Vector
        List<String> vector = new Vector<>();
        vector.add("a");
        vector.add("b");
        vector.add("c");
        System.out.println(vector);
        vector.remove(1);
        System.out.println(vector);
        System.out.println("---------");
        //Stack
        Stack<String> stack=new Stack<>();
        stack.push("a");
        stack.push("b");
        stack.push("c");
        System.out.println(stack);
        stack.pop();
        System.out.println(stack);
        System.out.println(stack.peek());
    }
}
