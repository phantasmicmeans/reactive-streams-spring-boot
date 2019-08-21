package com.reactive.streams.demo.test.subscriber;

import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public class TestSubscription implements Subscription {
    private final ExecutorService executorService;
    private Subscriber<? super Integer> subscriber;
    private final AtomicInteger value;

    public TestSubscription(Subscriber<? super Integer> subscriber,
                     ExecutorService executorService) {
        this.subscriber = subscriber;
        this.executorService = executorService;
        value = new AtomicInteger();
    }

    @Override
    public void request(long l) {
        if (l < 0)
            log.info("subscription - error");
        else
            for (int i = 0; i < l; i++) {
                executorService.execute(() -> {
                    int count = value.incrementAndGet();
                    if (count > 1000) {
                        log.info("item is over");
                        subscriber.onComplete();
                    }
                    else {
                        log.info("push item : " + count);
                        subscriber.onNext(count);
                    }
                });
            }
    }

    @Override
    public void cancel() {
        subscriber.onComplete();
    }
}
