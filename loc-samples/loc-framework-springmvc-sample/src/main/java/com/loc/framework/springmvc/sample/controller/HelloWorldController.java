package com.loc.framework.springmvc.sample.controller;

import com.google.common.collect.Lists;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@Tag(name = "我是一个用于测试的控制层", description = "测试控制层的描述")
@Slf4j
@RestController
public class HelloWorldController {

  @Operation(description = "我是一个简单的GET接口")
  @GetMapping(value = "/test/get")
  public Demo helloWorld(
      @Parameter(description = "名称") @RequestParam(name = "name") String name,
      @Parameter(description = "年龄") @RequestParam(name = "age") Integer age,
      @Parameter(description = "地址") @RequestParam(name = "address") String address
  ) {
    Demo demo = new Demo();
    demo.setName(name);
    demo.setAge(age);
    demo.setAddress(Lists.newArrayList(address));
    return demo;
  }

  @Operation(description = "我是一个简单的POST-FORM接口")
  @PostMapping(value = "/test/post")
  public Demo sleep(@Parameter(description = "名称") @RequestParam(name = "name") String name,
      @Parameter(description = "年龄") @RequestParam(name = "age") Integer age,
      @Parameter(description = "地址") @RequestParam(name = "address") String address) {
    Demo demo = new Demo();
    demo.setName(name);
    demo.setAge(age);
    demo.setAddress(Lists.newArrayList(address));
    return demo;
  }

  @Operation(description = "我是一个简单的POST-BODY接口")
  @PostMapping(value = "/test/post")
  public Demo sleep(@RequestBody Demo demo) {
    return demo;
  }


  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  public static class Demo {

    @Schema(description = "姓名")
    private String name;
    @Schema(description = "年龄")
    private int age;
    @Schema(description = "地址列表")
    private List<String> address;
  }
}
