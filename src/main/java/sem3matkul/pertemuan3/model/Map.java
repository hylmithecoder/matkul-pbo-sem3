package sem3matkul.pertemuan3.model;

import java.util.LinkedHashMap;
import java.util.Map.Entry;
import java.util.Set;

public class Map {
    // Menggunakan LinkedHashMap agar urutan input terjaga dengan rapi
    private final java.util.Map<String, String> hashmap = new LinkedHashMap<>();

    public void put(String key, String value) {
        hashmap.put(key, value);
    }

    public String get(String key) {
        return hashmap.get(key);
    }

    public String remove(String key) {
        return hashmap.remove(key);
    }

    public boolean containsKey(String key) {
        return hashmap.containsKey(key);
    }

    public boolean containsValue(String value) {
        return hashmap.containsValue(value);
    }

    public Set<Entry<String, String>> entrySet() {
        return hashmap.entrySet();
    }

    public Set<String> keySet() {
        return hashmap.keySet();
    }

    public void clear() {
        hashmap.clear();
    }

    public int size() {
        return hashmap.size();
    }

    public java.util.Map<String, String> getMap() {
        return hashmap;
    }

    public java.util.Map<String, String> getHashmap() {
        return hashmap;
    }
}