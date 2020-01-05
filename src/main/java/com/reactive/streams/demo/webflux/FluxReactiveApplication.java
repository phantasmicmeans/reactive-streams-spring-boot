package com.reactive.streams.demo.webflux;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;

@SpringBootApplication
@RestController
@Slf4j
public class FluxReactiveApplication {

    @GetMapping("/")
    Mono<String> hello(){
        log.info("pos1");

        String msg = generateHello();
        Mono<String> m =  Mono.just(msg).doOnNext(c -> log.info(c)).log();
        //Mono m = Mono.fromSupplier(() -> generateHello()).doOnNext(c -> log.info(c)).log();
        //log.info("subsribe");
        //m2.subscribe();
        return m;
    }

    @GetMapping("/event/{id}")
    Mono<Event> hello(@PathVariable long id) {
        return Mono.just(new Event(id, "event" + id));

    }

    @GetMapping("/event/mono/{id}")
    Mono<List<Event>> event(@PathVariable long id) {
        List<Event> list = Arrays.asList(new Event(1L, "a"), new Event(2L, "b"));
        return Mono.just(list);

    }

    @GetMapping(value = "/events", produces = MediaType.TEXT_EVENT_STREAM_VALUE) // chunk 단위로 스트림 전송
    Flux<Event> events() {
        List<Event> list = Arrays.asList(new Event(1L, "event1"), new Event(2L, "event2"));
        return Flux.fromIterable(list); // fromIterable 사용해야함.
    }

    @GetMapping(value = "/events/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE) // chunk 단위로 스트림 전송
    Flux<Event> streamEvents() {
        // event data generate, 1초마다 1개의 Event 생성
        Flux<Event> f =  Flux
                //.fromStream(Stream.generate(() -> new Event(System.currentTimeMillis(), "value")))
                //.<Event> generate(sink -> sink.next(new Event(System.currentTimeMillis(),"value"))) // sink는 data를 흘려 보내는 역할
                .<Event, Long>generate(() -> 1L, (id, sink) -> { // generate(Callable)
                    sink.next(new Event(id, "value" + id)); // sink next
                    return id + 1; // id generation
                })
                .delayElements(Duration.ofSeconds(1))
                .take(10);


        /** Flux merge
         * flux es 는 id를 1씩 generate, flux interval은 1초마다 실행
         * 두개의 flux를 가지고 쌍을 만드는 과정임
         * */
        Flux<Event> es =  Flux
                .<Event, Long>generate(() -> 1L, (id, sink) -> { // generate(Callable)
                    sink.next(new Event(id, "value" + id)); // sink next, sink는 data를 흘려 보내는 역할
                    return id + 1; // id generation
                });

        Flux<Long> interval = Flux.interval(Duration.ofSeconds(1)); // delay element 역할

        return Flux.zip(es, interval).map(tuple -> tuple.getT1()).take(10);

    }

    @GetMapping(value = "events/zipping", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    Flux<Event> eventsStream() {
        Flux<String> es = Flux.generate(sink -> sink.next("VALUE"));
        Flux<Long> interval = Flux.interval(Duration.ofSeconds(1));

        return Flux.zip(es, interval).map(tuple -> new Event(tuple.getT2(), tuple.getT1()));

    }

    private String generateHello() {
        log.info("method generateHello()");
        return "Hello Mono";

    }

    @Data @AllArgsConstructor
    public static class Event {
        long id;
        String value;
    }

    public static void main(String[] args) {
        SpringApplication.run(FluxReactiveApplication.class, args);
    }

}
