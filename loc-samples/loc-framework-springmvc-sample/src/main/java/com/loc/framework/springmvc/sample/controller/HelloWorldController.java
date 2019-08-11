package com.loc.framework.springmvc.sample.controller;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.google.common.collect.Lists;
import com.loc.framework.autoconfigure.common.BaseResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
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
  public BaseResult<Demo> testGet(
      @Parameter(description = "名称") @RequestParam(name = "name") String name,
      @Parameter(description = "年龄") @RequestParam(name = "age") Integer age,
      @Parameter(description = "出生时间") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") @RequestParam(name = "birthTime") LocalDateTime birthTime,
      @Parameter(description = "地址") @RequestParam(name = "address") String address
  ) {
    Demo demo = new Demo();
    demo.setName(name);
    demo.setAge(age);
    demo.setBirthTime(birthTime);
    demo.setAddress(Lists.newArrayList(address));
    log.trace(demo.toString());
    log.info(demo.toString());
    log.warn(demo.toString());
    return BaseResult.success(demo);
  }

  @Operation(description = "我是一个简单的POST-FORM接口")
  @PostMapping(value = "/test/post-form")
  public BaseResult<Demo> testPostForm(@Parameter(description = "名称") @RequestParam(name = "name") String name,
      @Parameter(description = "年龄") @RequestParam(name = "age") Integer age,
      @Parameter(description = "出生时间") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") @RequestParam(name = "birthTime") LocalDateTime birthTime,
      @Parameter(description = "地址") @RequestParam(name = "address") String address) {
    Demo demo = new Demo();
    demo.setName(name);
    demo.setAge(age);
    demo.setBirthTime(birthTime);
    demo.setAddress(Lists.newArrayList(address));
    return BaseResult.success(demo);
  }

  @Operation(description = "我是一个简单的POST-BODY接口")
  @PostMapping(value = "/test/post-body", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
  public BaseResult<Demo> testPostBody(@RequestBody Demo demo) {
    return BaseResult.success(demo);
  }


  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  public static class Demo {

    @Schema(description = "姓名")
    private String name;
    @Schema(description = "年龄")
    private int age;
    @Schema(description = "出生时间，格式(yyyy-MM-dd HH:mm:ss)")
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime birthTime;
    @Schema(description = "地址列表")
    private List<String> address;
  }
}
