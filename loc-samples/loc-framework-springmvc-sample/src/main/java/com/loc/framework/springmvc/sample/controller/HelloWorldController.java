package com.loc.framework.springmvc.sample.controller;

import com.google.common.collect.Lists;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created on 2018/6/25.
 */
@Slf4j
@RestController
public class HelloWorldController {

  @GetMapping(value = "/hello")
  public Demo helloWorld() {
    Demo demo = new Demo();
    demo.setName("龙哲");
    demo.setAge(32);
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


  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  public static class Demo {

    private String name;
    private int age;
    private List<String> address;
  }
}
