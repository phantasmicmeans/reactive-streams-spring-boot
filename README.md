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

REACTOR FUX & MONO
==================
Reactor는 JVM 기반을 위한 Non-Blocking 라이브러리이며, Reactive Streams의 구현체. 

Flux와 Mono는 시퀀스를 제공하는 역할을 하며 Publisher 구현체이며, 이들의 차이는 시퀀스를 얼마나 전송하느냐에 따라 나뉜다.

- Mono : 0 ~ 1개의 데이터 전달
- Flux : 0 ~ N개의 데이터 전달

FLUX
====
Publisher의 구현체중 하나로서, 0 ~ N개의 데이터를 전송한다. 기존 Publisher와 동일하게 각 전달마다 onNext()를 발생시킨다. 
