package ru.aston.myhashmap;

import org.junit.jupiter.api.*;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class MyHashMapTest {
    private static Set<String> keys;
    private static Random random;
    private static MyHashMap<String, Integer> myHashMap;
    private static Set<Integer> values;
    private static int numberOfEntries;
    private static int bottomLimit;
    private static int upperLimit;



    private String generateRandomLine(Random random, int bottomLimit, int upperLimit) {
        int lineSize = bottomLimit + random.nextInt(upperLimit);
        int count = 0;
        char[] array = new char[lineSize];
        char ch;
        while (count < lineSize) {
            ch = (char) (65 + random.nextInt(25));
            if (ch != array[count]) {
                array[count] = ch;
                count++;
            }
        }
        return String.valueOf(array);
    }

    private Set<String> makeSetOfKeys(int number, int bottomBorder, int upperBorder) {
        Set<String> set = new HashSet<>();
        while (set.size() < number) {
            set.add(generateRandomLine(random, bottomBorder, upperBorder));
        }
        return set;
    }

    private Set<Integer> makeSetOfValues(Set<String> keys) {
        Set<Integer> set = new HashSet<>();
        for (String item : keys) {
            set.add(calculateValue(item));
        }
        return set;
    }

    private int fillHashMap(MyHashMap<String, Integer> hashMap) {
        int count = 0;
        for (String line : keys) {
            if (hashMap.put(line, calculateValue(line)) == null) {
                count++;
            }
        }
        return count;
    }

    private int calculateValue(String line) {
        return line.length() * line.charAt(0);
    }

    private int calculateAnotherValue(String line) {
        return line.length() + (int) line.charAt(0);
    }


    @BeforeAll
    static void init() {
        random = new Random();
        numberOfEntries = 10;
        bottomLimit = 3;
        upperLimit = 4;
    }

    @BeforeEach
    void createFullHashMap() {
        myHashMap = new MyHashMap<>();
        keys = makeSetOfKeys(numberOfEntries, bottomLimit, upperLimit);
        values = makeSetOfValues(keys);
        numberOfEntries = fillHashMap(myHashMap);
    }


    @Test
    void getValueIfThisVaLueIsExist() {
        int number = random.nextInt(keys.size());
        String key = (String) keys.toArray()[number];
        Integer value = calculateValue(key);
        Assertions.assertEquals(value, myHashMap.get(key));
    }

    @Test
    void getValueIfKeyIsNotExist() {
        String key = generateRandomLine(random, upperLimit, upperLimit + bottomLimit);
        Assertions.assertNull(myHashMap.get(key));
    }


    @Test
    void clearAllMap() {
        myHashMap.clear();
        Assertions.assertNotEquals(myHashMap.size(), numberOfEntries);
        int number = random.nextInt(keys.size());
        String key = (String) keys.toArray()[number];
        Assertions.assertNull(myHashMap.get(key));
    }

    @Test
    void size() {
        int size = myHashMap.size();
        Assertions.assertEquals(numberOfEntries, size);
    }

    @Test
    void entrySetIfAllAreExist() {
        Set<String> actualKeys = new HashSet<>();
        Set<Integer> actualValues = new HashSet<>();
        for (MyMap.Entry<String, Integer> item : myHashMap.entrySet()) {
            actualKeys.add(item.getKey());
            actualValues.add(item.getValue());
        }
        Assertions.assertAll(
                () -> assertEquals(values, actualValues),
                () -> assertEquals(keys, actualKeys)
        );
    }

    @Test
    void putIfKeyIsExist() {
        int number = random.nextInt(keys.size());
        String key = (String) keys.toArray()[number];
        int trueValue = calculateValue(key);
        int anotherValue = calculateAnotherValue(key);
        Assertions.assertAll(
                () -> assertEquals(trueValue, myHashMap.put(key, anotherValue)),
                () -> assertEquals(anotherValue, myHashMap.get(key))
        );
    }

    @Test
    void putIfKeyIsNotExist() {
        String key = generateRandomLine(random, upperLimit, upperLimit + bottomLimit);
        int value = calculateValue(key);
        Assertions.assertAll(
                () -> assertNull(myHashMap.put(key, value)),
                () -> assertEquals(value, myHashMap.get(key))
        );
    }


    @Test
    void removeIfKeyIsExist() {
        int number = random.nextInt(keys.size());
        String key = (String) keys.toArray()[number];
        int expectedValue = calculateValue(key);
        Assertions.assertAll(
                () -> assertEquals(expectedValue, myHashMap.remove(key)),
                () -> assertNull(myHashMap.get(key))
        );
    }
    @Test
    void removeIfKeyIsNotExist() {
        String key = generateRandomLine(random,upperLimit,bottomLimit+upperLimit);
        Assertions.assertNull(myHashMap.remove(key));

    }


    @Test
    void containsKeyIfKeyIsExist() {
        int number = random.nextInt(keys.size());
        String key = (String) keys.toArray()[number];
        Assertions.assertTrue(myHashMap.containsKey(key));
    }

    @Test
    void containsValueIfValueIsExist() {
        int number = random.nextInt(keys.size());
        String key = (String) keys.toArray()[number];
        int value=calculateValue(key);
        Assertions.assertTrue(myHashMap.containsValue(value));
    }
    @Test
    void containsKeyIfKeyIsNotExist() {
        String key = generateRandomLine(random, upperLimit, upperLimit + bottomLimit);
        Assertions.assertFalse(myHashMap.containsKey(key));
    }

    @Test
    void containsValueIfValueIsNotExist() {
        int number = random.nextInt(keys.size());
        String key = (String) keys.toArray()[number];
        int value=calculateAnotherValue(key);
        Assertions.assertFalse(myHashMap.containsValue(value));
    }

    @AfterEach
    void destroyMap() {
        myHashMap=null;
        keys=null;
        values=null;
    }

    @AfterAll
    static void destroy() {
    }
    @Nested
    class MyHashMapTestConstructorsWithIllegalParameters {
        @Test
        void createWithNegativeCapacity() {
            Assertions.assertThrows(IllegalArgumentException.class, () -> new MyHashMap<String, Integer>(-5));
        }
        @Test
        void createWithNegativeLoadFactor(){
            Assertions.assertThrows(IllegalArgumentException.class, () -> new MyHashMap<String, Integer>(15,-0.6f));
        }
    }
    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class MyHashMapTestWithSameKey{
        @BeforeAll
        void init(){
            random=new Random();
            bottomLimit=3;
            upperLimit=4;
            numberOfEntries=1;
        }
        @BeforeEach
        void prepare(){
            myHashMap=new MyHashMap<>(2);
            keys=makeSetOfKeys(numberOfEntries,bottomLimit,upperLimit);
            values=makeSetOfValues(keys);
            numberOfEntries=fillHashMap(myHashMap);
        }
        @Test
        void putWithSameIndex(){
            int number = random.nextInt(keys.size());
            String key = (String) keys.toArray()[number];
            String newKey=key+" ";
            int newValue=calculateValue(newKey);
            Assertions.assertNull(myHashMap.put(newKey,newValue));
        }
        @Test
        void calculateIndexIfTabSizeNegative(){
            int number = random.nextInt(keys.size());
            String key = (String) keys.toArray()[number];
            int tabSize=-5;
            Assertions.assertThrows(IllegalArgumentException.class,()->MyHashMap.calculateIndex(key,tabSize));
        }
        @Test
        void getWithTheSameIndex(){
            int number = random.nextInt(keys.size());
            String key = (String) keys.toArray()[number];
            String newKey=key+" ";
            int value=calculateValue(newKey);
            myHashMap.put(newKey,value);
            Assertions.assertEquals(value,myHashMap.get(newKey));
        }
        @AfterEach
        void destroyMap(){
            myHashMap=null;
            keys=null;
            values=null;
        }

    }
//    @Nested
//    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
//    class MyHashMapTestCreationWithMaximumCapacity{
//        @BeforeAll
//        void init(){
//            random=new Random();
//            bottomLimit=1;
//            upperLimit=9;
//            numberOfEntries=MAXIMUM_CAPACITY;
//        }
//        @BeforeEach
//        void createMap(){
//            myHashMap=new MyHashMap<>(numberOfEntries);
//            keys=makeSetOfKeys(100,bottomLimit,upperLimit);
//            values=makeSetOfValues(keys);
//        }
//        @Test
//        void putWithMaxCapacity(){
//            String key = generateRandomLine(random, upperLimit, upperLimit + bottomLimit);
//            int value=calculateValue(key);
//            Assertions.assertNull(myHashMap.put(key,value));
//        }
//        @AfterEach
//        void destroy(){
//            myHashMap=null;
//            keys=null;
//            values=null;
//        }
//        @AfterAll
//        void delete(){
//            random=null;
//        }
//    }


}