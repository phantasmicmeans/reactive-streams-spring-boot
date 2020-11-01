package com.reactive.streams.demo.observer_1;

public interface Observer<T> {
    void observe(T event);
}
