package sem3matkul.pertemuan3.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Model koleksi ListArray menggunakan java.util.ArrayList
 */
public class ListArray {
    private final ArrayList<String> list = new ArrayList<>();

    public void add(String item) {
        list.add(item);
    }

    public boolean remove(String item) {
        return list.remove(item);
    }

    public String remove(int index) {
        if (index >= 0 && index < list.size()) {
            return list.remove(index);
        }
        return null;
    }

    public String get(int index) {
        if (index >= 0 && index < list.size()) {
            return list.get(index);
        }
        return null;
    }

    public void clear() {
        list.clear();
    }

    public int size() {
        return list.size();
    }

    public List<String> getList() {
        return list;
    }
}
