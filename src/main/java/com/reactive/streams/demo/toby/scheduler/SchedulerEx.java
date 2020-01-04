package com.reactive.streams.demo.toby.scheduler;

import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import org.springframework.scheduling.concurrent.CustomizableThreadFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
public class SchedulerEx {

    public static void main(String[] args) {
        Publisher<Integer> pub = sub -> {
            sub.onSubscribe(new Subscription() {
                @Override
                public void request(long l) {
                    log.debug("request()");
                    sub.onNext(1);
                    sub.onNext(2);
                    sub.onNext(3);
                    sub.onNext(4);
                    sub.onNext(5 );
                    sub.onComplete();

                }

                @Override
                public void cancel() {

                }
            });
        }; // pub

        // operator로 subOnPub
        Publisher<Integer> subOnPub = sub -> {
            ExecutorService ex = Executors.newSingleThreadExecutor(new CustomizableThreadFactory() {
                @Override
                public String getThreadNamePrefix() {
                    return "subOn-";
                }
            });
            ex.execute( () -> pub.subscribe(sub)); // 이 과정을 새로운 스레드로
        };

        // operator로 pubOnPub
        Publisher<Integer> pubOnPub = sub -> {
            // subOnPub와 같이 subscriber 자체가 별도의 스레드에서 동작 하는 것이 아니라,
            // 개별 data가 들어오는 부분 - (onNext, onError, onComplete등)을 별개의 스레드에서 실행한다.
            subOnPub.subscribe(new Subscriber<Integer>() {
                ExecutorService es = Executors.newSingleThreadExecutor(new CustomizableThreadFactory() {
                    @Override
                    public String getThreadNamePrefix() {
                        return "pubOn-";
                    }
                });
                @Override
                public void onSubscribe(Subscription subscription) {
                    sub.onSubscribe(subscription);
                }

                @Override
                public void onNext(Integer integer) {
                    es.execute(() -> sub.onNext(integer));
                }

                @Override
                public void onError(Throwable throwable) {
                    es.execute(() -> sub.onError(throwable));
                }

                @Override
                public void onComplete() {
                    es.execute(() -> sub.onComplete());
                }
            });
        };

        pubOnPub.subscribe(new Subscriber<Integer>() {
            @Override
            public void onSubscribe(Subscription subscription) {
                log.info("onSubscribe");
                subscription.request(Long.MAX_VALUE);
            }

            @Override
            public void onNext(Integer integer) {
                log.debug("onNext: {}", integer);
            }

            @Override
            public void onError(Throwable throwable) {
                log.debug(throwable.getMessage());
            }

            @Override
            public void onComplete() {
                log.debug("onComplete");
            }
        });

        System.out.println(Thread.currentThread().getName());
    }
}
