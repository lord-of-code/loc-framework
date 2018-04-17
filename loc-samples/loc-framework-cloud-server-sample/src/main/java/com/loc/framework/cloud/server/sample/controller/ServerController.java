package com.loc.framework.cloud.server.sample.controller;

import com.loc.framework.autoconfigure.springmvc.BasicResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created on 2018/4/17.
 */
@RestController
public class ServerController {

  @GetMapping("/server")
  public BasicResult server() {
    return BasicResult.success("hello world");
  }
}
