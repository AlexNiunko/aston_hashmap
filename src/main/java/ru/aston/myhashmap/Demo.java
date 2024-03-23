package ru.aston.myhashmap;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class Demo {
    public static void main(String[] args) {
      //Generate lines
      Random random=new Random();
      Set<String> set=new HashSet<>();
      while (set.size()<100){
          set.add(getRandomLine(random,4,6));
      }
        System.out.println(set);



    }

    private static String getRandomLine(Random random,int bottomLimit,int upperLimit) {
       int lineSize=bottomLimit+random.nextInt(upperLimit);
       int count=0;
       char [] array=new char[lineSize];
       char ch;
       while (count<lineSize){
           ch=(char)(65+random.nextInt(25));
           if (ch!=array[count]){
               array[count]=ch;
               count++;
           }
       }
       return String.valueOf(array);

    }

}
