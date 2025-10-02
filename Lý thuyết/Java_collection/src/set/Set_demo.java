package set;

import java.util.*;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.CopyOnWriteArraySet;

public class Set_demo {
    enum Day{MON,TUE,WED,THU,FRI,SAT,SUN};

    public static void main(String[] args) {
        System.out.println("=== 1. HashSet ===");
        Set<String> hashSet = new HashSet<String>();
        hashSet.add("a");
        hashSet.add("b");
        hashSet.add("a");//Phần tử trùng, sẽ bị bỏ
        hashSet.add("1");
        hashSet.add(null);
        System.out.println(hashSet);

        System.out.println("=== 2. LinkedHashSet ===");
        Set<String> linkedHashSet = new LinkedHashSet<String>();
        linkedHashSet.add("a");
        linkedHashSet.add("c");
        linkedHashSet.add("b");
        linkedHashSet.add(null); // cho phép null
        System.out.println("LinkedHashSet (thứ tự chèn): " + linkedHashSet);

        System.out.println("\n=== 3. TreeSet ===");
        Set<String> treeSet = new TreeSet<>();
        treeSet.add("b");
        treeSet.add("a");
        treeSet.add("c");
        //treeSet.add(null); // Exception nếu uncomment
        System.out.println("TreeSet (tự sắp xếp): " + treeSet);

        System.out.println("\n=== 4. EnumSet ===");
        Set<Day> enumSet = EnumSet.of(Day.MON, Day.WED, Day.FRI);
        System.out.println("EnumSet: " + enumSet);

        System.out.println("\n=== 5. CopyOnWriteArraySet ===");
        Set<String> cowSet = new CopyOnWriteArraySet<>();
        cowSet.add("x");
        cowSet.add("y");
        cowSet.add("x"); // trùng
        System.out.println("CopyOnWriteArraySet: " + cowSet);

        System.out.println("\n=== 6. ConcurrentSkipListSet ===");
        Set<Integer> concurrentSet = new ConcurrentSkipListSet<>();
        concurrentSet.add(3);
        concurrentSet.add(1);
        concurrentSet.add(2);
        concurrentSet.add(3);//Trùng
        System.out.println("ConcurrentSkipListSet (sắp xếp + thread-safe): " + concurrentSet);
    }
}
