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
        myHashMap = new MyHashMap<>();
        numberOfEntries = 10;
        bottomLimit = 3;
        upperLimit = 8;
    }

    @BeforeEach
    void createFullHashMap() {
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
        myHashMap.clear();
        keys.clear();
    }

    @AfterAll
    static void destroy() {
        myHashMap = null;
        keys = null;
    }
}