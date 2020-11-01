package com.reactive.streams.demo.observer_1;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

public class ConcreteSubject implements Subject<String> {
    /**
     * multi-thread 안정성 유지를 위해 업데이트시마다 새 복사본을 생성하는 Set 구현체 사용
     * 복사 비용은 큼 -> 구독자 목록 변경은 거의 없음.
     */
    private final Set<Observer<String>> observers = new CopyOnWriteArraySet<>();


    @Override
    public void registerObserver(Observer<String> observer) {
        observers.add(observer);
    }

    @Override
    public void unregisterObserver(Observer<String> observer) {
        observers.remove(observer);
    }

    @Override
    public void notifyObservers(String event) {
        observers.forEach(observers -> observers.observe(event));
    }
}
