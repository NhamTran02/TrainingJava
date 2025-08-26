package queue;

import java.util.*;

public class Queue_demo {
    public static void main(String[] args) {
        // 1. Queue với LinkedList (FIFO)
        Queue<String> queue = new LinkedList<>();
        queue.offer("a");
        queue.offer("b");
        queue.offer("c");
        System.out.println(queue);
        System.out.println(queue.poll());//xoá và trả về phần tử đầu
        System.out.println(queue);
        System.out.println(queue.peek());// xem phần tử đầu mà kh xoá
        System.out.println("-----------");
        // 2. PriorityQueue (ưu tiên)
        Queue<Integer> priorityQueue = new PriorityQueue<>();
        priorityQueue.offer(5);
        priorityQueue.offer(2);
        priorityQueue.offer(4);
        System.out.println(priorityQueue);
        System.out.println(priorityQueue.poll());
        System.out.println(priorityQueue.poll());
        Queue<Integer> p = new PriorityQueue<>((p1,p2)-> p2.compareTo(p1));
        p.offer(3);
        p.offer(1);
        p.offer(0);
        p.offer(2);
        System.out.println(p);
        System.out.println("-----------");
        // 3. ArrayDeque (deque dùng như queue/stack)
        Deque<Integer> arrayDeque = new ArrayDeque<>();
        arrayDeque.offerLast(2);
        arrayDeque.offerLast(3);
        arrayDeque.offerFirst(4);
        System.out.println(arrayDeque);
        System.out.println("pollFirst(): " + arrayDeque.pollFirst());
        System.out.println("pollLast(): " + arrayDeque.pollLast());
        System.out.println("After poll: " + arrayDeque);

    }
}
