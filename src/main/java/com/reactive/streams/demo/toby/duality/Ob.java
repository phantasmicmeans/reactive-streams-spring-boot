package com.reactive.streams.demo.toby.duality;

public class Ob {
    public static void main(String[] args) {
        /** Duality, Iterable & Observable
         * [Iterable]          < ---- >      [Observable] (Duality)
         * [Pull]              < ---- >      [Push]
         * [값을 끌어온다는 의미]   < ---- >      [값을 가져가라는 의미]
         * [iterator.next()]   < ---- >      [notifyObservers(i)]
         */
        Ob.IterableTest();
        Ob.ObservableTest();
    }

    private static void IterableTest() {
        System.out.println("\n[ITERABLE TEST]");
        IterableOb iter = new IterableOb();
        iter.Iterable();
    }

    private static void ObservableTest() {
        System.out.println("\n[ObservableTest TEST]");
        ObservableOb ob = new ObservableOb();
        ob.Observable();
    }
}
