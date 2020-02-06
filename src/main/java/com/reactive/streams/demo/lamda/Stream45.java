package com.reactive.streams.demo.lamda;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.math.BigInteger;
import java.util.*;
import java.util.function.BinaryOperator;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
public class Stream45 {
    static final String test1 = "aelpst";
    static final String test2 = "staple";
    static final String test3 = "stapel";

    public static void main(String[] args) {

        List<String> list = Arrays.asList(test1, test2, test3);

        Map<String, Set<String>> groups = new HashMap<>();
        for (String s : list) {
            groups.computeIfAbsent(alphabetize(s), (unused) -> new TreeSet<>()).add(s);
        }

        for (Set<String> group : groups.values()) {
            if (group.size() >= 1)
                log.info(group.size() + " : " + group);
        }

        // 수정
        //     public static <T, K> Collector<T, ?, Map<K, List<T>>>
        //    groupingBy(Function<? super T, ? extends K> classifier) {
        //        return groupingBy(classifier, toList());
        //    }

        list.stream().collect(Collectors.groupingBy(word -> alphabetize(word)))
                     .values()
                     .stream()
                     .filter(group -> group.size() >= 1)
                     .forEach(g -> log.info(g.size() +  " : " + g));

        //     Stream<T> sorted(Comparator<? super T> comparator);
        //groups.keySet().stream().sorted(Comparator.comparing())
        // primeTest();
        streamToMap();
        streamToMap2();
    }
    private static String alphabetize(String s) {
        char [] a = s.toCharArray();
        Arrays.sort(a);
        return new String(a);
    }

    static Stream<BigInteger> primes() {
        return Stream.iterate(BigInteger.valueOf(2), BigInteger::nextProbablePrime);
    }

    private static void primeTest() {
        primes().map(p -> BigInteger.ONE.pow(p.intValueExact()).subtract(BigInteger.ONE))
                .filter(mersenne -> mersenne.isProbablePrime(50))
                .limit(5)
                .forEach(System.out::println);
    }

    /**
     * Map 수집기
     * toMap(Key Mapper, Value Mapper)
     * Collector<T, ?, Map<K,U>> toMap(Function<? super T, ? extends K> keyMapper,
     *                                 Function<? super T, ? extends U> valueMapper)..
     */
    private static void streamToMap() {
        Stream.of(Operation.values())
                .collect(Collectors.toMap(Objects::toString, e -> e)); // key -> string, value -> value 그대로
        // 스트림 각 원소가 고유한 키에 매핑되어야 함
        // 원소 다수가 동일한 키 사용시 IllegalStateException
    }

    /**
     * Map 수집기 2 - 병합 함수 (BinaryOperator<U>)
     * Collector<T, ?, Map<K,U>> toMap(Function<? super T, ? extends K> keyMapper,
     *                                 Function<? super T, ? extends U> valueMapper,
     *                                 BinaryOperator<U> mergeFunction) {
     */
    private static void streamToMap2() {
        Artist artist1 = new Artist("A");
        Artist artist2 = new Artist("B");
        Artist artist3 = new Artist("C");

        Album firstAlbumArtist1 = new Album(artist1, 10);
        Album secondAlbumArtist1 = new Album(artist1, 20);
        Album firstAlbumArtist2 = new Album(artist2, 10);
        Album firstAlbumArtist3 = new Album(artist3, 10);

        List<Album> albums = Arrays.asList(firstAlbumArtist1, secondAlbumArtist1, firstAlbumArtist2, firstAlbumArtist3);

        Map<Artist, Album> toHits = albums.stream().collect(
                Collectors.toMap(Album::getArtist,  a -> a,  BinaryOperator.maxBy(Comparator.comparing(Album::getSales)) // sales count로 comparing -> max 값을 찾 merge function
                                //  Key Mapper   Value Mapper     Merge Function
        ));

        toHits.forEach((k, v) -> {
            log.info("[KEY] : " + k.name);
            log.info("[VALUE] : artist : " + v.artist + ", sales : " + v.sales);
        });
    }
}

enum Operation {
    PLUS("PLUS"),
    MINUS("MINUS");

    private final String name;

    Operation(String name) {
        this.name = name;
    }
}

@Getter
class Album {
    public Artist artist;
    public int sales;

    public Album(Artist artist, int sales) {
        this.artist = artist;
        this.sales = sales;
    }
}

class Artist {
    String name;
    public Artist(String name) {
        this.name = name;
    }
}

