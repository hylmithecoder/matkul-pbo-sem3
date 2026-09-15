package sem3matkul.pertemuan3.model;

import java.util.HashSet;
import java.util.function.Predicate;

public class Hashset {
    private final HashSet<String> hashset = new HashSet<>();

    public void add(String item) {
        hashset.add(item);
    }

    public String remove(String item) {
        return hashset.remove(item) ? item : null;
    }

    public boolean removeIf(Predicate<String> filter) {
        return hashset.removeIf(filter);
    }

    public boolean contains(String item) {
        return hashset.contains(item);
    }

    public void clear() {
        hashset.clear();
    }

    public int size() {
        return hashset.size();
    }

    public HashSet<String> getHashset() {
        return hashset;
    }
}