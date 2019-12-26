package com.reactive.streams.demo.toby.pubsub;


import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.util.Arrays;
import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * [phantasmicmeans] created on 21/12/2019
 */
public class PubSub {
    /**
     * Publisher  <-->  Observable
     * Subscriber <-->  Observer
     */

    public static void main(String[] args) {
        PubSub ps = new PubSub();
        ps.pubSubTest();
    }
    public void pubSubTest() {
        Iterable<Integer> itr = Arrays.asList(1, 2, 3, 4, 5);
        ExecutorService es = Executors.newCachedThreadPool();

        Publisher<Integer> p = new Publisher<Integer>() {
            @Override
            public void subscribe(Subscriber<? super Integer> subscriber) {
                Iterator<Integer> it = itr.iterator();
                System.out.println("[SUBSCRIBE THREAD] : " + Thread.currentThread().getName());
                subscriber.onSubscribe(new Subscription() {
                    @Override
                    public void request(long l) { // 개수
                        es.execute(() -> {
                            int i = 0;
                            try {
                                while (i++ < l) {
                                    if (it.hasNext()) {
                                        Integer next = it.next();
                                        subscriber.onNext(next);
                                    } else {
                                        subscriber.onComplete(); // 종료
                                        break;
                                    }
                                }
                            } catch (RuntimeException e) { // Error 처리는 이렇게
                                subscriber.onError(e);
                            }
                        });
                    }

                    @Override
                    public void cancel() {

                    }
                });
            }
        };

        /**
         * 정해진 Protocol 의 메소드
         */
        Subscriber<Integer> s = new Subscriber<Integer>() {
            Subscription subscription = null;
            int bufferSize = 2;

            @Override
            public void onSubscribe(Subscription subscription) { // 반드시 호출해야함. subscribe를 하는 즉시
                System.out.println(Thread.currentThread().getName() + " onSubscribe");
                this.subscription = subscription;
                this.subscription.request(1);
            }

            @Override
            public void onNext(Integer integer) {
                System.out.println(Thread.currentThread().getName() + " OnNext " + integer);
                this.subscription.request(1);
            }

            @Override
            public void onError(Throwable throwable) {
                System.out.println("onError " + throwable.getMessage());
            }

            @Override
            public void onComplete() {
                System.out.println("onComplete ");
            }
        };

        p.subscribe(s);

        try {
            es.awaitTermination(10, TimeUnit.HOURS);
        } catch (InterruptedException e) {
            System.out.println("[AWAIT TERMINATION ERROR] : " + e.getMessage());
        } finally {
            es.shutdown();
        }
    }
}
