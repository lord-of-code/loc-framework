package com.loc.framework.cloud.server.sample.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.zalando.problem.Problem;

/**
 * Created on 2018/4/17.
 */
@RestController
public class ServerController {

  @GetMapping("/server")
  public Problem server() {
    return Problem.builder().with("data", "hello world").build();
  }
}
