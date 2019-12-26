package com.reactive.streams.demo.toby.operator;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

/**
 * [phantasmicmeans] created on 26/12/2019
 */
public class DelegateSub implements Subscriber<Integer> {
    Subscriber sub;
    public DelegateSub(Subscriber sub) {
        this.sub = sub;
    }

    @Override
    public void onSubscribe(Subscription s) {
        //subscription 은 전달해야함.
        sub.onSubscribe(s);
    }

    @Override
    public void onNext(Integer i) { // 중계
        sub.onNext(i);
    }

    @Override
    public void onError(Throwable throwable) {
        sub.onError(throwable);
    }

    @Override
    public void onComplete() {
        sub.onComplete();
    }
}
