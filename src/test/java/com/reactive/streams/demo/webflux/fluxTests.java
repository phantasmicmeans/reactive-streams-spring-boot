package com.reactive.streams.demo.webflux;

import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Flux Basic Test
 */
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class fluxTests {

    /**
     * just method로 String 데이터를 전달하는 Flux를 생성.
     * Flux.just() 메소드로 A, B 라는 String을 포함하는 Publisher의 구현체인 Flux를 생성한다.
     * Flux는 subscribe가 실행되기 전 까지는 어떠한 일도 발생시키지 않는다.
     * Publishe는 구독이 되었을 경우에만 발행한다는 얘기 ..
     * Subscriber 가 Publisher에 구독을 하는 과정은 Publisher에 정의된 subscribe() 를 사용한다.
     * 이때 매개변수 Consumer를 전달할 수 있다 !!!!
     *
     * 이벤트는 onSubscribe() -> request() -> onNext() -> onNext() -> onComplete()로
     */
    @Test
    public void fluxJustConsumer() {
        Flux<String> flux = Flux.just("A", "B").log(); // log 는 로그 메시지 출력용...
        flux.subscribe(this::justConsumer);
    }

    @Test
    public void fluxJustConsumer2() {
        List<String> names = new ArrayList<>();
        Flux<String> flux = Flux.just("A","B","C").log();
        flux.subscribe(names::add);

        Assert.assertEquals(names.size(), 3);
        Assert.assertEquals(names, Arrays.asList("A","B","C"));
    }

    private void justConsumer(String sequence) {
        log.info(sequence);
    }

    /**
     * Flux Range
     */
    @Test
    public void fluxRange() {
        List<Integer> list = new ArrayList<>();
        Flux<Integer> flux = Flux.range(1, 5).log();
        flux.subscribe(list::add);

        Assert.assertEquals(list.size(), 5);
    }

    /**
     * fromArray(), fromIterable, fromStream 를 활용하면 Array, Iterable, Streams를 사용해 Flux 생성 가능.
     * 이미 생성되어 있는 Array, Iterable, Stream 의 데이터를 사용하여 Flux 생성.
     */
    @Test
    public void fluxFromArray() {
        List<String> names = new ArrayList<>();
        String[] nameStr = new String[]{"A","B","C","D","E"};
        Flux<String> flux = Flux.fromArray(nameStr);
        flux.subscribe(names::add);

        Assert.assertEquals(names, Arrays.asList("A","B","C","D","E"));
    }

    @Test
    public void fluxEmpty() {
        List<String> names = new ArrayList<>();
        Flux<String> flux = Flux.empty();
        flux.subscribe(names::add);

        Assert.assertEquals(names.size(), 0);
    }
}
