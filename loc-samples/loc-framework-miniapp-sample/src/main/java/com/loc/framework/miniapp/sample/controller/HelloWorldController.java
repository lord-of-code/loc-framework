package com.loc.framework.miniapp.sample.controller;

import com.loc.framework.autoconfigure.utils.ProblemUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.zalando.problem.Problem;

/**
 * Created on 2018/6/25.
 */
@Slf4j
@RestController
public class HelloWorldController {

  @GetMapping(value = "/helloWorld")
  public Problem helloWorld() {
    return ProblemUtil.createProblem("helloWorld");
  }
}
