REACTIVE STREAMS
=================

아래는 축약 설명이고 챕터별 자세한 설명은 [여기](https://github.com/phantasmicmeans/reatcive-streams-spring/tree/master/reactive-stream)서 볼 수 있음

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
쉽게 말해 Operator는 Data를 가공한다. JAVA8의 Stream 관련 메소드와 비슷한 의미를 가진다고 보면 된다. 

자세한 사항은 소스에서 확인하면 된다.
**toby/operator/PubSub2.java에서 확인**

아래에서 보게 될 Flux라는 Publisher에도 Operator를 활용한다.

REACTOR FLUX & MONO
==================
- https://projectreactor.io/docs

Reactor는 JVM 기반을 위한 Non-Blocking 라이브러리이며, Reactive Streams의 구현체.
또한 유틸성 클래스로 Flux, Mono 라는 클래스 제공 하며 이는 위에서 설명한 Publiser 인터페이스를 구현.

이들의 차이는 시퀀스를 얼마나 전송하느냐에 따라 나뉜다.

- Mono : 0 ~ 1개의 데이터 전달
- Flux : 0 ~ N개의 데이터 전달

```java
    public static void FluxTest() {
        // Flux는 Publisher 인터페이스 구현체
        Flux.<Integer> create(e -> {
            IntStream.range(1, 10).forEach(ele -> e.next(ele));
            e.complete();
        })
        .map(e -> e * 10) // map operator
        .reduce(0, (a, b) -> a + b) // reduce operator
        .subscribe(System.out::println);
        // subscriber는 System.out.println 기능만 수행
    }
```
FluxTest() 에서 Operator는 map, reduce 기능을 수행하고 있다. 이를 직접 Publisher, Subscriber로 구현하게 되면 아래와 같다.

```java

    public void PubSubTest() {

        // 1 ~ 10 기본 publisher
        Publisher<Integer> publisher = new Publisher<Integer>() {
            @Override
            public void subscribe(Subscriber<? super Integer> subscriber) {
                subscriber.onSubscribe(new Subscription() {
                    @Override
                    public void request(long l) {
                        IntStream.range(1, 10).forEach(ele -> subscriber.onNext(ele));
                        subscriber.onComplete();
                    }

                    @Override
                    public void cancel() {
                    }
                });
            }
        };

        // 로깅 subscriber
        Subscriber<Integer> subscriber = new Subscriber<Integer>() {
            @Override
            public void onSubscribe(Subscription subscription) {
                subscription.request(10);
            }
            @Override
            public void onNext(Integer i) {
                System.out.println(i);
            }
            @Override
            public void onError(Throwable throwable) { }
            @Override
            public void onComplete() { }
        };

        Publisher<Integer> mapPub = mapPublisher(publisher, e -> e * 10);
        Publisher<Integer> reducePub = reducePublisher(mapPub, 0, (a, b) -> a + b);
        reducePub.subscribe(subscriber);
    }

    // map Operator
    public Publisher<Integer> mapPublisher (Publisher<Integer> pub, Function<Integer, Integer> f) {
        return new Publisher<Integer>() {
            @Override
            public void subscribe(Subscriber<? super Integer> sub) {
                pub.subscribe(new Subscriber<Integer>() {
                    @Override
                    public void onSubscribe(Subscription subscription) {
                        sub.onSubscribe(subscription);
                    }

                    @Override
                    public void onNext(Integer integer) {
                        int next = f.apply(integer);
                        sub.onNext(next); // map
                    }

                    @Override
                    public void onError(Throwable throwable) { }

                    @Override
                    public void onComplete() {
                        sub.onComplete();
                    }
                });
            }
        };
    }

    // reduce Operator
    public Publisher<Integer> reducePublisher(Publisher<Integer> pub, int init, BiFunction<Integer, Integer, Integer> f) {
        return new Publisher<Integer>() {
            @Override
            public void subscribe(Subscriber<? super Integer> subscriber) {
                pub.subscribe(new Subscriber<Integer>() {
                    int result = init;
                    @Override
                    public void onSubscribe(Subscription subscription) {
                        subscriber.onSubscribe(subscription);
                    }

                    @Override
                    public void onNext(Integer integer) {
                        result = f.apply(result, integer);
                    }

                    @Override
                    public void onError(Throwable throwable) {
                    }

                    @Override
                    public void onComplete() {
                        subscriber.onNext(result);
                        subscriber.onComplete();
                    }
                });
            }
        };
    }
```

정리하자면 FluxTest()와 PubSubTest() 는 동일한 job을 실행한다. Flux를 사용하여 코드를 줄일 수 있는 이유는 Publisher 인터페이스를 구현해놓은 클래스이기 때문이다. 

Scheduler
=========
publisher와 subscriber의 동작이 같은 스레드에서 일어날 경우, 병목을 맞이한다. 수행시간이 긴 api를 호출하는 경우가 이에 해당한다.
심각한 경우 웹 서버의 모든 스레드가 점유되는 경우가 있을 수 있다. 

보통의 경우 스레드 풀 혹은 running thread를 생성하여 비동기로 이를 처리하곤 하는데, 이러한 역할을 하는 것이 스케줄러이다.

### subscribeOn
위와 같은 경우 io에 오랜 시간이 소요된다. 이러한 경우 **subscribeOn**이라는 스케줄러를 살펴보자
- > https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Flux.html

subscriberOn의 flow와 description을 확인하면 된다. 이렇게 정의되어 있다. **Typically used for slow publisher e.g., blocking IO, fast consumer(s) scenarios.**
```java 
    flux.subscribeOn(Schedulers.single()).subscribe() 
```
subscribeOn 스케줄러는 subcriber를 별도의 레드에서 실행한다. 

### publishOn
publishOn은 위와 조금 다르다. 
```java
flux.publishOn(Schedulers.sigle()).subscribe()
```
**Typically used for fast publisher, slow consumer(s) scenario.** 라고 명시되어 있다.
publishing 되는 data의 속도는 빠르나 이를 처리하는 subscriber 의 속도가 느린 경우 (ex. db save 등)는 publishOn을 활용하면 된다. 

data를 받아서 처리하는 쪽을 별개의 스레드에서 실행한다. 이 말은 onNext, onError, onComplete 등의 역할이 별도의 스레드에서 실행된다는 말이다.

코드를 보면 subscribeOn, publishOn에 대해 더 잘 이해할 수 있다.

```java
        Publisher<Integer> pub = sub -> {
            sub.onSubscribe(new Subscription() {
                @Override
                public void request(long l) {
                    log.debug("request()");
                    sub.onNext(1);
                    sub.onNext(2);
                    sub.onNext(3);
                    sub.onNext(4);
                    sub.onNext(5 );
                    sub.onComplete();

                }

                @Override
                public void cancel() {

                }
            });
        }; 

        pub.subscribe(new Subscriber<Integer>() {
            @Override
            public void onSubscribe(Subscription subscription) {
                log.info("onSubscribe");
                subscription.request(Long.MAX_VALUE);
            }

            @Override
            public void onNext(Integer integer) {
                log.debug("onNext: {}", integer);
            }

            @Override
            public void onError(Throwable throwable) {
                log.debug(throwable.getMessage());
            }

            @Override
            public void onComplete() {
                log.debug("onComplete");
            }
        });

        System.out.println("EXIT");
    }
```
기본적인 publisher, subscriber 위와 같다. publisher는 5개의 data를 제공하고, subcriber 는 이를 처리하는 간단한 로직이다. 먼저 subscribeOn 부터 보자. 

```java
        Publisher<Integer> subOnPub = sub -> {
            ExecutorService ex = Executors.newSingleThreadExecutor();
            ex.execute( () -> pub.subscribe(sub)); // 이 과정을 새로운 스레드로
        };
```
위는 Operator 개념으로 publisher를 두고 subcribeOn, publishOn의 역할을 맡기는 코드이다. 위 subOnPub 메소드를 보면, 새로운 싱글 스레드를 통해 subscriber가 실행된다.
subscriber는 pub.subscribe(subscriber)가 아닌 위에서 정의한 Operator를 통해 subOnPub.subscribe(subscriber)를 실행하는 형태이다.
결국 subscriber는 subOnPub에 의해 새로운 스레드에서 실행되고, 결론적으로 subscribe를 할 때에 별도의 스레드에서 실행하게 한다. 

publishOn은 data의 제공 속도에 비해 subscriber에서의 data 처리가 느린 경우 활용한다고 하였다. 예를 들어 onNext, onError 등의 개별 data 처리가 느릴 경우 아래처럼 동작시킨다.
```java
        Publisher<Integer> pubOnPub = sub -> {
            // subOnPub와 같이 subscriber 자체가 별도의 스레드에서 동작 하는 것이 아니라,
            // 개별 data가 들어오는 부분 - (onNext, onError, onComplete등)을 별개의 스레드에서 실행한다.
            pub.subscribe(new Subscriber<Integer>() {
                ExecutorService es = Executors.newSingleThreadExecutor();
                @Override
                public void onSubscribe(Subscription subscription) {
                    sub.onSubscribe(subscription);
                }

                @Override
                public void onNext(Integer integer) {
                    es.execute(() -> sub.onNext(integer));
                }

                @Override
                public void onError(Throwable throwable) {
                    es.execute(() -> sub.onError(throwable));
                }

                @Override
                public void onComplete() {
                    es.execute(() -> sub.onComplete());
                }
            });
        };
```
data를 처리하는 onNext, onError, onComplete의 경우, 새로운 스레드에서 실행할 수 있다. 이는 결국 publishing 할 때에 별도의 스레드에서 실행하게 하는것이다. 

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

 
 
 
 
 
 
 
 
