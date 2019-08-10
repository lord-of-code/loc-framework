package com.loc.framework.springmvc.sample.controller;

import com.loc.framework.autoconfigure.LocServiceException;
import com.loc.framework.autoconfigure.common.BaseResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import javax.validation.constraints.NotBlank;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@Tag(name = "我是一个用于常异测试的控制层", description = "常异测试控制层的描述")
@Slf4j
@RestController
public class ProblemController {

  @Operation(description = "我是一个业务的异常")
  @GetMapping(value = "/problem/business/exception")
  public BaseResult<String> businessException() {
    throw new LocServiceException(1001, "我一个业务异常");
  }

  @Operation(description = "我是一个运行时的异常")
  @PostMapping(value = "/problem/runtime/exception")
  public BaseResult<String> runtimeException() {
    String s = null;
    String result = s.concat("asd");
    return BaseResult.success(result);
  }

  @Operation(description = "我是一个参数异常")
  @PostMapping(value = "/problem/param/exception")
  public BaseResult<String> paramException(@Parameter(description = "名称")  @NotBlank(message = "名字是必须的") @RequestParam(name = "name") String name) {
    return BaseResult.success(name);
  }


}
