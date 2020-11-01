package com.reactive.streams.demo.iterator_2;

public class ConcreteRxObserver implements RxObserver<String> {
    @Override
    public void onNext(String next) {
        System.out.println("RxObserver A");
        System.out.println(Thread.currentThread().getName());
        System.out.println("RxObserver A " + next);
    }

    @Override
    public void onComplete() {
        System.out.println("RxObserver A Complete");
    }

    @Override
    public void onError(Exception e) {
        System.out.println(Thread.currentThread().getName());
        System.out.println(e.getMessage());
    }
}
