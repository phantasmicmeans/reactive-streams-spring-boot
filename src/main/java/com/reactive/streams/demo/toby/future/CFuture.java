package com.reactive.streams.demo.toby.future;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.*;

@Slf4j
public class CFuture {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        ExecutorService es = Executors.newFixedThreadPool(10);
        CompletableFuture
                .supplyAsync(() -> {
                    log.info("supplyAsync");
                    return 1;
                })
                .thenCompose(s -> {
                    log.info("thenApply {}", s);
                    return CompletableFuture.completedFuture(s + 1);
                })  // 위의 백그라운드 스레드를 그대로 이용해서 실행
                .thenApplyAsync(s2 -> {
                    log.info("thenApply {}", s2);
                    return s2 * 3;
                }, es)
                .exceptionally(e -> -10) // 위 3단계중 error 발생시 -10으로 복구
                .thenAcceptAsync(s2 -> log.info("thenAccept {}", s2), es);

        ForkJoinPool.commonPool().shutdown();
        ForkJoinPool.commonPool().awaitTermination(10, TimeUnit.SECONDS);
    }
}
