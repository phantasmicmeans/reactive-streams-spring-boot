## Operator
일반적인 flow는 Publisher -> Data -> Subscriber이다.

그러나 Publisher -> [Data1] -> Operator -> [Data2] -> Operator2 -> [Data3] -> Subscriber 처럼 Operator를 활용해 Subcriber에 도달하는 Data를 컨트롤 할 수 있다. 
쉽게 말해 Operator는 Data를 가공한다. JAVA8의 Stream 관련 메소드와 비슷한 의미를 가진다고 보면 된다

아래 코드는 3가지 operator를 거쳐 logSubscriber에게 데이터가 전달되는 과정을 나타낸다. 

기본 개념은 아래와 같다. 
- 대리 Subscriber를 사용하며 Subscription을 대리자에게 넘겨 진행한다.

```java
	private static Publisher<Integer> sumPub(Publisher<Integer> pub) {
		return new Publisher<Integer> () {
			@Override
			public void subscribe(Subscriber<? super Integer> subscriber) {
				pub.subscribe(new Subscriber<Integer>() {
					int sum = 0;

					@Override
					public void onSubscribe(Subscription subscription) {
						subscriber.onSubscribe(subscription);
					}

					@Override
					public void onNext(Integer integer) {
						sum += integer;
					}

					@Override
					public void onError(Throwable throwable) {
						subscriber.onError(throwable);
					}

					@Override
					public void onComplete() {
						subscriber.onNext(sum);
						subscriber.onComplete();
					}
				});
			}
		};
	}
  ```
  
  sumPub을 기준으로 메소드 인자인 `Publisher<Integer> pub`은 map2Pub이고, `Subscriber<? super Integer> subscriber`는 logSubscriber이다.
  pub은 새로운 대리자 Subscriber를 `pub.subscribe` 인자로 받고 subscription을 그대로 넘긴다. 
  
  대리자 Subscriber는 onNext 이벤트를 받아 sum에 더하고, onComplete 이벤트를 받고 난 후 sum을 넘긴다. 또한 onComplete 이벤트도 logSub에게 전달한다. 
  
```java
@Slf4j
public class PubSub {
	public static void main(String[] args) {
		// pub -> [Data1] -> mapPub -> [Data2] -> map2Pub -> [Data3] -> sumPub -> [Data] -> logSub
		Publisher<Integer> pub = iterPub(Stream.iterate(1, a -> a + 1).limit(10).collect(Collectors.toList()));
		Publisher<Integer> mapPub = mapPub(pub, (Function<Integer, Integer>) s -> s * 10);
		Publisher<Integer> map2Pub = map2Pub(mapPub, (Function<Integer, Integer>) s -> s + 1);
		Publisher<Integer> sumPub = sumPub(map2Pub);
		sumPub.subscribe(logSub());

		try {
			Thread.sleep(1000);
		} catch (Exception e) {

		}
	}

	private static Publisher<Integer> sumPub(Publisher<Integer> pub) {
		return new Publisher<Integer> () {
			@Override
			public void subscribe(Subscriber<? super Integer> subscriber) {
				pub.subscribe(new Subscriber<Integer>() {
					int sum = 0;

					@Override
					public void onSubscribe(Subscription subscription) {
						subscriber.onSubscribe(subscription);
					}

					@Override
					public void onNext(Integer integer) {
						sum += integer;
					}

					@Override
					public void onError(Throwable throwable) {
						subscriber.onError(throwable);
					}

					@Override
					public void onComplete() {
						subscriber.onNext(sum);
						subscriber.onComplete();
					}
				});
			}
		};
	}

	private static Publisher<Integer> map2Pub(Publisher<Integer> pub, Function<Integer, Integer> integerIntegerFunction) {
		return new Publisher<Integer>() {
			@Override
			public void subscribe(Subscriber<? super Integer> subscriber) {
				pub.subscribe(new Subscriber<Integer>() {
					@Override
					public void onSubscribe(Subscription subscription) {
						subscriber.onSubscribe(subscription);
					}

					@Override
					public void onNext(Integer integer) {
						subscriber.onNext(integerIntegerFunction.apply(integer));
					}

					@Override
					public void onError(Throwable throwable) {
						subscriber.onError(throwable);
					}

					@Override
					public void onComplete() {
						subscriber.onComplete();
					}
				});
			}
		};
	}

	private static Publisher<Integer> mapPub(Publisher<Integer> pub, Function<Integer, Integer> integerIntegerFunction) {
		return new Publisher<Integer>() {
			@Override
			public void subscribe(Subscriber<? super Integer> subscriber) {
				pub.subscribe(new Subscriber<Integer>() {
					@Override
					public void onSubscribe(Subscription subscription) {
						subscriber.onSubscribe(subscription);
					}

					@Override
					public void onNext(Integer integer) {
						subscriber.onNext(integerIntegerFunction.apply(integer));
					}

					@Override
					public void onError(Throwable throwable) {
						subscriber.onError(throwable);
					}

					@Override
					public void onComplete() {
						subscriber.onComplete();
					}
				});
			}
		};
	}

	private static Subscriber<Integer> logSub() {
		return new Subscriber<Integer>() {
			volatile Subscription subscription;

			@Override
			public void onSubscribe(Subscription subscription) {
				this.subscription = subscription;
				log.info("onSubscribe");
				this.subscription.request(Long.MAX_VALUE);
			}

			@Override
			public void onNext(Integer integer) {
				log.info("onNext: {}", integer);
				//this.subscription.request(1);
			}

			@Override
			public void onError(Throwable throwable) {
				log.error(throwable.getMessage());

			}

			@Override
			public void onComplete() {
				System.out.println("onComplete");
			}
		};
	}

	private static Publisher<Integer> iterPub(Iterable<Integer> iterable) {

		return new Publisher<Integer>() {
			@Override
			public void subscribe(Subscriber<? super Integer> subscriber) {
				subscriber.onSubscribe(new Subscription() {
					@Override
					public void request(long l) {
						try {
							iterable.forEach(subscriber::onNext);
							subscriber.onComplete();
						} catch (Exception e) {
							subscriber.onError(e);
						}
					}

					@Override
					public void cancel() {

					}
				});
			}
		};
	}
}

```
