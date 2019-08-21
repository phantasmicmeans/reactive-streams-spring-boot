package com.reactive.streams.demo;

import com.reactive.streams.demo.publisher.TestPublisher;
import com.reactive.streams.demo.subscriber.TestSubscriber;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DemoApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        Publisher<Integer> publisher = new TestPublisher();
        Subscriber<Integer> subscriber = new TestSubscriber();
        publisher.subscribe(subscriber);
    }
}
