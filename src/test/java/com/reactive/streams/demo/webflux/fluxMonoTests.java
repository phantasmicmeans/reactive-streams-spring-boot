package com.reactive.streams.demo.webflux;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class fluxMonoTests {

    /**
     * Flux와 Mono는 subscribe() 가 실행되기 전까지 어떠한 데이터도 전달하지 않는다.
     * 구구단을 외워서 데이터를 전달하는 상황을 생각해보자.
     */
    @Test
    public void fluxMonoLazy() {
        Flux<Integer> flux = Flux.range(1, 9)
                .flatMap(n -> {
                    try {
                        Thread.sleep(500);
                    } catch (Exception e) {
                        log.info(e.getLocalizedMessage());
                    }
                    return Mono.just(3 * n);
                });

        flux.subscribe(this::logConsumer, this::errorConsumer, this::completeConsumer); // 구독 시작
        log.info("All Completed");
    }

    public void logConsumer(Integer value) {
        log.info(value +"");
    }

    public void errorConsumer(Throwable e) {
        log.info(e.getMessage());
    }

    public void completeConsumer() {
        log.info("completed");
    }

}
