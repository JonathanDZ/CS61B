package deque;

public class ArrayDeque<item>{
    private item[] items;
    private int size;
    private int first;
    private int last;
    private int length;
    public static int initialSize = 8;
    public static int beginPoint = 3;
    public static double miniRatio = 0.25;

    public ArrayDeque(){
        items = (item[]) new Object[initialSize];
        first = beginPoint;
        last = beginPoint + 1;
        length = initialSize;
        size = 0;
    }

    /**
     * Helper methods:
     * to correctly upsize and downsize the array.
     * Upsize/Downsize: put everything in order, then put them into new array
     */
    private void resize(int capacity){
        item[] a = (item[]) new Object[capacity];
        item[] temp = (item[]) new Object[size];
        int index = first + 1;
        if (index == length){
            index = 0;
        }
        for (int i = 0; i < size; i += 1){
            temp[i] = items[index];
            index += 1;
            if (index == length){
                index = 0;
            }
        }
        System.arraycopy(temp, 0, a, 0, size);
        items = a;
        length = capacity;
        first = length - 1;
        last = size;
    }

    public void addFirst(item item){
        if (size == length){
            resize(2 * length);
        }
        items[first] = item;
        first -= 1;
        if (first < 0){
            first = length - 1;
        }
        size += 1;
    }

    public void addLast(item item){
        if (size == length){
            resize(2 * length);
        }
        items[last] = item;
        last += 1;
        if (last == length){
            last = 0;
        }
        size += 1;
    }

    public boolean isEmpty(){
        if (size == 0){
            return true;
        }
        return false;
    }

    public int size(){
        return size;
    }

    public void printDeque(){
        System.out.print("sentinel");
        int index = first + 1;
        if (index == length){
            index = 0;
        }
        for (int i = size; i > 0; i -= 1){
            System.out.print(" --> " + items[index]);
            index += 1;
            if (index == length){
                index = 0;
            }
        }
    }

    public item removeFirst(){
        if (isEmpty() == true){
            return null;
        }
        int index = first + 1;
        if (index == length){
            index = 0;
        }
        item firstItem = items[index];
        items[index] = null;
        first = index;
        // remove one item
        size -= 1;
        double usageRatio = (double) size/length;
        if (usageRatio < miniRatio && length > initialSize){
            resize(length / 2);
        }
        return firstItem;
    }

    public item removeLast(){
        if (isEmpty() == true){
            return null;
        }
        int index = last - 1;
        if (index < 0){
            index = length - 1;
        }
        item lastItem = items[index];
        items[index] = null;
        last = index;
        // remove one item
        size -= 1;
        double usageRatio = (double) size/length;
        if (usageRatio < miniRatio && length > initialSize){
            resize(length / 2);
        }
        return lastItem;
    }

    public item get(int index){
        index = first + index + 1;
        if (first == length - 1){
            index = index - length;
        }
        int lastIndex;
        if (last <= first || size == length){
            lastIndex = last + length;
        }else {
            lastIndex = last;
        }
        if (index >= lastIndex){
            return null;
        }
        if (index >= length){
            index = index - length;
        }
        return items[index];
    }

    public boolean equals(Object o){
        if (o instanceof ArrayDeque<?>){
            if (this.size == ((ArrayDeque<?>) o).size){
                int index = first + 1;
                for (int i = 0; i < size; i += 1){
                    if (this.items[index] != ((ArrayDeque<?>) o).items[index]){
                        return false;
                    }
                    index += 1;
                    if (index == length){
                        index = 0;
                    }
                }
                return true;
            }
        }
        return false;
    }
}