package com.loc.framework.springmvc.sample;

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

  @RestController
  public class GetController {

    @GetMapping(value = "/hello")
    public String helloWorld() {
      return "hello world";
    }
  }
}
