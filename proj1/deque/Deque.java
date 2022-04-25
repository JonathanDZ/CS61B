package deque;

public interface Deque<item> {
    public void addFirst(item item);
    public void addLast(item item);
    default public boolean isEmpty(){
        return size() == 0;
    }
    public int size();
    public void printDeque();
    public item removeFirst();
    public item removeLast();
    public item get(int index);
}
