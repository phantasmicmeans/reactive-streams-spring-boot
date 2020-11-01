package com.reactive.streams.demo.toby.duality;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class IterableOb {
    public void Iterable() {
        // Iterable < ---- > Observable (Duality)
        // Pull  < ---- > Push
        // (next()를 이용해 값을 끌어온다는 의미) < ---- > (값을 가져가라는 의미)

        List<Integer> list = Arrays.asList(1, 2, 3, 4, 5);
        for (Integer i : list) { // for-each
            System.out.println("[COLLECTION] " + i);
        }

        Iterable<Integer> iter = Arrays.asList(1, 2, 3, 4, 5);
        for (Integer i : list) {
            System.out.println("[ITER] " + i);
        }
        // for - each는 iterable에서 사용할 수 있다...
        // 즉 JAVA의 for-each는 컬렉션이 아닌 iterable을 구현한 무언가를 넣는 것!

        // Iterable 생성
        Iterable<Integer> newIter = () ->
                new Iterator<Integer>() {
                    int i = 0;
                    final static int MAX = 10;

                    public boolean hasNext() {
                        return i < MAX;
                    }

                    public Integer next() {
                        return ++i;
                    }
                };

        for (Integer i : newIter) {   // for-each
            System.out.println("[NEW ITER] " + i);
        }


        Iterable<Integer> iterable = () -> {
                return new Iterator<Integer>() {
                    int start = 0;
                    int MAX = 10;

                    @Override
                    public boolean hasNext() {
                        return this.start < MAX;
                    }

                    @Override
                    public Integer next() {
                        return this.start++;
                    }
                };
            };

        for (Iterator<Integer> iterator = iterable.iterator(); iterator.hasNext();) {
            System.out.println(iterator.next());
        }

        for (Integer i : iterable) { // 위와 동일
            System.out.println(i);
        }
    }
}
