package com.reactive.streams.demo.toby.scheduler;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Slf4j
public class FluxScEx {
    public static void main(String[] args) {
        Flux.range(1, 10)
                .publishOn(Schedulers.newSingle("pub"))
                .log()
                .subscribeOn(Schedulers.newSingle("sub")) // subscribe 시점에 시작
                .subscribe(System.out::println);

        Flux.interval(Duration.ofMillis(200))
                .take(10)
                .subscribe(s -> log.debug("onNext:{}", s));

        try {
            TimeUnit.SECONDS.sleep(10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
