package com.reactive.streams.demo.observer_1;

import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@SpringBootTest
public class ObserverTests {

    @Test
    public void observerHandleEventsFromSubject() {
        // given
        Subject<String> subject = new ConcreteSubject();
        Observer<String> observerA = new ConcreteObserverA();
        Observer<String> observerB = new ConcreteObserverB();

        // when
        subject.notifyObservers("no subscriber!");
        subject.registerObserver(observerA);
        subject.registerObserver(observerB);

        subject.notifyObservers("event !");

        subject.unregisterObserver(observerB);

        subject.notifyObservers("event for A");
        subject.unregisterObserver(observerA);

        subject.notifyObservers("no subscriber");
    }

    @Test
    public void subjectLeverageLambdas() {
        Subject<String> subject = new ConcreteSubject();
        subject.registerObserver(new Observer<String>() {
            @Override
            public void observe(String event) {
                System.out.println("A : " + event);
            }
        });
        subject.registerObserver(event -> System.out.println("B : " + event));
        subject.notifyObservers("event start for A & B");
    }

    /**
     * 대기시간이 상당히 긴 이벤트를 처리하는 관찰자가 많을 경우 추가적인 스레드 할당 또는 스레드풀을
     * 사용해 병렬로 전달해야함
     */
    @Test
    public void subjectThreadTest() {
        Subject<String> subject = new ConcreteSubject();
        subject.registerObserver(event -> {
            try {
                Thread.sleep(3000);
            } catch (Exception e) {

            }

            System.out.println(Thread.currentThread().getName());
            System.out.println("A: " + event);
        });

        subject.registerObserver(event -> {
            System.out.println(Thread.currentThread().getName());
            System.out.println("B: " + event);
        });

        subject.notifyObservers("event!");

        try {
            Thread.sleep(3100);
        } catch (Exception e) {

        }
    }

    @Test
    public void subjectParallelTest() throws InterruptedException {
        final ExecutorService ex = Executors.newFixedThreadPool(10);

        Subject<String> parallelSubject = new ConcreteParallelSubject(ex);
        parallelSubject.registerObserver(event -> {
            try {
                Thread.sleep(3000);
            } catch (Exception e) {

            }

            System.out.println(Thread.currentThread().getName());
            System.out.println("A : " + event);
        });

        parallelSubject.registerObserver(event -> {
            System.out.println(Thread.currentThread().getName());
            System.out.println("B : " + event);
        });

        parallelSubject.notifyObservers("event!");

        ex.awaitTermination(3100, TimeUnit.MILLISECONDS);
    }

    public ConcreteSubject createSubject() {
        return new ConcreteSubject();
    }

    String getName(Thread t) {
        return t.getName();
    }
}
