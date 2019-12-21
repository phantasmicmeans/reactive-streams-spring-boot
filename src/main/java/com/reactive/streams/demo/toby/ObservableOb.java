package com.reactive.streams.demo.toby;

import java.util.Observable;
import java.util.Observer;

public class ObservableOb {
    public void Observable() {
        // Event Source가 event or data를 던진다. Observer에게
        // Observable은 새로운 정보 발생시마다 Observer(multi Observer가능) 에게 notify함.
        // Source -> Event / Data -> Observer에게 (타겟)

        Observer ob = new Observer() {
            @Override
            public void update(Observable o, Object arg) {
                System.out.println(arg);
            }
        };

        IntObservable io = new IntObservable(); // Observable 생성
        io.addObserver(ob); // Observer 등록

        io.run(); // 등록된 Observer 들에게 알림
    }

    /**
     * Observer에게 알릴 이벤트
     */
    static class IntObservable extends Observable implements Runnable {
        @Override
        public void run() {
            for (int i = 1; i < 10; i++) {
                setChanged();
                notifyObservers(i);
            }
        }
    }
}
