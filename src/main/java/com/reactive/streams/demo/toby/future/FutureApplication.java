package com.reactive.streams.demo.toby.future;

import io.netty.channel.nio.NioEventLoopGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.Netty4ClientHttpRequestFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.AsyncRestTemplate;
import org.springframework.web.context.request.async.DeferredResult;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

@SpringBootApplication
public class FutureApplication {
    @RestController
    public static class MyController {
        AsyncRestTemplate rt = new AsyncRestTemplate(new Netty4ClientHttpRequestFactory(new NioEventLoopGroup(1)));
        @Autowired MyService myService;
        static final String URL1 = "http://localhost:8081/service?req={req}";
        static final String URL2 = "http://localhost:8081/service2?req={req}";

        @GetMapping("/rest")
        public DeferredResult<String> rest(int idx) {
            DeferredResult<String> dr = new DeferredResult<>();
            toCF(rt.getForEntity(URL1, String.class,"h" + idx))
                    .thenCompose(s -> toCF(rt.getForEntity(URL2, String.class, s.getBody())))
                    .thenCompose(s2 -> toCF(myService.work(s2.getBody())))
                    .thenAccept(s3 -> dr.setResult(s3))
                    .exceptionally(e ->  {
                        dr.setErrorResult(e.getMessage());
                        return (Void)null;
                    });
            return dr;

        }
        <T>CompletableFuture<T> toCF(ListenableFuture<T> lf) {
            CompletableFuture<T> cf = new CompletableFuture<>();
            lf.addCallback(s -> cf.complete(s) , e -> cf.completeExceptionally(e));
            return cf;
        }
    }

    public static class Completion {
        Completion next;
        Consumer<ResponseEntity<String>> con;

        public Completion(Consumer<ResponseEntity<String>> con) {
            this.con = con;
        }

        public Completion() {
        }

        public void andAccept(Consumer<ResponseEntity<String>> con) {
            Completion c = new Completion(con);
            this.next = c;
        }

        public static Completion from(ListenableFuture<ResponseEntity<String>> lf) {
            Completion c = new Completion();
            lf.addCallback(s -> {
                c.complete(s);
            }, e -> {
                c.error();
            });
            return c;
        }

        void error() {
        }

        void complete(ResponseEntity<String> s) {
            if (next != null) next.run(s);
        }

        void run(ResponseEntity<String> value) {
            if (con != null) con.accept(value);
        }


    }
    @Service
    public static class MyService {
        @Async
        public ListenableFuture<String> work(String req) {
            return new AsyncResult<>(req + "/asyncwork");
        }
    }
        @Bean
        public ThreadPoolTaskExecutor myThreadPool() {
            ThreadPoolTaskExecutor te = new ThreadPoolTaskExecutor();
            te.setCorePoolSize(1);
            te.setMaxPoolSize(1);
            te.initialize();
            return te;
    }

    public static void main(String[] args) {
        //System.setProperty("reactor.ipc.netty.workerCount", "2");
        //System.setProperty("reactor.ipc.netty.pool.maxConnections", "2000");
        SpringApplication.run(FutureApplication.class, args);
    }
}
