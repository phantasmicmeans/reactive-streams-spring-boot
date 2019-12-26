package com.reactive.streams.demo.toby.operator;

import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * [phantasmicmeans] created on 26/12/2019
 * Spring Reactive (2) - Operators
 *
 */
@Slf4j
public class PubSub2 {
    public static void main(String[] args) {
        Publisher<Integer> pub = iterPub(Stream.iterate( 1, a->a+1).limit(10).collect(Collectors.toList()));
        //[Flow]: pub -> [Data1] -> mapPub -> [Data2] -> logSub
        Publisher<Integer> mapPub = mapPub(pub, s -> s * 10); // Operator
        Publisher<Integer> sumPub = subPub(pub); // 합계를 리턴하는 Publisher
        sumPub.subscribe(logSub());
    }

    private static Publisher<Integer> subPub(Publisher<Integer> pub) {
        return new Publisher<Integer>() {
            @Override
            public void subscribe(Subscriber<? super Integer> sub) {
                pub.subscribe(new DelegateSub(sub) {
                    int sum = 0;
                    @Override
                    public void onNext(Integer i) {
                        sum += i;
                    }

                    @Override
                    public void onComplete() {
                        sub.onNext(sum); // 최종 결과
                        sub.onComplete();
                    }
                });
            }
        };
    }

    //[Flow]: pub -> [Data1] -> mapPub -> [Data2] -> logSub
    private static Publisher<Integer> mapPub(Publisher<Integer> pub, Function<Integer, Integer> f) {
        return new Publisher<Integer>() {
            @Override
            public void subscribe(Subscriber<? super Integer> sub) {
                /**
                 * param 인 subscriber 는 logSub 이다.
                 * Data를 mapPub에 전달하고 이를 다시 logSub에 전달해야한다.
                 * 아래의 new Subscriber는 data 변경 과정을 거치고 logSub subscriber 에게 전달하면 됨.
                 * 즉 중계용 subcriber 임.
                 */
                pub.subscribe(new DelegateSub(sub) {
                    @Override
                    public void onNext(Integer i) {
                        sub.onNext(f.apply(i));
                    }
                });
            }
        };
    }

    private static Subscriber<Integer> logSub() {
        return new Subscriber<Integer>() {
            @Override
            public void onSubscribe(Subscription subscription){
                log.debug("onSubscribe");
                subscription.request(Long.MAX_VALUE);
            }

            @Override
            public void onNext(Integer integer) {
                log.debug("OnNext:{} ", integer);
            }

            @Override
            public void onError(Throwable throwable) {
                log.debug("onError:{}", throwable);
            }

            @Override
            public void onComplete() {
                log.debug("onComplete()");
            }
        };
    }

    private static Publisher<Integer> iterPub(List<Integer> list) {
        return new Publisher<Integer>() {
            Iterable<Integer> iter = list;

            @Override
            public void subscribe(Subscriber<? super Integer> subscriber) {
                subscriber.onSubscribe(new Subscription() {
                    @Override
                    public void request(long n) {
                        try {
                            iter.forEach(s -> subscriber.onNext(s));
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
