package com.loc.framework.autoconfigure.test.springmvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.loc.framework.autoconfigure.springmvc.BasicResult;
import com.loc.framework.autoconfigure.springmvc.LocAdviceErrorConfig;
import com.loc.framework.autoconfigure.springmvc.LocCommonException;
import com.loc.framework.autoconfigure.springmvc.LocSpringMvcConfig;
import com.loc.framework.autoconfigure.test.springmvc.LocBasicResultTest.BasicRequestController;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.ServletWebServerFactoryAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created on 2017/12/6.
 */
@RunWith(SpringRunner.class)
@WebMvcTest(value = BasicRequestController.class)
@DirtiesContext
public class LocBasicResultTest {

  @Autowired
  private MockMvc mockMvc;


  @Test
  public void getSuccess() throws Exception {
    this.mockMvc
        .perform(get("/basic/success").accept("application/json"))
        .andExpect(jsonPath("$.code").value("200000"))
        .andExpect(jsonPath("$.msg").value("success"))
        .andExpect(jsonPath("$.detailMsg").value("success"))
        .andExpect(jsonPath("$.data.name").value("thomas"))
        .andExpect(jsonPath("$.data.age").value("29"))
        .andExpect(status().isOk()).andReturn();
  }

  @Test
  public void getFail() throws Exception {
    this.mockMvc
        .perform(get("/basic/fail").accept("application/json"))
        .andExpect(jsonPath("$.code").value("200001"))
        .andExpect(jsonPath("$.msg").value("显示的错误"))
        .andExpect(jsonPath("$.detailMsg").value("详细的错误"))
        .andExpect(status().isOk()).andReturn();
  }

