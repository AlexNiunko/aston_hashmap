package ru.aston.myhashmap;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * This class is a tutorial project and represents my implementation of hash table,
 * contains basic methods for working with this data structure;
 * author - Aliaksandr Niunko;
 *
 * @param <K> is the type of keys maintained by this map
 * @param <V> is the type of mapped values
 */
public class MyHashMap<K, V> implements MyMap<K, V> {
    /**
     * This constant is a default initial capacity
     */
    private static final int DEFAULT_CAPACITY = 16;
    /**
     * This constant is a maximum possible hash table capacity
     */
    private static final int MAXIMUM_CAPACITY = 1_073_741_823;
    /**
     * This constant is a default load factor;y
     */
    private static final float DEFAULT_LOAD_FACTOR = 0.75f;
    /**
     * the limit on the number of elements, upon reaching which the size of the hash table doubles.
     * Calculated using the formula (capacity * loadFactor);
     */

    private int threshold;
    /**
     * This field determines at what level of load the current hash table needs to create a new hash table,
     * i.e. as soon as the hash table is 75% full, a new hash table will be created and the current elements will be moved into it
     * (a costly operation, since all elements must be rehashed);
     */

    private final float loadFactor;
    /**
     * This is the hash table itself, implemented in an array, to store key-value pairs as nodes. This is where our Nodes are stored;
     */
    private Node<K, V>[] table;
    /**
     * It's just a number of key-value pairs
     */
    private int size;

    /**
     * This is a nested Node class describing our key-value pair
     *
     * @param <K> is the type of keys
     * @param <V> is the type of value
     */
    static class Node<K, V> implements MyMap.Entry<K, V> {
        /**
         * This is the hashcode value of our key, calculated using the private method hash ,described below;
         */
        final int hash;
        /**
         * This is the key value;
         */
        final K key;
        /**
         * This is the  value;
         */
        V value;
        /**
         * This is a link to the next node located in the same bucket (cell of our array);
         */
        Node<K, V> next;

        /**
         * This is the constructor which creates our node using all fields;
         */
        public Node(int hash, K key, V value, Node<K, V> next) {
            this.hash = hash;
            this.key = key;
            this.value = value;
            this.next = next;
        }

        /**
         * This is the constructor which creates our node using all fields except link to next node;
         */
        public Node(int hash, K key, V value) {
            this.hash = hash;
            this.key = key;
            this.value = value;
        }

        /**
         * This method returns the value of key;
         */
        @Override
        public K getKey() {
            return this.key;
        }

        /**
         * This method returns the value;
         */

        @Override
        public V getValue() {
            return this.value;
        }

        /**
         * This method sets a new value (passed in arguments)
         * of the value field and returns the value of the old one;
         * This method has one argument - V newValue;
         */
        @Override
        public V setValue(V newValue) {
            V oldValue = value;
            value = newValue;
            return oldValue;
        }


        /**
         * An overridden method compares two objects of a given class;
         */
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Node<?, ?> node = (Node<?, ?>) o;
            return Objects.equals(key, node.key) && Objects.equals(value, node.value);
        }

        /**
         * An overridden method returns hashcode of an object of this class;
         */
        @Override
        public int hashCode() {
            return Objects.hash(key, value);
        }

