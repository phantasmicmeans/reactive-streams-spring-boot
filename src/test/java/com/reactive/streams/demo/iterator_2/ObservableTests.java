package com.reactive.streams.demo.iterator_2;

import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@SpringBootTest
public class ObservableTests {

    @Test
    public void ObservableTest() throws InterruptedException {
        ExecutorService ex = Executors.newFixedThreadPool(10);
        RxObservable<String> observable = new ConcreteRxObservable(ex);
        RxObserver<String> observerA = new ConcreteRxObserver();
        RxObserver<String> observerB = new RxObserver<String>() {
            @Override
            public void onNext(String next) {
                System.out.println("RxObserver B");
                System.out.println(Thread.currentThread().getName());
                System.out.println("RxObserver B " + next);
            }

            @Override
            public void onComplete() {
                System.out.println("RxObserver B Complete");
            }

            @Override
            public void onError(Exception e) {

            }
        };

        observable.registerObserver(observerA);
        observable.registerObserver(observerB);
        observable.notifyObservers("Event!");
        observable.notifyObservers("Event!");
        observable.notifyObservers("Event!");
        observable.notifyObservers("Event!");
        observable.notifyObservers("Event!");
        observable.notifyComplete();


        ex.awaitTermination(1000, TimeUnit.MILLISECONDS);
    }
}
