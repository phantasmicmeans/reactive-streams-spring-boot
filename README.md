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
Flux는 추상클래스로 정의되어 있다. 

```java
public abstract class Flux<T> implements Publisher<T> 
```

Flux는 Reactive Streams에서 정의한 Publisher의 구현체로서 0-N개의 데이터를 발행할 수 있다. 
하나의 데이터를 전달할 때 마다 onNext() 이벤트를 발생한다. Flux 내의 모든 데이터 처리가 완료되면, onComplete() 이벤트가 발생하며, 전달 과정에 문제가 생기면 onError() 가 발생한다. 

MONO 
====
Mono 또한 Publisher 인터페이스를 구현하는 구현체인데, Flux와의 차이점은 데이터 처리 개수이다.
Flux는 0 ~ N개의 데이터를 처리하지만, Mono는 0 ~ 1개의 데이터를 처리한다. onNext(), onComplete(), onError()는 동일하다. 

Flux 및 Mono 생성
================
먼저 Flux를 생성하는 가장 간단한 방법은 제공되는 팩토리 메소드를 활용하는 것이다.
- just()
- range()
- fromArray(), fromIterable(), fromStream()
- empty()

```java 
Flux<String> flux = Flux.just("A", "B"...);
Flux<String> flux = Flux.range(0, 5);
Flux<String> flux = Flux.fromArray(new String[] {"A", "B"...});
Flux<String> flux = Flux.fromIterable(...);
Flux<String> flux = Flux.fromStream(..);
Flux<String> flux = Flux.empty();
```

상세는 테스트 코드 확인 하면 됨.
