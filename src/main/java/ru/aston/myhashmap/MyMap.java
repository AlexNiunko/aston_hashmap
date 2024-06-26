package ru.aston.myhashmap;

import java.util.Set;

public interface MyMap <K,V> {
    V get(Object obj);
    V put(K key,V value);

    V remove(Object key);

    void clear();

    boolean containsKey(Object key);
    boolean containsValue(Object value);
    int size();
    Set<MyMap.Entry<K,V>> entrySet();
    interface Entry<K,V>{
        K getKey();
        V getValue();
        V setValue(V obj);
    }

}
