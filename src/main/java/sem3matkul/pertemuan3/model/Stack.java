package sem3matkul.pertemuan3.model;

/**
 * Model koleksi Stack menggunakan java.util.Stack (LIFO)
 */
public class Stack {
    private final java.util.Stack<String> internalStack = new java.util.Stack<>();

    public void push(String item) {
        internalStack.push(item);
    }

    public String pop() {
        return internalStack.isEmpty() ? null : internalStack.pop();
    }

    public String peek() {
        return internalStack.isEmpty() ? null : internalStack.peek();
    }

    public boolean isEmpty() {
        return internalStack.isEmpty();
    }

    public void clear() {
        internalStack.clear();
    }

    public int size() {
        return internalStack.size();
    }

    public java.util.Stack<String> getStack() {
        return internalStack;
    }
}
