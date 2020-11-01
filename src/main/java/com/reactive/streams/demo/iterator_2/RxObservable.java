package com.reactive.streams.demo.iterator_2;

public interface RxObservable<T> {
    void registerObserver(RxObserver<T> observer);
    void unregisterObserver(RxObserver<T> observer);
    void notifyObservers(T event);
    void notifyComplete();
}
