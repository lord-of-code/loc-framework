package com.loc.framework.okhttp.sample;

import com.loc.framework.autoconfigure.springmvc.BasicResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
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

    @PostMapping(value = "/send")
    public BasicResult send() {
      return BasicResult.success();
    }
  }

}
