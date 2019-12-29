package com.reactive.streams.demo.toby.operator.generic;

import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * [phantasmicmeans] created on 27/12/2019
 */
@Slf4j
public class OperatorGenerics {
    public static void main(String[] args) {
        Publisher<Integer> pub = iterPub(Stream.iterate(1, a -> a + 1).limit(10).collect(Collectors.toList()));
        Publisher<Integer> mapPub = mapPub(pub, s -> s * 10); // operator
        Publisher<String> mapStrPub = mapPub(pub, s -> "[" + s + "]"); // operator
        mapStrPub.subscribe(logsSub());
    }

    // T -> R
    private static <T, R> Publisher<R> mapPub(Publisher<T> pub, Function<T, R> f) {
        return new Publisher<R>() {
            @Override
            public void subscribe(Subscriber<? super R> sub) {
                pub.subscribe(new DelegateSubGeneric<T, R>(sub){
                    @Override
                    public void onNext(T t) {
                        sub.onNext(f.apply(t));
                    }
                });
            }
        };
    }

    private static <T> Subscriber<T> logsSub() {
        return new Subscriber<T>() {
            @Override
            public void onSubscribe(Subscription subscription) {
                log.debug("onSubscribe");
                subscription.request(Long.MAX_VALUE);
            }

            @Override
            public void onNext(T t) {
                log.debug("onNext:{}", t);
            }

            @Override
            public void onError(Throwable throwable) {
                log.debug("onError:{}", throwable);
            }

            @Override
            public void onComplete() {
                log.debug("onComplete");
            }
        };
    }

    private static Publisher<Integer> iterPub(List<Integer> list) {
        return new Publisher<Integer>() {
            Iterable<Integer> iter =  list;
            @Override
            public void subscribe(Subscriber<? super Integer> subscriber) {
                subscriber.onSubscribe(new Subscription() {
                    @Override
                    public void request(long l) {
                        try {
                            iter.forEach(ele -> subscriber.onNext(ele));
                            subscriber.onComplete();
                        } catch (Throwable t) {
                            subscriber.onError(t);
                        }
                    }

                    @Override
                    public void cancel() {
                        log.debug("cancel");
                    }
                });
            }
        };
    }
}
