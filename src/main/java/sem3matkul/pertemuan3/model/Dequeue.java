package sem3matkul.pertemuan3.model;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * Model koleksi Dequeue menggunakan java.util.Deque (ArrayDeque)
 */
public class Dequeue {
    private final Deque<String> deque = new ArrayDeque<>();

    public void addFirst(String item) {
        deque.addFirst(item);
    }

    public void addLast(String item) {
        deque.addLast(item);
    }

    public String pollFirst() {
        return deque.pollFirst();
    }

    public String pollLast() {
        return deque.pollLast();
    }

    public String peekFirst() {
        return deque.peekFirst();
    }

    public String peekLast() {
        return deque.peekLast();
    }

    public void clear() {
        deque.clear();
    }

    public int size() {
        return deque.size();
    }

    public Deque<String> getDeque() {
        return deque;
    }
}
