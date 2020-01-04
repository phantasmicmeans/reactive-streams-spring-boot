package com.reactive.streams.demo.webflux;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.concurrent.CompletableFuture;

@EnableAsync
@SpringBootApplication
@Slf4j
@RestController
public class WebFluxApplication {
    static final String URL1 = "http://localhost:8081/service?req={req}";
    static final String URL2 = "http://localhost:8081/service2?req={req}";

    @Autowired
    MyService myService;
    static int index = 0;
    WebClient client = WebClient.create();

    @GetMapping("/rest")
    public Mono<String> rest(int idx) {
        // mono 또한 publisher 구현한 것. subscriber가 subscribe 하기 전 까지는 실행 안함.
        Mono<String> result =  client.get().uri(URL1, idx).exchange()
                .flatMap(c -> c.bodyToMono(String.class))
                .flatMap(res -> client.get().uri(URL2, res).exchange())
                .flatMap(c -> c.bodyToMono(String.class))
                .doOnNext(c -> log.info(c.toString()))
                .flatMap(res2 -> Mono.fromCompletionStage(myService.work(res2)));
                        // Mono.fromCompletionStage ==> CompletableFuture<String> -> Mono<String>
        return result;
    }

    @GetMapping("/rest2")
    public Mono<String> rest2(int idx) {
        log.info("NAME : " + Thread.currentThread().getName());
        log.info("[REQUEST 2]");
        return client.get().uri(URL1, idx).exchange()
                .flatMap(c -> {
                    log.info(Thread.currentThread().getName());
                    return c.bodyToMono(String.class);
                })
                .flatMap(res -> {
                    log.info(Thread.currentThread().getName());
                    return client.get().uri(URL2, res).exchange();
                })
                .flatMap(c -> c.bodyToMono(String.class));
    }

    public static void main(String[] args) {
        log.debug("MAIN");
        System.setProperty("reactor.ipc.netty.workerCount", "1");
        System.setProperty("reactor.ipc.netty.pool.maxConnections", "2000");
        SpringApplication.run(WebFluxApplication.class, args);
    }

    @Service
    public static class MyService {
        // 비동기로
        public CompletableFuture<String> work(String req) {
            return CompletableFuture.completedFuture(req + "/asyncwork");
        }
    }
}

