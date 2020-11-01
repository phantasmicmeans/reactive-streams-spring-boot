package com.reactive.streams.demo.iterator_2;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.ExecutorService;

public class ConcreteRxObservable implements RxObservable<String> {

    private final ExecutorService ex;
    private final Set<RxObserver<String>> observers = new CopyOnWriteArraySet<>();

    public ConcreteRxObservable(ExecutorService ex) {
        this.ex = ex;
    }

    @Override
    public void registerObserver(RxObserver<String> observer) {
        observers.add(observer);
    }

    @Override
    public void unregisterObserver(RxObserver<String> observer) {
        observers.remove(observer);
    }

    @Override
    public void notifyObservers(String event) {
        observers.forEach(observer -> {
            ex.submit(() -> observer.onNext(event));
        });
    }

    @Override
    public void notifyComplete() {
        observers.forEach(observers -> {
            ex.submit(observers::onComplete);
        });
    }
}
