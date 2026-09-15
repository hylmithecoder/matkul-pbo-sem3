package sem3matkul.pertemuan3.model;

import java.util.LinkedList;

/**
 * Model koleksi Queue menggunakan FIFO Queue
 */
public class Queue {
    private final java.util.Queue<String> queue = new LinkedList<>();

    public boolean offer(String item) {
        return queue.offer(item);
    }

    public String poll() {
        return queue.poll();
    }

    public String peek() {
        return queue.peek();
    }

    public void clear() {
        queue.clear();
    }

    public int size() {
        return queue.size();
    }

    public java.util.Queue<String> getQueue() {
        return queue;
    }
}
