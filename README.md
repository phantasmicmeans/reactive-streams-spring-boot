REACTIVE STREAMS
=================

### Publisher
Publisher 인터페이스는 단 하나의 메소드를 정의한다. 
- subscribe(Subscriber <? super Integer>) void

Publisher 를 구현하는 클래스는 subscribe 메소드를 구현해야 한다. 이 메소드는 Subscriber 객체를 매개변수로 받아 Subscriber 의 onSubscribe() 메소드를 실행시킨다.

### Subscriber 
Subscribe 는 다음과 같은 메소드를 정의한다.
- void onSubscribe(Subscription var1);
- void onNext(T var1);
- void onError(Throwable var1);
- void onComplete();