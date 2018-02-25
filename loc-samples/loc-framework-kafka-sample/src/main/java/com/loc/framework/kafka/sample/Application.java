package com.loc.framework.kafka.sample;

import com.loc.framework.autoconfigure.kafka.LocKafkaProducer;
import com.loc.framework.autoconfigure.springmvc.BasicResult;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.support.SendResult;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created on 2017/12/29.
 */
@SpringBootApplication
public class Application {

  public static void main(String[] args) {
    SpringApplication.run(Application.class, args);
  }

  @Slf4j
  @RestController
  public static class KafkaController {

    private final String TEST_TOPIC = "test_topic";

    @Autowired
    private LocKafkaProducer kafkaProducer;

    @PostMapping(value = "/send")
    public BasicResult send() {
      ListenableFuture<SendResult<byte[], byte[]>> future = kafkaProducer
          .send(TEST_TOPIC, DemoInfo.builder().name("loc").age(123).id(1000).score(100).build());
      future.addCallback(
          (result) -> log.info("send message success"),
          (e) -> log.error(e.getMessage(), e)
      );
      return BasicResult.success("send message success");
    }
  }

  @Data
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class DemoInfo {

    private long id;
    private String name;
    private int age;
    private int score;
  }

}
