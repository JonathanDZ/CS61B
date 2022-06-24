package hashmap;

import java.util.*;

/**
 *  A hash table-backed Map implementation. Provides amortized constant time
 *  access to elements via get(), remove(), and put() in the best case.
 *
 *  Assumes null keys will never be inserted, and does not resize down upon remove().
 *  @author YOUR NAME HERE
 */
public class MyHashMap<K, V> implements Map61B<K, V> {

    /**
     * Protected helper class to store key/value pairs
     * The protected qualifier allows subclass access
     */
    protected class Node {
        K key;
        V value;

        Node(K k, V v) {
            key = k;
            value = v;
        }
    }

    /* Instance Variables */
    private Collection<Node>[] buckets;
    // You should probably define some more!
    private int elementSize;
    private int bucketSize;
    private double loadFactor;

    /** Constructors */
    public MyHashMap() {
        this(16, 0.75);
    }

    public MyHashMap(int initialSize) {
        this(initialSize, 0.75);
    }

    /**
     * MyHashMap constructor that creates a backing array of initialSize.
     * The load factor (# items / # buckets) should always be <= loadFactor
     *
     * @param initialSize initial size of backing array
     * @param maxLoad maximum load factor
     */
    public MyHashMap(int initialSize, double maxLoad) {
        elementSize = 0;
        bucketSize = initialSize;
        loadFactor = maxLoad;
        buckets = createTable(initialSize);
        for (int i = 0; i < initialSize; i+=1) {
            buckets[i] = createBucket();
        }
    }

    /**
     * Returns a new node to be placed in a hash table bucket
     */
    private Node createNode(K key, V value) {
        return new Node(key, value);
    }

    /**
     * Returns a data structure to be a hash table bucket
     *
     * The only requirements of a hash table bucket are that we can:
     *  1. Insert items (`add` method)
     *  2. Remove items (`remove` method)
     *  3. Iterate through items (`iterator` method)
     *
     * Each of these methods is supported by java.util.Collection,
     * Most data structures in Java inherit from Collection, so we
     * can use almost any data structure as our buckets.
     *
     * Override this method to use different data structures as
     * the underlying bucket type
     *
     * BE SURE TO CALL THIS FACTORY METHOD INSTEAD OF CREATING YOUR
     * OWN BUCKET DATA STRUCTURES WITH THE NEW OPERATOR!
     */
    protected Collection<Node> createBucket() {
        return new LinkedList<>();
    }

    /**
     * Returns a table to back our hash table. As per the comment
     * above, this table can be an array of Collection objects
     *
     * BE SURE TO CALL THIS FACTORY METHOD WHEN CREATING A TABLE SO
     * THAT ALL BUCKET TYPES ARE OF JAVA.UTIL.COLLECTION
     *
     * @param tableSize the size of the table to create
     */
    private Collection<Node>[] createTable(int tableSize) {
        return new Collection[tableSize];
    }

    // TODO: Implement the methods of the Map61B Interface below
    // Your code won't compile until you do so!
    @Override
    public void clear() {
//        for (int i = 0; i < bucketSize; i += 1) {
//            buckets[i].clear();
//        }
        int newBucketSize = 16;
        Collection<Node>[] newBuckets = createTable(newBucketSize);
        for (int i = 0; i < newBucketSize; i+=1) {
            newBuckets[i] = createBucket();
        }
        buckets = newBuckets;
        elementSize = 0;
        bucketSize = 16;
    }

    /**
     * A helper function to calculate the bucket index of the given key.
     * @param key the input key
     * @return the index of given key
     */
    public int findBucketIndex(K key) {
        int hashcode = key.hashCode();
        int index = hashcode % bucketSize;
        if (index < 0) {
            index += bucketSize;
        }
        return index;
    }

    @Override
    public boolean containsKey(K key) {
        int bucketIndex = findBucketIndex(key);
        for (Node node: buckets[bucketIndex]) {
            if (node.key.equals(key)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public V get(K key) {
        int bucketIndex = findBucketIndex(key);
        for (Node node: buckets[bucketIndex]) {
            if (node.key.equals(key)) {
                return node.value;
            }
        }
        return null;
    }

    @Override
    public int size() {
        return elementSize;
    }

    /**
     * resize the buckets when elementSize/bucketSize exceed 3/4
     */
    public void resizeBuckets() {
        int newBucketSize = 2 * bucketSize;
        Collection<Node>[] newBuckets = createTable(newBucketSize);
        for (int i = 0; i < newBucketSize; i+=1) {
            newBuckets[i] = createBucket();
        }
        for (int i = 0; i < bucketSize; i += 1) {
            for (Node node: buckets[i]) {
                int hashcode = node.key.hashCode();
                int newIndex = hashcode % newBucketSize;
                if (newIndex < 0) {
                    newIndex += newBucketSize;
                }
                newBuckets[newIndex].add(node);
            }
        }

        bucketSize = newBucketSize;
        buckets = newBuckets;
    }

    @Override
    public void put(K key, V value) {
        int bucketIndex = findBucketIndex(key);
        boolean foundKey = false;
        for (Node node: buckets[bucketIndex]) {
            if (node.key.equals(key)) {
                node.value = value;
                foundKey = true;
                break;
            }
        }
        if (!foundKey) {
            Node newNode = createNode(key, value);
            buckets[bucketIndex].add(newNode);
            elementSize += 1;
            double currentLoad = (double) elementSize / bucketSize;
            if (currentLoad > loadFactor) {
                resizeBuckets();
            }
        }
    }

    @Override
    public Set<K> keySet() {
        Set<K> keySet = new HashSet<>();
        for (int i = 0; i < bucketSize; i += 1) {
            for (Node node: buckets[i]) {
                keySet.add(node.key);
            }
        }
        return keySet;
    }

    private class MyHashMapIterator implements Iterator<K> {
        private int index;
        private K[] keyArray;

        public MyHashMapIterator() {
            index = 0;
            keyArray = (K[]) keySet().toArray();
        }

        @Override
        public boolean hasNext() {
            return index < elementSize;
        }

        @Override
        public K next() {
            K item = keyArray[index];
            index += 1;
            return item;
        }
    }

    @Override
    public Iterator<K> iterator() {
        return new MyHashMapIterator();
    }

    @Override
    public V remove(K key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public V remove(K key, V value) {
        throw new UnsupportedOperationException();
    }

}
