package sem3matkul.pertemuan3.model;

import java.util.NavigableSet;
import java.util.TreeSet;

public class Treeset {
    private final TreeSet<String> treeset = new TreeSet<>();

    public void add(String item) {
        treeset.add(item);
    }

    public String remove(String item) {
        return treeset.remove(item) ? item : null;
    }

    public boolean contains(String item) {
        return treeset.contains(item);
    }

    public String first() {
        return treeset.isEmpty() ? null : treeset.first();
    }

    public String last() {
        return treeset.isEmpty() ? null : treeset.last();
    }

    public NavigableSet<String> getDescending() {
        return treeset.descendingSet();
    }

    public void clear() {
        treeset.clear();
    }

    public int size() {
        return treeset.size();
    }

    public TreeSet<String> getTreeset() {
        return treeset;
    }

    public TreeSet<String> getTreeSet() {
        return treeset;
    }
}