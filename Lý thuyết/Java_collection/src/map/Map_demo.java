package map;

import java.util.*;

public class Map_demo {
    enum Day {MON, TUE, WED, THU, FRI, SAT, SUN};
    public static void main(String[] args) {
        System.out.println("=== 1. HashMap ===");
        Map<String, Integer> hashMap = new HashMap<>();
        hashMap.put("A", 1);
        hashMap.put("B", 2);
        hashMap.put("1", 3);
        hashMap.put("1", 4);// trùng key nên nó ghi đè lại value
        hashMap.put(null, 0); // 1 key null
        hashMap.put("C", null); // nhiều value null
        hashMap.put("D", null);
        System.out.println("HashMap: " + hashMap);

        System.out.println("=== 2. LinkedHashMap ===");
        Map<String, Integer> linkedMap = new LinkedHashMap<>();
        linkedMap.put("B", 2);
        linkedMap.put("A", 1);
        linkedMap.put("C", 3);
        System.out.println("LinkedHashMap (insertion order): " + linkedMap);

        System.out.println("\n=== 3. LinkedHashMap ===");
        LinkedHashMap<Integer, String> linkedHashMap = new LinkedHashMap<>(16, 0.75f,true);
        linkedHashMap.put(1, "A");
        linkedHashMap.put(2, "B");
        linkedHashMap.put(3, "C");
        System.out.println("LinkedHashMap (insertion order): " + linkedHashMap);
        linkedHashMap.get(1); // truy cập key 1 -> lên cuối
        linkedHashMap.put(4, "D");
        System.out.println("LRU Cache (accessOrder=true): " + linkedHashMap);

        System.out.println("\n=== 4. TreeMap ===");
        Map<Integer, String> treeMap = new TreeMap<>();
        treeMap.put(30, "C");
        treeMap.put(10, "A");
        treeMap.put(20, "B");
        //treeMap.put(null, "D");// Lỗi vì kh cho phép key null
        System.out.println("TreeMap (sorted keys): " + treeMap);
        // Range queries
        System.out.println("subMap(10, 20): " + ((TreeMap<Integer,String>)treeMap).subMap(10, 20));
        System.out.println("headMap(20): " + ((TreeMap<Integer,String>)treeMap).headMap(20));
        System.out.println("tailMap(20): " + ((TreeMap<Integer,String>)treeMap).tailMap(20));

        System.out.println("\n=== 5. EnumMap ===");
        Map<Day, String> enumMap = new EnumMap<>(Day.class);
        enumMap.put(Day.MON, "Work");
        enumMap.put(Day.WED, "Gym");
        enumMap.put(Day.TUE, "learn");
        System.out.println("EnumMap: " + enumMap);// sắp xếp theo thứ tự enum
    }
}
