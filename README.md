REACTIVE STREAMS
=================

## Duality

Iterable | Observable
-------- | ----------
[Iterable] | [Observable]
[Pull] | [Push]
[값을 끌어온다는 의미] | [값을 가져가라는 의미]
[iterator.next()] | [notifyObservers(arg)]

## Observer Pattern
Observer Pattern에 2가지 문제가 있다.. 
 1. Data의 '끝'이라는 개념이 없다. 
 2. Error 처리.. Exception은 어떻게? 

물론 많은 부분들이 있겠지만 위 2가지 요소를 더욱 보강한다는 전제가 Reactive Programming의 여러 기준 중 하나이다.

## Reactive Streams - 표준 

**표준 Spec Document**
- http://www.reactive-streams.org/

**그 외 Document**
- http://reactivex.io/
- https://github.com/reactive-streams/reactive-streams-jvm

### Publisher
https://github.com/reactive-streams/reactive-streams-jvm에 따르면 

A Publisher is a provider of a potentially unbounded number of sequenced elements, publishing them according to the demand received from its Subscriber(s).
In response to a call to Publisher.subscribe(Subscriber). 라고 명시되어있다.

즉 Publisher는 시퀀셜한 element들을 제공하는 Provider이고, Subscriber의 demand에 따라 publish 한다. 이는 Observable과 동등한 의미로 보면 된다.
Observable의 Observable.addObserver(observer)와 같은 의미로 Publisher는 Publisher.subscribe(Subcriber)를 활용해 Receiver를 정한다. 

 기준 | Observable | Publisher
---------- | --------- | ---------
Provider | O  | O
Receiver | Observer | Subscriber
Add Receiver | Observable.addObserver(ob) | Publisher.subscribe(sb) 

### Subscriber    
Subscribe 는 다음과 같은 메소드를 정의한다.
- void onSubscribe(Subscription var1);
- void onNext(T var1);
- void onError(Throwable var1);
- void onComplete();

또한 Publisher가 Subscriber에게 전달하는 정보는 아래와 같은 protocol을 따른다.

```java 
onSubscribe onNext* (onError | onComplete)?
```
Subscriber는 onSubscribe(arg)를 통해 subscribe를 시작하고, onNext()를 통해 element를 수신한다.
여기서 onNext* 는 0 ~ N(무한대)까지 호출 가능하다는 의미이다.
마지막으로 (onError | onComplete)? 는 optional이다. 두가지 중 하나를 호출할 수 있고, 이 과정을 거치면 마치는 protocol이다.

### Operator 
일반적인 flow는 다음과 같다. **Publisher -> Data -> Subscriber**
그러나 Publisher -> [Data1] -> Operator -> [Data2] -> Operator2 -> [Data3] -> Subscriber 처럼 Operator를 활용해 Subcriber에 도달하는 Data를 컨트롤 할 수 있다.
쉽게 말해 Operator는 Data를 가공한다. JAVA8의 Stream 관련 메소드와 비슷한 의미를 가진다고 보면 다. 

1. map ([Flow]: pub -> [Data1] -> mapPub -> [Data2] -> sub), **toby/operator/PubSub2.java에서 확인**

REACTOR FUX & MONO
==================
Reactor는 JVM 기반을 위한 Non-Blocking 라이브러리이며, Reactive Streams의 구현체. 

Flux와 Mono는 시퀀스를 제공하는 역할을 하며 Publisher 구현체이며, 이들의 차이는 시퀀스를 얼마나 전송하느냐에 따라 나뉜다.

- Mono : 0 ~ 1개의 데이터 전달
- Flux : 0 ~ N개의 데이터 전달

FLUX````
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

Spring WebFlux 
==========================

Spring WebFlux는 스프링 5부터 지원하는 reactive-stack Web Framework이다.
Fully Non-blocking이고, Netty, Servlet3.1+ 등에서 실행될 수 있다. 

Reactive Programming이란?
=========================
데이터를 처리함에 있어 Asynchronous, Non-Blocking, Event-Driven하게 처리하는 프로그래밍 패러다임을 말한다. 
이를 잘 이해하기 위해서는 Blocking과 Non-Blocking의 차이에 대해 제대로 알아야한다.

#### Blocking vs Non-Blocking
Client의 Request가 들어오고, 이를 Worker Thread가 받아 해당 Operation들을 실행하고 리턴값을 반환한다.
이때 DB I/O 작업 혹은 다른 서버로의 I/O 작업이 포함될 수 있다. 
이러한 작업들이 실행되는 도중에는 thread는 block되고, 다른 request는 wait 상태를 지닌다. 
이를 "동기 혹은 Synchronous" 하다고 말한다. 

Multi-Thread를 지원하는 톰캣과 같은 WAS는, worker thread pool을 만들고 wait 상태인 request를 줄여 성능을 높인다.
다른 서버 혹은 DB로의 I/O 또한 해당 thread pool을 생성해놓고 async하게 처리할 수는 있다. 
그러나 위의 설명처럼 "다른 서버 혹은 DB로의 I/O 작업 시, request를 수용했던 thread는 작업이 끝날 때 까지 wait 상태를 유지한다.  

**추가로 thread pool의 thread가 모두 활성화 되어 있거나 혹은 cpu core가 전부 점유 상태라면 결국 wait 상태를 유지한다. **

#### Non-Blocking 
Non-Blocking or Asynchronous Request Processing을 보자.
어떠한 작업 Thread가 wait 상태를 지니지 않는다. 
 
Multi-Thread 상황에서 특정 공유자원에 접근할 때 사용하는 뮤텍스, 세마포어 혹은 synchronized 등을 사용한다. 이를 통해 동시접근을 막는다.
Non-Blocking I/O는 해당 thread가 다른 작업을 진행하더라도 기다리지 않고 다음 작업을 진행한다. 
작업이 끝나야만 다음 작업을 실행할 수 있는 상황에서는 굳이 Non-blocking I/O는 필요없을 것 같다.. 어차피 기다려야하니
 
뭐 어쨌든 Async와 Non-Blocking을 같은 맥락으로 이해할 수도 있으나 명확하게 다르다. (알아서 찾아보셈) 

#### Reactive Programming 
Publisher - Subscriber Pattern 즉 Observer Pattern 이라고도 불리우는 Reactive Programming은 데이터의 흐름을 먼저 정하고 데이터가 변경되었을 때 그에 따른 행동을 하는것이다.
즉 데이터의 변화와 그에대한 전파에 중점을 둔 프로그래밍이며, 데이터 흐름에 따라 의존된 실행 모델이 자동으로 변화를 전파할 수 있는것을 의미한다. 
쉽게말하면 어떤 컴포넌트가 있고, 이 컴포넌트의 변화를 수신하는 다른 컴포넌트가 있을 때, 첫번째 컴포넌트가 변화시 다른 컴포넌트에 변화를 자동으로 전달하게 되는것이다.

 위의 Mono와 Flux에 대해 읽었다면, 충분히 이해 갈 것이다. 
 
 
 
 
 
 
 
 