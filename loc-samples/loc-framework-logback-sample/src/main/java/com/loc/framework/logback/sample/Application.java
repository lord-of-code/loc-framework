package com.loc.framework.logback.sample;

import javax.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@Slf4j
public class Application {

  @PostConstruct
  public void logSomething() {
    log.debug("debug");
    log.info("info");
    log.warn("warn");
    log.error("error");
  }

  public static void main(String[] args) throws Exception {
    SpringApplication.run(Application.class, args);
  }

}

