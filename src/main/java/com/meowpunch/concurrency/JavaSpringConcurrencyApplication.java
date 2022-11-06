package com.meowpunch.concurrency;

import java.util.concurrent.Future;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;

@Slf4j
@EnableAsync
@SpringBootApplication
public class JavaSpringConcurrencyApplication {

  public JavaSpringConcurrencyApplication(MyService myService) {
    this.myService = myService;
  }

  @Component
  static class MyService {

    @Async
    public java.util.concurrent.Future<String> futureHello() throws InterruptedException {
      log.info("myService.futureHello() processing... ");
      Thread.sleep(2000);
      return new AsyncResult<>("Hello");
    }

    @Async
    public org.springframework.util.concurrent.ListenableFuture<String> listenableFutureHello() throws InterruptedException {
      log.info("myService.listenableFutureHello() processing... ");
      Thread.sleep(2000);
      return new AsyncResult<>("Hello");
    }
  }

  final MyService myService;

  public static void main(String[] args) {
    var configurableApplicationContext = SpringApplication.run(JavaSpringConcurrencyApplication.class, args);
  }

  @Bean
  ApplicationRunner runFuture() {
    return args -> {
      log.info("run future");

      Future<String> futureHello = myService.futureHello();

      Thread.sleep(1000);
      log.info("Is it done processing " + futureHello.getClass().getName() + "? " + futureHello.isDone());
      log.info("exit with " + futureHello.get()); // asynchronous and blocking
    };
  }

  @Bean
  ApplicationRunner runListenableFuture() {
    return args -> {
      log.info("run listenableFuture");

      ListenableFuture<String> listenableFutureHello = myService.listenableFutureHello();
      listenableFutureHello.addCallback(str -> log.info("exit with " + str), err -> log.error(err.getMessage()));

      Thread.sleep(1000);
      log.info("Is it done processing " + listenableFutureHello.getClass().getName() + "? " + listenableFutureHello.isDone()); // unnecessary
    };
  }
}
