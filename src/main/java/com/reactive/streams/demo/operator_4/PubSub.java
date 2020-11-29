package com.reactive.streams.demo.operator_4;

import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PubSub {
	public static void main(String[] args) {
		// pub -> [Data1] -> mapPub -> [Data2] -> logSub
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