  @Test
  public void getBodyFail() throws Exception {
    LocAccessLogTest.Demo demo = new LocAccessLogTest.Demo();
    demo.setName("thomas12141536");
    demo.setAge(29);
    List<String> address = Lists.newArrayList();
    for (int i = 0; i < 2; i++) {
      address.add("a" + i);
    }
    demo.setAddress(address);

    ObjectMapper objectMapper = new ObjectMapper();
    String content = objectMapper.writeValueAsString(demo);

    this.mockMvc.perform(post("/bodyParam/fail")
        .content(content)
        .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
        .andDo(print())
        .andExpect(jsonPath("$.code").value("400000"))
        .andExpect(jsonPath("$.msg").value("method argument miss error"))
        .andExpect(jsonPath("$.detailMsg").value("name长度范围是1～10"))
        .andExpect(status().is4xxClientError()).andReturn();

    demo.setName("thomas");
    demo.setAddress(null);
    content = objectMapper.writeValueAsString(demo);
    this.mockMvc.perform(post("/bodyParam/fail")
        .content(content)
        .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
        .andDo(print())
        .andExpect(jsonPath("$.code").value("400000"))
        .andExpect(jsonPath("$.msg").value("method argument miss error"))
        .andExpect(jsonPath("$.detailMsg").value("address不能为空"))
        .andExpect(status().is4xxClientError()).andReturn();
  }

  @Test
  public void getFormFail() throws Exception {
    this.mockMvc
        .perform(post("/formParam/fail")
            .param("age", "29")
            .param("address", "a1", "a2")
            .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE))
        .andDo(print())
        .andExpect(jsonPath("$.code").value("400000"))
        .andExpect(jsonPath("$.msg").value("method argument miss error"))
        .andExpect(jsonPath("$.detailMsg").value("参数name未传"))
        .andExpect(status().is4xxClientError()).andReturn();

    this.mockMvc
        .perform(post("/formParam/fail")
            .param("name", "thomas12141536")
            .param("age", "29")
            .param("address", "a1", "a2")
            .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE))
        .andDo(print())
        .andExpect(jsonPath("$.code").value("400001"))
        .andExpect(jsonPath("$.msg").value("method argument validate error"))
        .andExpect(jsonPath("$.detailMsg").value("responseParamFail.name:字符串长度在1~10之间"))
        .andExpect(status().is4xxClientError()).andReturn();

    this.mockMvc
        .perform(post("/formParam/fail")
            .param("name", "thomas")
            .param("age", "29")
            .param("address", "a1", "a2", "a3", "a4", "a5", "a6")
            .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE))
        .andDo(print())
        .andExpect(jsonPath("$.code").value("400001"))
        .andExpect(jsonPath("$.msg").value("method argument validate error"))
        .andExpect(jsonPath("$.detailMsg").value("responseParamFail.address:数组长度范围在1～3之间"))
        .andExpect(status().is4xxClientError()).andReturn();
  }

  @Test
  public void methodExceptionTest() throws Exception {
    this.mockMvc
        .perform(post("/runtime/exception"))
        .andDo(print())
        .andExpect(jsonPath("$.code").value("405000"))
        .andExpect(jsonPath("$.msg").value("method not allow error"))
        .andExpect(jsonPath("$.detailMsg").value("Request method 'POST' not supported"))
        .andExpect(status().is4xxClientError()).andReturn();
  }


  @Test
  public void runtimeExceptionTest() throws Exception {
    this.mockMvc
        .perform(get("/runtime/exception").accept("application/json"))
        .andDo(print())
        .andExpect(jsonPath("$.code").value("500001"))
        .andExpect(jsonPath("$.msg").value("runtime exception"))
        .andExpect(jsonPath("$.detailMsg").value("运行时错误"))
        .andExpect(status().is5xxServerError()).andReturn();
  }

  @Test
  public void outOfSizeTest() throws Exception {
    this.mockMvc
        .perform(get("/runtime/outofsize").accept("application/json"))
        .andDo(print())
        .andExpect(jsonPath("$.code").value("500001"))
        .andExpect(jsonPath("$.msg").value("runtime exception"))
        .andExpect(jsonPath("$.detailMsg").value("Index: 10, Size: 0"))
        .andExpect(status().is5xxServerError()).andReturn();
  }


  @Test
  public void locExceptionTest() throws Exception {
    this.mockMvc
        .perform(get("/loc/exception1").accept("application/json"))
        .andDo(print())
        .andExpect(jsonPath("$.code").value("200001"))
        .andExpect(jsonPath("$.msg").value("loc exception"))
        .andExpect(jsonPath("$.detailMsg").value("loc exception"))
        .andExpect(status().isOk()).andReturn();

    this.mockMvc
        .perform(get("/loc/exception2").accept("application/json"))
        .andDo(print())
        .andExpect(jsonPath("$.code").value("200002"))
        .andExpect(jsonPath("$.msg").value("loc exception"))
        .andExpect(jsonPath("$.detailMsg").value("loc exception"))
        .andExpect(status().isOk()).andReturn();

    this.mockMvc
        .perform(get("/loc/exception3").accept("application/json"))
        .andDo(print())
        .andExpect(jsonPath("$.code").value("200003"))
        .andExpect(jsonPath("$.msg").value("loc exception"))
        .andExpect(jsonPath("$.detailMsg").value("detail exception msg"))
        .andExpect(status().isOk()).andReturn();

    this.mockMvc
        .perform(get("/loc/exception4").accept("application/json"))
        .andDo(print())
        .andExpect(jsonPath("$.code").value("200004"))
        .andExpect(jsonPath("$.msg").value("loc exception"))
        .andExpect(jsonPath("$.detailMsg").value("detail exception msg"))
        .andExpect(status().isOk()).andReturn();
  }

  @Test
  public void mediaTypeExceptionTest() throws Exception {
    this.mockMvc
        .perform(post("/mediatype/exception").contentType(MediaType.APPLICATION_JSON_UTF8))
        .andDo(print())
        .andExpect(jsonPath("$.code").value("415000"))
        .andExpect(jsonPath("$.msg").value("unsupported media type error"))
        .andExpect(jsonPath("$.detailMsg").value("媒体类型application/json;charset=UTF-8错误"))
        .andExpect(status().is4xxClientError()).andReturn();
  }


  @MinimalWebConfiguration
  @RestController
  @Validated
  public static class BasicRequestController {

    @GetMapping(value = "/basic/success")
    public BasicResult<Demo> responseBasicSuccess() {
      Demo demo = new Demo();
      demo.setName("thomas");
      demo.setAge(29);
      demo.setAddress(Lists.newArrayList("a1", "a2"));
      return BasicResult.success(demo);
    }

    @GetMapping(value = "/basic/fail")
    public BasicResult<Demo> responseBasicFail() {
      return BasicResult.fail(200_001, "显示的错误", "详细的错误");
    }

    @PostMapping(value = "/formParam/fail")
    public BasicResult<Demo> responseParamFail(
        @RequestParam @Size(min = 1, max = 10, message = "字符串长度在1~10之间") String name,
        @NotNull(message = "age不能为空") @RequestParam int age,
        @NotNull(message = "address不能为空") @Size(min = 1, max = 3, message = "数组长度范围在1～3之间") @RequestParam(required = false) List<String> address) {
      Demo demo = new Demo();
      demo.setName(name);
      demo.setAge(age);
      demo.setAddress(address);
      return BasicResult.success(demo);
    }

    @PostMapping(value = "/bodyParam/fail")
    public BasicResult<Demo> responseFormFail(
        @Valid @RequestBody Demo demo) {
      return BasicResult.success(demo);
    }

    @GetMapping(value = "/runtime/exception")
    public BasicResult<Demo> runtimeException() {
      throw new RuntimeException("运行时错误");
    }

    @GetMapping(value = "/runtime/outofsize")
    public BasicResult<Demo> outOfSizeException() {
      List<String> lists = Lists.newArrayList();
      lists.get(10);
      Demo demo = new Demo();
      return BasicResult.success(demo);
    }

    @GetMapping(value = "/loc/exception1")
    public BasicResult<Demo> locException1() {
      throw new LocCommonException(200_001, "loc exception");
    }

    @GetMapping(value = "/loc/exception2")
    public BasicResult<Demo> locException2() {
      throw new LocCommonException(200_002, "loc exception",
          new IllegalArgumentException("illegal argument"));
    }

    @GetMapping(value = "/loc/exception3")
    public BasicResult<Demo> locException3() {
      throw new LocCommonException(200_003, "loc exception", "detail exception msg");
    }

    @GetMapping(value = "/loc/exception4")
    public BasicResult<Demo> locException4() {
      throw new LocCommonException(200_004, "loc exception", "detail exception msg",
          new IllegalArgumentException("illegal argument"));
    }

    @PostMapping(value = "/mediatype/exception", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public BasicResult<Demo> mediaTypeException() {
      Demo demo = new Demo();
      return BasicResult.success(demo);
    }
  }


  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  public static class Demo {

    @Size(min = 1, max = 10, message = "name长度范围是1～10")
    private String name;
    private int age;
    @NotNull(message = "address不能为空")
    private List<String> address;
  }

  @Target(ElementType.TYPE)
  @Retention(RetentionPolicy.RUNTIME)
  @Documented
  @Configuration
  @Import({
      ServletWebServerFactoryAutoConfiguration.class,
      JacksonAutoConfiguration.class,
      LocAdviceErrorConfig.class,
      LocSpringMvcConfig.class
  })
  protected @interface MinimalWebConfiguration {

  }
}
