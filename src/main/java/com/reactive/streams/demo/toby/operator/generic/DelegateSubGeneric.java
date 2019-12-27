package com.reactive.streams.demo.toby.operator.generic;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

/**
 * [phantasmicmeans] created on 27/12/2019
 */
public class DelegateSubGeneric<T> implements Subscriber<T> {
    Subscriber sub;
    public DelegateSubGeneric(Subscriber<? super T> sub) {
        this.sub = sub;
    }
    @Override
    public void onSubscribe(Subscription subscription) {
        sub.onSubscribe(subscription);
    }

    @Override
    public void onNext(T t) {

    }

    @Override
    public void onError(Throwable throwable) {

    }

    @Override
    public void onComplete() {

    }
}
