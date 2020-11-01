package com.reactive.streams.demo.pubsub_3;

import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.util.Arrays;
import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class PubSub {
    public static void main(String[] args) {


        /*
        public interface Publisher<T> {
            void subscribe(Subscriber<? super T> var1);
        }*/

        Iterable<Integer> iterable = Arrays.asList(1,2,3,4,5,6,7,8,9,10);

        // 1~5 까지의 어떤 스트림을 연속적으로 push 할 것이다.
        // 그런 publisher
        Publisher<Integer> publishers = new Publisher<Integer>() {
            Iterator<Integer> tor = iterable.iterator();

            @Override
            public void subscribe(Subscriber<? super Integer> subscriber) {
                subscriber.onSubscribe(new Subscription() {
                    int a = 0;
                    @Override
                    public void request(long l) {
                        while (l-- > 0 && tor.hasNext()) {
                            subscriber.onNext(tor.next());
                        }
                    }

                    @Override
                    public void cancel() {

                    }
                });
            }
        };

        Subscriber<Integer> subscribers = new Subscriber<Integer>() {
            private volatile Subscription subscription;

            @Override
            public void onSubscribe(Subscription subscription) {
                System.out.println("onSubscribe");
                this.subscription = subscription;
                this.subscription.request(1);
            }

            @Override
            public void onNext(Integer integer) {
                System.out.println(Thread.currentThread().getName());
                System.out.println("Subscriber Event : " + integer);
                this.subscription.request(1);
            }

            @Override
            public void onError(Throwable throwable) {
                System.out.println("onError");
            }

            @Override
            public void onComplete() {
                System.out.println("onComplete");
            }
        };

        //publisher.subscribe(subscriber);

        Stream<Integer> integerStream = IntStream.range(0, 100).boxed();
        //Flux<Integer> bigPublisher = Flux.fromStream(integerStream);

        ExecutorService ex = Executors.newFixedThreadPool(1);

        Publisher<Integer> publisher = new Publisher<Integer>() {
            Iterator<Integer> iterator = integerStream.iterator();

            @Override
            public void subscribe(Subscriber<? super Integer> subscriber) {
                subscriber.onSubscribe(new Subscription() {
                    @Override
                    public void request(long l) {
                        if (!iterator.hasNext()) {
                            subscriber.onComplete();
                        }

                        while (l-- > 0 && iterator.hasNext()) {
                            System.out.println("[Publisher] : " + Thread.currentThread().getName());
                            subscriber.onNext(iterator.next());
                        }
                    }

                    @Override
                    public void cancel() {

                    }
                });
            }
        };

        Subscriber<Integer> subscriber = new Subscriber<Integer>() {
            private Subscription subscription;
            @Override
            public void onSubscribe(Subscription subscription) {
                this.subscription = subscription;
                this.subscription.request(1);
            }

            @Override
            public void onNext(Integer integer) {
                System.out.println("[Subscriber] : " + Thread.currentThread().getName() + " event : " + integer);
                this.subscription.request(1);
            }

            @Override
            public void onError(Throwable throwable) {
            }

            @Override
            public void onComplete() {
                System.out.println("onComplete");
            }
        };

        publisher.subscribe(subscriber);

    }

    /**
     * Observable - observable.registerObserver(observer)
     * Publihser - publisher.subscribe(subscriber)
     */

}
