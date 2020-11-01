package com.reactive.streams.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DemoApplication /*implements CommandLineRunner*/ {

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }

    /*
    @Override
    public void run(String... args) throws Exception {
        Publisher<Integer> publisher = new TestPublisher();
        Subscriber<Integer> subscriber = new TestSubscriber();
        publisher.subscribe(subscriber);
    }*/
}
