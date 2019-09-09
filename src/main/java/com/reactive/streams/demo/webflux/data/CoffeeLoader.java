package com.reactive.streams.demo.webflux.data;

import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisOperations;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import javax.annotation.PostConstruct;
import java.util.UUID;

@Component
public class CoffeeLoader {
    private final ReactiveRedisConnectionFactory factory;
    private final ReactiveRedisOperations<String, Coffee> coffeeOps;

    public CoffeeLoader(ReactiveRedisConnectionFactory factory, ReactiveRedisOperations<String, Coffee> coffeeOps) {
        this.factory = factory;
        this.coffeeOps = coffeeOps;
    }

    /*
    In functional programming, flatMap returns the same type than the type that bear the method,
    so for Mono<T>, flatMap returns a Mono.
    Which means that only one element can be emitted by the inner Publisher (or that it is truncated).
    We enforced that by having Mono#flatMap take a Function<T, Mono<R>>.
    */
    @PostConstruct
    public void loadData() {
        factory.getReactiveConnection().serverCommands().flushAll().thenMany(
                Flux.just("Jet Black Redis", "Darth Redis", "Black Alert Redis")
                    .map(name -> new Coffee(UUID.randomUUID().toString(), name))
                    .flatMap(coffee -> coffeeOps.opsForValue().set(coffee.getId(), coffee)))
                .thenMany(coffeeOps.keys("*")
                        .flatMap(coffeeOps.opsForValue()::get))
                .subscribe(System.out::println);
    }
}
