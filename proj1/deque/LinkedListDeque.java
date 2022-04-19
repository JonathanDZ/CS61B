package deque;

public class LinkedListDeque<AnyType> {
    private int size;
    private DENode sentinel;

    private class DENode{
        public DENode prev;
        public DENode next;
        public AnyType item;

        public DENode(DENode prev, AnyType item, DENode next){
            this.prev = prev;
            this.item = item;
            this.next = next;
        }
    }

    public LinkedListDeque(){
        this.sentinel = new DENode(null, null, null);
        this.sentinel.prev = sentinel;
        this.sentinel.next = sentinel;
        this.size = 0;
    }

    public void addFirst(AnyType item){
        sentinel.next = new DENode(sentinel, item, sentinel.next);
        sentinel.next.next.prev = sentinel.next;
        this.size += 1;
    }

    public void addLast(AnyType item){
        DENode last = sentinel.prev;
        last.next = new DENode(sentinel.prev, item, sentinel);
        sentinel.prev = last.next;
        this.size += 1;
    }

    public boolean isEmpty(){
        if (sentinel.next == sentinel){
            return true;
        }else{
            return false;
        }
    }

    /**
     * Remember to add size changes in add/remove functions
     */
    public int size(){
        return size;
    }

    public void printDeque(){
        System.out.print("sentinel");
        DENode pointer = sentinel.next;
        while (pointer != sentinel){
            System.out.print(" --> " + pointer.item);
            pointer = pointer.next;
        }
        System.out.println("");
    }

    public AnyType removeFirst(){
        if (sentinel.next == sentinel){
            return null;
        }
        AnyType firstItem = sentinel.next.item;
        sentinel.next = sentinel.next.next;
        sentinel.next.prev = sentinel;
        // size reduces one
        this.size -= 1;
        return firstItem;
    }

    public AnyType removeLast(){
        if (sentinel.prev == sentinel){
            return null;
        }
        AnyType lastItem = sentinel.prev.item;
        sentinel.prev = sentinel.prev.prev;
        sentinel.prev.next = sentinel;
        // size reduces one
        this.size -= 1;
        return lastItem;
    }

    public AnyType get(int index){
        DENode pointer = sentinel;
        while(pointer.next != sentinel){
            if (index == 0){
                return pointer.next.item;
            }else {
                pointer = pointer.next;
                index -= 1;
            }
        }
        return null;
    }

    private AnyType getRecursive(int index, DENode pointer){
        if (pointer == sentinel){
            return null;
        }
        if (index == 0){
            return pointer.item;
        }else {
            return getRecursive(index - 1, pointer.next);
        }
    }

    public AnyType getRecursive(int index){
        return getRecursive(index, sentinel.next);
    }

    /**
     * I don't know what is iterator, maybe I should come back and write this
     * after I learn the concept of iterator in Lecture 11.
     */
//    public Iterator<AnyType> iterator(){
//        return null;
//    }

    public boolean equals(Object o){
        if (o instanceof LinkedListDeque<?>){
            if (this.size == ((LinkedListDeque<?>) o).size){
                DENode thisPointer = this.sentinel.next;
                LinkedListDeque<?>.DENode oPointer = ((LinkedListDeque<?>) o).sentinel.next;
                while(thisPointer != sentinel){
                    if(thisPointer.item != oPointer.item){
                        return false;
                    }
                    thisPointer = thisPointer.next;
                    oPointer = oPointer.next;
                }
                return true;
            }
            return false;
        }
        return false;
    }
}
