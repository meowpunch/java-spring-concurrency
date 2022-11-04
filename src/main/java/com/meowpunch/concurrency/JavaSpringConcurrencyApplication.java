package com.meowpunch.concurrency;

import java.util.concurrent.Future;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Component;

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
    public Future<String> hello() throws InterruptedException {
      log.info("myService.hello() processing... ");
      Thread.sleep(2000);
      return new AsyncResult<>("Hello");
    }
  }

  final MyService myService;

  public static void main(String[] args) {
    var configurableApplicationContext = SpringApplication.run(JavaSpringConcurrencyApplication.class, args);
  }

  @Bean
  ApplicationRunner run() {
    return args -> {
      log.info("run Application");

      Future<String> future = myService.hello();

      Thread.sleep(1000);
      log.info("Is it done processing myService.hello()? " + future.isDone());
      log.info("exit with " + future.get());
    };
  }
}
