package com.reactive.streams.demo.test.publisher;

import com.reactive.streams.demo.test.subscriber.TestSubscription;
import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
public class TestPublisher implements Publisher<Integer> {
    private final ExecutorService executorService = Executors.newFixedThreadPool(3);

    /**
     * publisher 를 구현하는 EddyPublisher 클래스는 subscribe 메소드를 구현해야함.
     * subscribe 메소드는 Subscriber 객체를 매개변수로 받고, subscriber 의 onSubscribe() 를 실행함.
     * @param subscriber
     */
    @Override
    public void subscribe(Subscriber<? super Integer> subscriber) {
        log.info("publisher - subscriber");
        subscriber.onSubscribe(new TestSubscription(subscriber, executorService));
    }
}
