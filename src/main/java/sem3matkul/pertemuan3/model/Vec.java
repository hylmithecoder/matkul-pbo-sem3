package sem3matkul.pertemuan3.model;

import java.util.Vector;

/**
 * Model koleksi Vec menggunakan java.util.Vector
 */
public class Vec {
    private final Vector<String> vector = new Vector<>();

    public void add(String item) {
        vector.add(item);
    }

    public boolean remove(String item) {
        return vector.remove(item);
    }

    public String get(int index) {
        if (index >= 0 && index < vector.size()) {
            return vector.get(index);
        }
        return null;
    }

    public void clear() {
        vector.clear();
    }

    public int size() {
        return vector.size();
    }

    public Vector<String> getVector() {
        return vector;
    }
}
