package com.reactive.streams.demo.toby.reactor;

import com.reactive.streams.demo.toby.pubsub.PubSub;
import com.sun.org.apache.xml.internal.resolver.helpers.PublicId;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import reactor.core.publisher.Flux;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.IntStream;

/**
 * [phantasmicmeans] created on 30/12/2019
 */
public class ReactorEx {
    public static void main(String[] args) {
        PubSubTest();
    }

    public static void basicFluxTest() {
        Flux.<Integer> create(e -> {
            IntStream.range(1, 10).forEach(ele -> e.next(ele));
            e.complete();
        }).subscribe(System.out::println);
    }

    public static void FluxTest() {
        // Flux는 Publisher 인터페이스 구현체
        Flux.<Integer> create(e -> {
            IntStream.range(1, 10).forEach(ele -> e.next(ele));
            e.complete();
        })
        .map(e -> e * 10) // map operator
        .reduce(0, (a, b) -> a + b) // reduce operator
        .subscribe(System.out::println);
        // subscriber는 System.out.println 기능만 수행
    }

    public static void PubSubTest() {

        // 1 ~ 10 기본 publisher
        Publisher<Integer> publisher = new Publisher<Integer>() {
            @Override
            public void subscribe(Subscriber<? super Integer> subscriber) {
                subscriber.onSubscribe(new Subscription() {
                    @Override
                    public void request(long l) {
                        IntStream.range(1, 10).forEach(ele -> subscriber.onNext(ele));
                        subscriber.onComplete();
                    }

                    @Override
                    public void cancel() {
                    }
                });
            }
        };

        // 로깅 subscriber
        Subscriber<Integer> subscriber = new Subscriber<Integer>() {
            @Override
            public void onSubscribe(Subscription subscription) {
                subscription.request(10);
            }
            @Override
            public void onNext(Integer i) {
                System.out.println(i);
            }
            @Override
            public void onError(Throwable throwable) { }
            @Override
            public void onComplete() { }
        };

        Publisher<Integer> mapPub = mapPublisher(publisher, e -> e * 10);
        Publisher<Integer> reducePub = reducePublisher(mapPub, 0, (a, b) -> a + b);
        reducePub.subscribe(subscriber);
    }

    // map Operator
    public static Publisher<Integer> mapPublisher (Publisher<Integer> pub, Function<Integer, Integer> f) {
        return new Publisher<Integer>() {
            @Override
            public void subscribe(Subscriber<? super Integer> sub) {
                pub.subscribe(new Subscriber<Integer>() {
                    @Override
                    public void onSubscribe(Subscription subscription) {
                        sub.onSubscribe(subscription);
                    }

                    @Override
                    public void onNext(Integer integer) {
                        int next = f.apply(integer);
                        sub.onNext(next); // map
                    }

                    @Override
                    public void onError(Throwable throwable) { }

                    @Override
                    public void onComplete() {
                        sub.onComplete();
                    }
                });
            }
        };
    }

    // reduce Operator
    public static Publisher<Integer> reducePublisher(Publisher<Integer> pub, int init, BiFunction<Integer, Integer, Integer> f) {
        return new Publisher<Integer>() {
            @Override
            public void subscribe(Subscriber<? super Integer> subscriber) {
                pub.subscribe(new Subscriber<Integer>() {
                    int result = init;
                    @Override
                    public void onSubscribe(Subscription subscription) {
                        subscriber.onSubscribe(subscription);
                    }

                    @Override
                    public void onNext(Integer integer) {
                        result = f.apply(result, integer);
                    }

                    @Override
                    public void onError(Throwable throwable) {
                    }

                    @Override
                    public void onComplete() {
                        subscriber.onNext(result);
                        subscriber.onComplete();
                    }
                });
            }
        };
    }
}
