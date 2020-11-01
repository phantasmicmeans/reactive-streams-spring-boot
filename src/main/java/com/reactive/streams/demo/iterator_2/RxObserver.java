package com.reactive.streams.demo.iterator_2;

/**
 * Observer Pattern + Iterator Pattern == Reactive Stream
 * @param <T>
 */
public interface RxObserver<T> {
    void onNext(T next);
    void onComplete();
    void onError(Exception e);
}
