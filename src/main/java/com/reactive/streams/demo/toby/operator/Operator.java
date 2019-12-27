package com.reactive.streams.demo.toby.operator;

import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * [phantasmicmeans] created on 26/12/2019
 */
@Slf4j
public class Operator {

    public static void main(String[] args) {
        List<Integer> iter = Stream.iterate(1, ele -> ele + 1).limit(10).collect(Collectors.toList());
        Function<Integer, Integer> fPlus = ele -> ele + 1;
        Function<Integer, Integer> fMulti = ele -> ele * 10;

        Publisher<Integer> pub = iteratePubWithLambda(iter); // original publisher
        Publisher<Integer> mapPlusPub  = mapPub(pub, fPlus); // Operator, Flow => [ Publisher -> Data -> Operator -> Data2 -> Subscriber ]
        Publisher<Integer> mapMultiPub = mapPub(pub, fMulti); // Operator
        Publisher<Integer> mapPlusPubLambda = mapPubWithLambda(pub, fPlus);
        Publisher<Integer> mapMultiPubLambda = mapPubWithLambda(pub, fMulti);

        Publisher<Integer> reducePub = reducePub(pub, 0, (a, b) -> a + b);
        // 1,2,3,4,5
        // 0->(0,1) -> 0 + 1 = 1
        // 1->(1,2) -> 1 + 2 = 3
        // 2->(2,3) -> 3 + 3 = 6 .
        // ..
        Subscriber<Integer> subscriber = logsSub();
        reducePub.subscribe(subscriber);
    }

    private static Subscriber<Integer> logsSub() {
        return new Subscriber<Integer>() {
            @Override
            public void onSubscribe(Subscription subscription) {
                log.debug("onSubscribe");
                subscription.request(Long.MAX_VALUE);
            }

            @Override
            public void onNext(Integer integer) {
                log.debug("onNext:{}", integer);

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

    private static Publisher<Integer> mapPubWithLambda(Publisher<Integer> pub, Function<Integer, Integer> f) {
        return subscriber ->
            pub.subscribe(new Subscriber<Integer>() {
                @Override
                public void onSubscribe(Subscription subscription) {
                    subscriber.onSubscribe(subscription);
                }

                @Override
                public void onNext(Integer integer) {
                    subscriber.onNext(f.apply(integer));
                }

                @Override
                public void onError(Throwable throwable) {
                    subscriber.onError(throwable);
                }

                @Override
                public void onComplete() {
                    subscriber.onComplete();
                }
            });
    }

    private static Publisher<Integer> mapPub(Publisher<Integer> pub, Function<Integer, Integer> f) {
        return new Publisher<Integer>() {
            @Override
            public void subscribe(Subscriber<? super Integer> subscriber) {
                pub.subscribe(new Subscriber<Integer>() {
                    @Override
                    public void onSubscribe(Subscription subscription) {
                        subscriber.onSubscribe(subscription); // subscription 전달
                    }

                    @Override
                    public void onNext(Integer integer) {
                        int next = f.apply(integer);
                        subscriber.onNext(next);
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        subscriber.onError(throwable);
                    }

                    @Override
                    public void onComplete() {
                        subscriber.onComplete();
                    }
                });
            }
        };
    }

    private static Publisher<Integer> reducePub(Publisher<Integer> pub, int init, BiFunction<Integer, Integer, Integer> bf) {
        return new Publisher<Integer>() {
            @Override
            public void subscribe(Subscriber<? super Integer> sub) {
                pub.subscribe(new DelegateSub(sub) {
                    int result = init;
                    @Override
                    public void onNext(Integer i) {
                        result = bf.apply(result, i);
                    }

                    @Override
                    public void onComplete() {
                        sub.onNext(result);
                        sub.onComplete();
                    }
                });
            }
        };
    }

    private static Publisher<Integer> iteratePubWithLambda(List<Integer> list) {
        return subscriber ->
            subscriber.onSubscribe(new Subscription() {
                Iterable<Integer> iter = list;
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
                    log.debug("[CANCEL]");
                }
            });
    }

    private static Publisher<Integer> iteratePub(List<Integer> list) {
        return new Publisher<Integer>() {
            Iterable<Integer> iter = list;
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

                    }
                });
            }
        };
    }
}
