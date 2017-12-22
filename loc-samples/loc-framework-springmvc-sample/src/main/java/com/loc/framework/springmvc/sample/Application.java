package com.loc.framework.springmvc.sample;

import com.google.common.collect.Lists;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
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

    @GetMapping(value = "/hello")
    public Demo helloWorld() {
      Demo demo = new Demo();
      demo.setName("abc");
      demo.setAge(12);
      demo.setAddress(Lists.newArrayList());
      return demo;
    }

    @GetMapping(value = "/sleep")
    public String sleep(@RequestParam long time) throws Exception {
      long startTime = System.currentTimeMillis();
      log.info("before sleep, time is {}", time);
      Thread.sleep(time);
      log.info("after sleep, sleep time is {}", (System.currentTimeMillis() - startTime));
      return "OK";
    }
  }

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  public static class Demo {

    private String name;
    private int age;
    private List<String> address;
  }
}
