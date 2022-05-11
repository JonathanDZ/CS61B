package bstmap;

import java.util.Iterator;
import java.util.Set;

public class BSTMap<K extends Comparable, V> implements Map61B<K, V> {
    BSTNode root;
    int size = 0;

    private class BSTNode {
        BSTNode leftChild;
        BSTNode rightChild;

        K key;
        V value;

        public BSTNode(K k, V v) {
            this.key = k;
            this.value = v;
        }

        public BSTNode get(BSTNode T, K key) {
            if (T == null) {
                return null;
            }
            if (key.equals(T.key)) {
                return T;
            }
            if (key.compareTo(T.key) < 0) {
                return get(T.leftChild, key);
            }else {
                return get(T.rightChild, key);
            }
        }

        public BSTNode insert(BSTNode T, K key, V value) {
            if (T == null) {
                size += 1;
                return new BSTNode(key, value);
            }
            if (key.equals(T.key)) {
                T.value = value;
            } else if (key.compareTo(T.key) < 0) {
                T.leftChild = insert(T.leftChild, key, value);
            } else {
                T.rightChild = insert(T.rightChild, key, value);
            }
            return T;
        }

    }
    /**
     * Removes all of the mappings from this map.
     */
    @Override
    public void clear() {
        root = null;
        size = 0;
    }

    /**
     * Returns true if this map contains a mapping for the specified key
     */
    @Override
    public boolean containsKey(K key) {
        if (key == null) {
            return false;
        }
        if (root == null) {
            return false;
        }
        BSTNode lookup = root.get(root, key);
        return lookup != null;
    }

    /**
     * Returns the value to which the specified key is mapped, or null if this
     * map contains no mapping for the key.
     */
    @Override
    public V get(K key) {
        if (key == null) {
            return null;
        }
        if (root == null) {
            return null;
        }
        BSTNode lookup = root.get(root, key);
        if (lookup == null) {
            return null;
        }
        return lookup.value;
    }

    /** Returns the number of key-value mappings in this map. */
    @Override
    public int size() {
        return size;
    }

    /** Associates the specified value with the specified key in this map. */
    @Override
    public void put(K key, V value) {
        if (key == null) {
            return;
        }
        if (root == null) {
            root = new BSTNode(key,value);
            size += 1;
            return;
        }
        root = root.insert(root, key, value);
    }

    /**
     * Returns a Set view of the keys contained in this map. Not required for Lab 7.
     * If you don't implement this, throw an UnsupportedOperationException. */
    @Override
    public Set<K> keySet() {
        throw new UnsupportedOperationException();
    }

    /**
     * Removes the mapping for the specified key from this map if present.
     * Not required for Lab 7. If you don't implement this, throw an
     * UnsupportedOperationException. */
    @Override
    public V remove(K key) {
        throw new UnsupportedOperationException();
    }

    /**
     * Removes the entry for the specified key only if it is currently mapped to
     * the specified value. Not required for Lab 7. If you don't implement this,
     * throw an UnsupportedOperationException.*/
    @Override
    public V remove(K key, V value) {
        throw new UnsupportedOperationException();
    }

    /**
     * Returns an iterator over elements of type {@code T}.
     *
     * @return an Iterator.
     */
    @Override
    public Iterator<K> iterator() {
        throw new UnsupportedOperationException();
    }

    /**
     * prints out your BSTMap in order of increasing Key.
     * We will not test the result of this method,
     * but you will find this helpful for testing your implementation!
     */
    public void printInOrder() {
        return;
    }

}
