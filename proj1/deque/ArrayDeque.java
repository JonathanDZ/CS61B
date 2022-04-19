package deque;

class ArrayDeque<AnyType>{
    private int size;
    private ArrayNode sentinel;

    private class ArrayNode{
        public AnyType[] AnyTypeArray;
        public ArrayNode prev;
        public ArrayNode next;
        // To indicate the position of the first/last element
        // Changes When add first or add last
        public int first;
        public int last;
        // To define how big the array is
        public static int ArraySize = 8;
        // To define where to store first element
        public static int beginPoint = 3;

        public ArrayNode(ArrayNode prev, ArrayNode next){
            this.AnyTypeArray = (AnyType[]) new Object[ArraySize];
            this.prev = prev;
            this.next = next;
            this.first = beginPoint;
            this.last = beginPoint + 1;
        }

        /**
         * Helper method: add an element to this array,
         * if it added successfully, return the index of the element,
         * if the array is full, return -1 (an array only holds 7 elements)
         */
        public int arrayAddLast(AnyType item){
            if (first == last){
                return -1;
            }
            AnyTypeArray[last] = item;
            last += 1;
            if (last == ArraySize){
                return 0;
            }
            return last;
        }

        public int arrayAddFirst(AnyType item){
            if (first == last){
                return -1;
            }
            AnyTypeArray[first] = item;
            first -= 1;
            if (first == -1){
                return ArraySize - 1;
            }
            return first;
        }

        /**
         * Return true if this array is empty, don't care about other arrayNode.
         * Can be used in remove array function,
         * to check if the array is empty or not;
         * When used in isEmpty function,
         * be aware to check if it's the first node or not!!
         */
        public boolean arrayIsEmpty(){
            if (first == last + 1){
                return true;
            }
            if (first == ArraySize - 1 && last == 0){
                return true;
            }
            return false;
        }

        public void printArray(){
            int index = first + 1;
            if (index == ArraySize){
                index = 0;
            }
            while (index != last){
                System.out.print(" --> " + AnyTypeArray[index]);
                index += 1;
                if (index == ArraySize){
                    index = 0;
                }
            }
        }

        /**
         * Remove the first/last element of array, and return it.
         * If array is empty, return null.
         */
        public AnyType arrayRemoveFirst(){
            if (this.arrayIsEmpty() == true){
                return null;
            }
            int index = first + 1;
            if (index == ArraySize){
                index = 0;
            }
            AnyType firstItem = AnyTypeArray[index];
            first = index;
            return firstItem;
        }

        public AnyType arrayRemoveLast(){
            if (this.arrayIsEmpty() == true){
                return null;
            }
            int index = last - 1;
            if (index == -1){
                index = ArraySize - 1;
            }
            AnyType lastItem = AnyTypeArray[index];
            last = index;
            return lastItem;
        }

        /**
         * Get an element in array, if the index is invalid, return null;
         */
        public AnyType arrayGet(int index){
            index = first + index + 1;
            int lastIndex;
            if (last <= first){
                lastIndex = last + ArraySize;
            }else {
                lastIndex = last;
            }
            if (index >= lastIndex){
                return null;
            }
            if (index >= ArraySize){
                index = index - ArraySize;
            }
            return AnyTypeArray[index];
        }

        /**
         * Use to find two arrayNodes are equal or not.
         */
        public boolean equals(ArrayNode objectArrayNode){
            if (this.first == objectArrayNode.first &&
                    this.last == objectArrayNode.last){
                int index = first + 1;
                if (index == ArraySize){
                    index = 0;
                }
                while (index != last){
                    if (this.AnyTypeArray[index] !=
                            objectArrayNode.AnyTypeArray[index]){
                        return false;
                    }
                    index += 1;
                    if (index == ArraySize){
                        index = 0;
                    }
                }
                return true;
            }
            return false;
        }
    }

    public ArrayDeque(){
        this.sentinel = new ArrayNode(null, null);
        this.sentinel.prev = sentinel;
        this.sentinel.next = sentinel;
        this.size = 0;
    }

    public void addFirst(AnyType item){
        int index = sentinel.arrayAddFirst(item);
        // Add item to first array
        // If array is full, then create a new arrayNode before first arrayNode,
        // and point sentinel to this new arrayNode.
        // Then add the item into the new arrayNode.
        if (index < 0){
            sentinel = new ArrayNode(sentinel.prev, sentinel);
            sentinel.next.prev = sentinel;
            sentinel.prev.next = sentinel;
            sentinel.arrayAddFirst(item);
        }
        // add one item
        size += 1;
    }

    public void addLast(AnyType item){
        int index = sentinel.prev.arrayAddLast(item);
        // Add item to last array
        // If array is full, then create a new arrayNode after last arrayNode,
        // Then add the item into the new arrayNode.
        if (index < 0){
            sentinel.prev = new ArrayNode(sentinel.prev, sentinel);
            sentinel.prev.prev.next = sentinel.prev;
            sentinel.prev.arrayAddLast(item);
        }
        // add one item
        size += 1;
    }

    public boolean isEmpty(){
        if (sentinel.next == sentinel){
            if (sentinel.arrayIsEmpty() == true){
                return true;
            }
        }
        return false;
    }

    public int size(){
        return size;
    }

    public void printDeque(){
        System.out.print("sentinel");
        sentinel.printArray();
        ArrayNode pointer = sentinel.next;
        while (pointer != sentinel){
            pointer.printArray();
            pointer = pointer.next;
        }
    }

    public AnyType removeFirst(){
        if (this.isEmpty() == true){
            return null;
        }
        AnyType firstItem = sentinel.arrayRemoveFirst();
        // check if this arrayNode is empty, if it is, remove it.
        if (sentinel.arrayIsEmpty() == true){
            sentinel.prev.next = sentinel.next;
            sentinel.next.prev = sentinel.prev;
            sentinel = sentinel.next;
        }
        // remove one item
        size -= 1;
        return firstItem;
    }

    public AnyType removeLast(){
        if (this.isEmpty() == true){
            return null;
        }
        AnyType lastItem = sentinel.prev.arrayRemoveLast();
        // check if this arrayNode is empty, if it is, remove it.
        if (sentinel.prev.arrayIsEmpty() == true){
            sentinel.prev.prev.next = sentinel;
            sentinel.prev = sentinel.prev.prev;
        }
        // remove one item
        size -= 1;
        return lastItem;
    }

    public AnyType get(int index){
        if (index < size){
            ArrayNode pointer = sentinel;
            while (index >= ArrayNode.ArraySize){
                pointer = pointer.next;
                index -= ArrayNode.ArraySize;
            }
            return pointer.arrayGet(index);
        }
        return null;
    }

    /**
     * I don't know what is iterator, maybe I should come back and write this
     * after I learn the concept of iterator in Lecture 11.
     */
//    public Iterator<AnyType> iterator(){
//        return null;
//    }

    public boolean equals(Object o){
        if (o instanceof ArrayDeque<?>){
            ArrayNode thisPointer = this.sentinel;
            ArrayDeque<?>.ArrayNode oPointer = ((ArrayDeque<?>) o).sentinel;
            if (thisPointer.equals(oPointer) == true){
                thisPointer = thisPointer.next;
                oPointer = oPointer.next;
                while (thisPointer != sentinel){
                    if (thisPointer.equals(oPointer) == false){
                        return false;
                    }
                    thisPointer = thisPointer.next;
                    oPointer = oPointer.next;
                }
                return true;
            }
        }
        return false;
    }
}