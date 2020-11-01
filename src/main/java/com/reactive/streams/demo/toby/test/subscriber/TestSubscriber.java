package com.reactive.streams.demo.toby.test.subscriber;

import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

/**
 * Subscriber 시퀀스를 받았을 때에 대한 로직을 구현. 어떻게 데이터를 전송하는지에 대해서는 Subscription
 */
@Slf4j
public class TestSubscriber implements Subscriber<Integer> {
    private Integer count;
    private final Integer DEMO_COUNT = 3;
    private Subscription subscription;
    public TestSubscriber() {}

    @Override
    public void onSubscribe(Subscription subscription) {
        log.info("subscriber - onSubscribe");
        count = DEMO_COUNT;
        this.subscription = subscription;
        this.subscription.request(DEMO_COUNT); // request(N) ==> N 개의 request, Publisher 에 N 개의 데이터 전달.
    }

    /**
     * 위의 onSubscribe 에서 request(N)으로 요청 개수를 정한다. 이후 Publisher 에게 N개의 데이터를 전달하는데,
     * 이 전달을 위해 사용하는 메소드이다.
     * @param integer
     */
    @Override
    public void onNext(Integer integer) {
        log.info("subscriber - onNext");
        synchronized (this) {
            count --;
            if (count == 0) {
                log.info("count is zero");
                count = DEMO_COUNT;
                subscription.request(count);
            }
        }
    }

    @Override
    public void onError(Throwable throwable) {
        log.info("subscriber - onError");
    }

    @Override
    public void onComplete() {
        log.info("subscriber - onComplete");
    }
}
