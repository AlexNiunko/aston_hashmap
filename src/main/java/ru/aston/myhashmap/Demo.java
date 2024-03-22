package ru.aston.myhashmap;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Demo {
    public static void main(String[] args) {
        String [] names={"Alex","Michail","Daria","Natalia"};
        HashMap<String,Integer>hashMap=new HashMap<>();
        Set<Map.Entry<String, Integer>> set=hashMap.entrySet();
        MyHashMap<String,Integer>myHashMap=new MyHashMap<>();
        for (int i = 0; i < names.length; i++) {
            myHashMap.put(names[i],i );
            System.out.println(names[i]+" "+i);
        }
        System.out.println(myHashMap.size());
        System.out.println(set);

    }
}
