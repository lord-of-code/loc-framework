package com.loc.framework.config.sample;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created on 2017/12/2.
 */
@SpringBootApplication
public class Application {

  public static void main(String[] args) {
    SpringApplication.run(Application.class, args);
  }


  @Slf4j
  @RestController
  public static class GetController {

    @Value("${loc.env}")
    private String env;

    @GetMapping(value = "/hello")
    public String helloWorld() {
      return "hello world, env is {} " + env;
    }
  }
}
