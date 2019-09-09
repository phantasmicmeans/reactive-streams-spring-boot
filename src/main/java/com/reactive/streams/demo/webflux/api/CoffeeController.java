package com.reactive.streams.demo.webflux.api;

import com.reactive.streams.demo.webflux.data.Coffee;
import org.springframework.data.redis.core.ReactiveRedisOperations;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/employee")
public class CoffeeController {
    private final ReactiveRedisOperations<String, Coffee> coffeeOps;
    CoffeeController(ReactiveRedisOperations<String, Coffee> coffeeOps){
        this.coffeeOps = coffeeOps;
    }

    @GetMapping("/coffees")
    public Flux<Coffee> all() {
        return coffeeOps.keys("*")
                .flatMap(element -> coffeeOps.opsForValue().get(element));
    }

}