        /**
         * The overridden method returns a string describing the object of this class.
         */
        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder();
            sb.append("key=").append(key);
            sb.append(", value=").append(value);
            return sb.toString();
        }
    }

    /**
     * This constructor creates a hash map with the given parameters: initial capacity and load factor.
     *
     * @param initialCapacity
     * @param loadFactor
     */

    public MyHashMap(int initialCapacity, float loadFactor) {
        if (initialCapacity < 0) {
            throw new IllegalArgumentException("Illegal initial capacity: " +
                    initialCapacity);
        }
        if (initialCapacity > MAXIMUM_CAPACITY) {
            initialCapacity = MAXIMUM_CAPACITY;
        }
        if (loadFactor <= 0 || Float.isNaN(loadFactor))
            throw new IllegalArgumentException("Illegal load factor: " +
                    loadFactor);
        this.loadFactor = loadFactor;
        this.threshold = sizeCalculation(initialCapacity);

    }

    /**
     * This constructor creates a hash map with the one given parameters: initial capacity and default load factor.
     *
     * @param initialCapacity
     */
    public MyHashMap(int initialCapacity) {
        this(initialCapacity, DEFAULT_LOAD_FACTOR);
    }

    /**
     * This constructor creates a hash map with the  default load factor and empty capacity,
     * after creating an object using this constructor, the value of the table field is null;
     */
    public MyHashMap() {
        this.loadFactor = DEFAULT_LOAD_FACTOR;
    }

    /**
     * This method returns a value from our hash table corresponding to the key passed into the arguments of this method;
     *
     * @param inputKey
     * @return
     */
    @Override
    public V get(Object inputKey) {
        Node<K, V>[] tab = this.table;
        int n = tab.length;
        int hash = hash(inputKey);
        int index = calculateIndex(inputKey, n);
        if (n != 0) {
            Node<K, V> first = tab[index];
            if (first != null) {
                if (first.hash == hash && (inputKey != null && inputKey.equals(first.key))) {
                    return first.value;
                }
                if (first.next != null) {
                    Node<K, V> temp = first.next;
                    while (temp != null) {
                        if (temp.hash == hash && (inputKey != null && temp.key.equals(inputKey))) {
                            return temp.value;
                        }
                        temp = temp.next;
                    }
                }
            }
        }
        return null;
    }

    /**
     * This method places in our hash table the value corresponding to the key, if the key already exists in the hash table,
     * then the value corresponding to it will be replaced by the one passed as an argument, the old value will be returned,
     * otherwise the method will return null;
     *
     * @param key
     * @param value
     * @return null if there is no key, or the value corresponding to the key;
     */
    @Override
    public V put(K key, V value) {
        Node<K, V>[] tab;
        int n, index;
        if ((tab = table) == null || (n = tab.length) == 0) {
            n = (tab = increaseSize()).length;
        }
        /*
         *calculates the key hashcode
         */
        int hash = hash(key);
        /*
         *calculates the index in hash table
         */
        index = calculateIndex(key, n);
        Node<K, V> node = this.table[index];
        /*
         *check if there is an entry in the cell with this index
         */
        if (node != null) {
            if (node.key.equals(key)) {
                /*
                 if yes, then we check the equality of the entry key to the one passed in the arguments,
                 if true, then we replace it with a new one and return the old one
                 */
                return node.setValue(value);
            } else {
                /*
                 *If the keys are not equal, then we move through the singly linked list until we find a null link to the next element
                 */
                while (node.next != null) {
                    node = node.next;
                }
                /*
                 *We put entry there
                 */
                Node<K, V> nodeInOldBucket = new Node<>(hash, key, value);
                node.next = nodeInOldBucket;
                this.size++;
                return null;
            }
        } else {
            Node<K, V> nodeInNewBucket = new Node<>(hash, key, value);
            this.table[index] = nodeInNewBucket;
            this.size++;
        }
        if (size > threshold) {
            this.table = increaseSize();
        }
        return null;
    }

    /**
     * This method deletes the value corresponding to the key passed as a parameter, returns the deleted valueж
     *
     * @param key
     * @return removed value
     */
    @Override
    public V remove(Object key) {
        Node<K, V>[] tab = this.table;
        Node<K, V> node = null;
        int n = table.length;
        int index = 0;
        int keyHash = hash(key);
        if (n > 0) {
            index = calculateIndex(key, n);
            node = tab[index];
        }
        if (node != null) {
            if (node.hash == keyHash && (key != null && key.equals(node.key))) {
                tab[index] = null;
                size--;
                return node.value;
            } else if (node.next != null) {
                Node<K, V> temp = node.next;
                while (temp != null) {
                    if (temp.hash == keyHash && (key != null && key.equals(temp.key))) {
                        node.next = temp.next;
                        V value = temp.value;
                        temp = null;
                        size--;
                        return value;
                    }
                }
            }
        }
        return null;
    }

    /**
     * Clears our table;
     */
    @Override
    public void clear() {
        Node<K, V>[] tab = this.table;
        if (tab != null && this.size > 0) {
            for (int i = 0; i < tab.length; i++) {
                tab[i] = null;
            }
        }
        this.size = 0;
    }

    /**
     * Checks whether the hash table contains the key passed as a parameter;
     *
     * @param key
     * @return true if key is exist or false if key is not exist
     */
    @Override
    public boolean containsKey(Object key) {
        Node<K, V>[] tab = this.table;
        if (tab != null && size > 0) {
            for (int i = 0; i < tab.length; i++) {
                Node<K, V> temp = tab[i];
                while (temp != null) {
                    if (temp.key.equals(key)) {
                        return true;
                    }
                    temp = temp.next;
                }
            }
        }
        return false;
    }

    /**
     * Checks whether the hash table contains the value passed as a parameter;
     *
     * @param value
     * @return true if key is exist or false if key is not exist
     */
    @Override
    public boolean containsValue(Object value) {
        Node<K, V>[] tab = this.table;
        if (tab != null && size > 0) {
            for (int i = 0; i < tab.length; i++) {
                Node<K, V> temp = tab[i];
                while (temp != null) {
                    if (temp.value.equals(value)) {
                        return true;
                    }
                    temp = temp.next;
                }
            }
        }
        return false;
    }

    /**
     * Returns the number of key value pairs
     *
     * @return number of key value pairs (int)
     */
    @Override
    public int size() {
        return this.size;
    }

    /**
     * Returns a set containing key-value pairs
     *
     * @return set
     */
    @Override
    public Set<MyMap.Entry<K, V>> entrySet() {
        Node<K, V>[] tab = this.table;
        Set<MyMap.Entry<K, V>> entries = new HashSet<>();
        if (size > 0) {
            for (int i = 0; i < tab.length; i++) {
                Node<K, V> node = tab[i];
                if (node != null && node.next == null) {
                    entries.add(node);
                } else if (node != null && node.next != null) {
                    entries.add(node);
                    Node<K, V> temp = node.next;
                    while (temp != null) {
                        entries.add(temp);
                        temp = temp.next;
                    }
                }
            }
        }
        return entries;
    }

    /**
     * calculates the hash code of the key;
     *
     * @param key
     * @return int
     */
    private final int hash(Object key) {
        int h;
        return (key == null) ? 0 : (h = key.hashCode()) ^ (h >>> 16);
    }

    /**
     * calculates the index code of the hash table;
     *
     * @param key
     * @return int
     */
    private final int calculateIndex(Object key, int tabSize) {
        if (tabSize < 0) {
            throw new IllegalArgumentException("The negative table size " + tabSize);
        }
        return (tabSize - 1) & hash(key);
    }

    /**
     * An auxiliary method that calculates the capacity of a hash table
     *
     * @param capacity
     * @return int
     */
    private int sizeCalculation(int capacity) {
        int x = 2;
        int power = 1;
        while (Math.pow(x, power) < capacity) {
            power++;
        }
        return (int) Math.pow(x, power);
    }

    /**
     * Increases the capacity of the hash table and transfers values from the old one there
     *
     * @return Node<K, V>[]
     */
    private final Node<K, V>[] increaseSize() {
        Node<K, V>[] oldTab = this.table;
        int oldCap = (oldTab == null) ? 0 : oldTab.length;
        int oldThr = this.threshold;
        int newCap, newThr = 0;
        if (oldCap > 0) {
            if (oldCap >= MAXIMUM_CAPACITY) {
                threshold = Integer.MAX_VALUE;
                return oldTab;
            } else if ((newCap = oldCap * 2) < MAXIMUM_CAPACITY && oldCap >= DEFAULT_CAPACITY) {
                newThr = oldThr * 2;
            }
        } else if (oldThr > 0) {
            newCap = oldThr;
        } else {
            newCap = DEFAULT_CAPACITY;
            newThr = (int) (DEFAULT_LOAD_FACTOR * DEFAULT_CAPACITY);
        }
        if (newThr == 0) {
            float ft = (float) newCap * loadFactor;
            newThr = (newCap < MAXIMUM_CAPACITY && ft < (float) MAXIMUM_CAPACITY ? (int) ft : Integer.MAX_VALUE);
        }
        this.threshold = newThr;
        Node<K, V>[] newTab = (Node<K, V>[]) new Node[newCap];
        this.table = newTab;
        for (int i = 0; i < oldCap; i++) {
            Node<K, V> node;
            if ((node = oldTab[i]) != null) {
                oldTab[i] = null;
                newTab[calculateIndex(node.key, newCap)] = node;
            }
        }
        return newTab;
    }

    /**
     * An overridden method compares two objects of a given class;
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MyHashMap)) return false;
        MyHashMap<?, ?> myHashMap = (MyHashMap<?, ?>) o;
        return threshold == myHashMap.threshold && Float.compare(myHashMap.loadFactor, loadFactor) == 0 && size == myHashMap.size && Arrays.equals(table, myHashMap.table);
    }

    /**
     * An overridden method returns hashcode of an object of this class;
     */
    @Override
    public int hashCode() {
        int result = Objects.hash(threshold, loadFactor, size);
        result = 31 * result + Arrays.hashCode(table);
        return result;
    }

    /**
     * The overridden method returns a string describing the object of this class.
     */
    @Override
    public String toString() {
        Node<K, V>[] tab = this.table;
        final StringBuilder sb = new StringBuilder("MyHashMap{");
        for (Node<K, V> node : tab) {
            if (node != null) {
                if (node.next == null) {
                    sb.append("[Key:" + node.key + " /Value: " + node.value + "],");
                } else {
                    sb.append("[Key:" + node.key + " /Value: " + node.value + "],");
                    Node<K, V> temp = node.next;
                    while (temp != null) {
                        sb.append("[Key:" + temp.key + " /Value: " + temp.value + "],");
                        temp = temp.next;
                    }
                }
            }
        }
        return sb.toString();
    }
}
