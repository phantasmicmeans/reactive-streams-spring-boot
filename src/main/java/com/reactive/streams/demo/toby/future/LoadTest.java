package com.reactive.streams.demo.toby.future;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StopWatch;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public class LoadTest {
    static AtomicInteger atomicInteger = new AtomicInteger(0);

    public static void main(String[] args) throws InterruptedException, BrokenBarrierException {
        ExecutorService es = Executors.newFixedThreadPool(100);

        RestTemplate rt = new RestTemplate();
        String url = "http://localhost:8080/rest?idx={idx}";
        StopWatch main = new StopWatch();
        main.start();

        CyclicBarrier barrier = new CyclicBarrier(101);
        for (int i = 0; i < 100; i++) {
            es.submit(() -> {
                int idx = atomicInteger.addAndGet(1);

                barrier.await();
                log.info("Thread {}", idx);

                StopWatch sw = new StopWatch();
                sw.start();

                String res = rt.getForObject(url, String.class, idx);
                sw.stop();

                log.info("Elapsed : {} {} / {}", idx, sw.getTotalTimeSeconds(), res);
                return null;
            });
        }

        barrier.await();
        main.stop();
        log.info("Elapsed : {} ", main.getTotalTimeSeconds());
        es.shutdown();
        es.awaitTermination(100, TimeUnit.SECONDS);
    }
}
