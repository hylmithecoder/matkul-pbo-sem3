package sem3matkul.pertemuan3.model;

import java.util.LinkedList;

/**
 * Model koleksi Linked menggunakan java.util.LinkedList
 */
public class Linked {
    private final LinkedList<String> linkedList = new LinkedList<>();

    public void add(String item) {
        linkedList.add(item);
    }

    public void addFirst(String item) {
        linkedList.addFirst(item);
    }

    public void addLast(String item) {
        linkedList.addLast(item);
    }

    public boolean remove(String item) {
        return linkedList.remove(item);
    }

    public String removeFirst() {
        return linkedList.isEmpty() ? null : linkedList.removeFirst();
    }

    public String removeLast() {
        return linkedList.isEmpty() ? null : linkedList.removeLast();
    }

    public void clear() {
        linkedList.clear();
    }

    public int size() {
        return linkedList.size();
    }

    public LinkedList<String> getLinkedList() {
        return linkedList;
    }
}
