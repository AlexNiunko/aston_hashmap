package ru.aston.myhashmap;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class MyHashMap<K,V> implements MyMap<K,V> {
    private static final int DEFAULT_CAPACITY = 16;
    private static final int MAXIMUM_CAPACITY = 1_073_741_823;
    private static final float DEFAULT_LOAD_FACTOR = 0.75f;
    private int threshold;
    private final float loadFactor;
    private Node<K, V>[] table;
    private int size;

    static class Node<K, V> implements MyMap.Entry<K, V> {
        final int hash;
        final K key;
        V value;
        Node<K, V> next;


        public Node(int hash, K key, V value, Node<K, V> next) {
            this.hash = hash;
            this.key = key;
            this.value = value;
            this.next = next;
        }

        public Node(int hash, K key, V value) {
            this.hash = hash;
            this.key = key;
            this.value = value;
        }


        @Override
        public K getKey() {
            return this.key;
        }

        @Override
        public V getValue() {
            return this.value;
        }

        @Override
        public V setValue(V newValue) {
            V oldValue = value;
            value = newValue;
            return oldValue;
        }


        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Node<?, ?> node = (Node<?, ?>) o;
            return Objects.equals(key, node.key) && Objects.equals(value, node.value);
        }

        @Override
        public int hashCode() {
            return Objects.hash(key, value);
        }

        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder();
            sb.append("key=").append(key);
            sb.append(", value=").append(value);
            return sb.toString();
        }
    }

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

    public MyHashMap(int initialCapacity) {
        this(initialCapacity, DEFAULT_LOAD_FACTOR);
    }

    public MyHashMap() {
        this.loadFactor = DEFAULT_LOAD_FACTOR;
    }


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

    @Override
    public V put(K key, V value) {
        Node<K, V>[] tab;
        int n, index;
        if ((tab = table) == null || (n = tab.length) == 0) {
            n = (tab = increaseSize()).length;
        }
        int hash = hash(key);
        index = calculateIndex(key, n);
        Node<K, V> node = this.table[index];
        if (node != null) {
            if (node.key.equals(key)) {
                return node.setValue(value);
            } else {
                while (node.next != null) {
                    node = node.next;
                }
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

    @Override
    public void clear() {
        Node<K, V>[] tab = this.table;
        if (tab != null && this.size > 0) {
            for (int i = 0; i < tab.length; i++) {
                tab[i] = null;
            }
        }
        this.size=0;
    }

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

    @Override
    public int size() {
        return this.size;
    }

    @Override
    public Set<MyMap.Entry<K, V>> entrySet() {
        Node<K,V> []tab=this.table;
        Set<MyMap.Entry<K,V>>entries=new HashSet<>();
        if (size>0){
            for (int i = 0; i < tab.length; i++) {
                Node<K,V>node=tab[i];
                if (node!=null && node.next==null){
                    entries.add(node);
                } else if (node!=null && node.next!=null) {
                    entries.add(node);
                    Node<K,V>temp=node.next;
                    while (temp!=null){
                        entries.add(temp);
                        temp=temp.next;
                    }
                }
            }
        }
        return entries;
    }

    private final int hash(Object key) {
        int h;
        return (key == null) ? 0 : (h = key.hashCode()) ^ (h >>> 16);
    }

    private final int calculateIndex(Object key, int tabSize) {
        if (tabSize < 0) {
            throw new IllegalArgumentException("The negative table size " + tabSize);
        }
        return (tabSize - 1) & hash(key);
    }

    private int sizeCalculation(int capacity) {
        int x = 2;
        int power = 1;
        while (Math.pow(x, power) < capacity) {
            power++;
        }
        return (int) Math.pow(x, power);
    }

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MyHashMap)) return false;
        MyHashMap<?, ?> myHashMap = (MyHashMap<?, ?>) o;
        return threshold == myHashMap.threshold && Float.compare(myHashMap.loadFactor, loadFactor) == 0 && size == myHashMap.size && Arrays.equals(table, myHashMap.table);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(threshold, loadFactor, size);
        result = 31 * result + Arrays.hashCode(table);
        return result;
    }

    @Override
    public String toString() {
        Node<K,V> []tab=this.table;
        final StringBuilder sb = new StringBuilder("MyHashMap{");
        for (Node<K,V> node:tab) {
            if (node!=null){
                if (node.next==null){
                    sb.append("[Key:"+node.key+" /Value: "+node.value+"],");
                } else {
                    sb.append("[Key:"+node.key+" /Value: "+node.value+"],");
                    Node<K,V>temp=node.next;
                    while (temp!=null){
                        sb.append("[Key:"+temp.key+" /Value: "+temp.value+"],");
                        temp=temp.next;
                    }
                }
            }
        }
        return sb.toString();
    }
}
