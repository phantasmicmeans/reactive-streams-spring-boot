package com.reactive.streams.demo.toby.future;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
public class RemoteService {
    @RestController
    public static class MyController {
        @GetMapping("/service")
        public String service(String req) throws InterruptedException {
            System.out.println("AA");
            Thread.sleep(1000); // 작업 시간이 있는 과정이라고 가정
            return req + "/service1";
        }

        @GetMapping("/service2")
        public String service2(String req) throws InterruptedException {
            System.out.println("BB");
            Thread.sleep(1000);
            return req + "/service2";
        }
    }

    public static void main(String[] args) {
        System.setProperty("server.port", "8081");
        System.setProperty("server.tomcat.max-threads", "1000");
        SpringApplication.run(RemoteService.class, args);
    }
}
