package com.loc.framework.autoconfigure.test.springmvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.google.common.collect.Lists;
import com.loc.framework.autoconfigure.springmvc.BasicResult;
import com.loc.framework.autoconfigure.springmvc.LocAccessLogFilter;
import com.loc.framework.autoconfigure.springmvc.LocAdviceErrorConfig.LocCommonExceptionConfiguration;
import com.loc.framework.autoconfigure.springmvc.LocAdviceErrorConfig.MethodArgumentNotValidExceptionConfiguration;
import com.loc.framework.autoconfigure.springmvc.LocAdviceErrorConfig.MissingServletRequestParameterExceptionConfiguration;
import com.loc.framework.autoconfigure.springmvc.LocAdviceErrorConfig.RuntimeExceptionConfiguration;
import com.loc.framework.autoconfigure.springmvc.LocCommonException;
import com.loc.framework.autoconfigure.springmvc.LocSpringMvcProperties;
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
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.ServletWebServerFactoryAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
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
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext
public class LocBasicResultTest {

  @Autowired
  private MappingJackson2HttpMessageConverter jackson2HttpMessageConverter;

  private MockMvc mockMvc;

  @Before
  public void setUp() throws Exception {
    LocSpringMvcProperties requestProperties = new LocSpringMvcProperties();
    requestProperties.setIncludeResponse(true);

    this.mockMvc = MockMvcBuilders
        .standaloneSetup(new BasicRequestController())
        .setMessageConverters(jackson2HttpMessageConverter)
        .setControllerAdvice(
            new MissingServletRequestParameterExceptionConfiguration(),
            new MethodArgumentNotValidExceptionConfiguration(),
            new LocCommonExceptionConfiguration(),
            new RuntimeExceptionConfiguration())
        .addFilters(new LocAccessLogFilter(requestProperties))
        .build();
  }

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
  public void getParamFail() throws Exception {
    this.mockMvc
        .perform(post("/requestParam/fail")
            .param("age", "29")
            .param("address", "a1", "a2")
            .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE))
        .andDo(print())
        .andExpect(jsonPath("$.code").value("400000"))
        .andExpect(jsonPath("$.msg").value("method argument error"))
        .andExpect(jsonPath("$.detailMsg").value("参数name未传"))
        .andExpect(status().isOk()).andReturn();

//    this.mockMvc
//        .perform(post("/requestParam/fail")
//            .param("name", "thomas12141536")
//            .param("age", "29")
//            .param("address", "a1", "a2")
//            .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE))
//        .andDo(print())
//        .andExpect(jsonPath("$.code").value("400000"))
//        .andExpect(jsonPath("$.msg").value("显示的错误"))
//        .andExpect(jsonPath("$.detailMsg").value("详细的错误"))
//        .andExpect(status().isOk()).andReturn();
//
//    this.mockMvc
//        .perform(post("/requestParam/fail")
//            .param("name", "thomas")
//            .param("age", "29")
//            .param("address", "a1", "a2", "a3", "a4", "a5", "a6")
//            .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE))
//        .andDo(print())
//        .andExpect(jsonPath("$.code").value("200001"))
//        .andExpect(jsonPath("$.msg").value("method argument error"))
//        .andExpect(jsonPath("$.detailMsg").value("数组长度范围在1～3之间"))
//        .andExpect(status().isOk()).andReturn();
  }


  @Test
  public void runtimeExceptionTest() throws  Exception {
    this.mockMvc
        .perform(get("/runtime/exception").accept("application/json"))
        .andDo(print())
        .andExpect(jsonPath("$.code").value("500000"))
        .andExpect(jsonPath("$.msg").value("runtime fail"))
        .andExpect(jsonPath("$.detailMsg").value("runtime fail"))
        .andExpect(status().isOk()).andReturn();
  }

  @Test
  public void outOfSizeTest() throws  Exception {
    this.mockMvc
        .perform(get("/runtime/outofsize").accept("application/json"))
        .andDo(print())
        .andExpect(jsonPath("$.code").value("500000"))
        .andExpect(jsonPath("$.msg").value("Index: 10, Size: 0"))
        .andExpect(jsonPath("$.detailMsg").value("Index: 10, Size: 0"))
        .andExpect(status().isOk()).andReturn();
  }


  @Test
  public void locExceptionTest() throws  Exception  {
    this.mockMvc
        .perform(get("/loc/exception").accept("application/json"))
        .andDo(print())
        .andExpect(jsonPath("$.code").value("200002"))
        .andExpect(jsonPath("$.msg").value("loc exception"))
        .andExpect(jsonPath("$.detailMsg").value("loc exception"))
        .andExpect(status().isOk()).andReturn();
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

    @PostMapping(value = "/requestParam/fail")
    public BasicResult<Demo> responseParamFail(
        @Size(min = 1, max = 10, message = "字符串长度在1~10之间") @RequestParam String name,
        @NotNull(message = "age不能为空") @RequestParam(required = false) int age,
        @Size(min = 1, max = 3, message = "数组长度范围在1～3之间") @RequestParam(required = false) List<String> address) {
      Demo demo = new Demo();
      demo.setName(name);
      demo.setAge(age);
      demo.setAddress(address);
      return BasicResult.success(demo);
    }

    @PostMapping(value = "/formParam/fail")
    public BasicResult<Demo> responseFormFail(
        @Valid @RequestBody Demo demo) {
      return BasicResult.success(demo);
    }

    @GetMapping(value = "/runtime/exception")
    public BasicResult<Demo> runtimeException() {
      throw new RuntimeException("runtime fail");
    }

    @GetMapping(value = "/runtime/outofsize")
    public BasicResult<Demo> OurOfSizeException() {
      List<String> lists = Lists.newArrayList();
      lists.get(10);
      Demo demo = new Demo();
      return BasicResult.success(demo);
    }

    @GetMapping(value = "/loc/exception")
    public BasicResult<Demo> locException() {
      throw new LocCommonException(200_002, "loc exception");
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


  @Configuration
  public static class WebConfig {

    @Bean
    public MappingJackson2HttpMessageConverter jackson2HttpMessageConverter() {
      MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
      Jackson2ObjectMapperBuilder builder = this.jacksonBuilder();
      converter.setObjectMapper(builder.build());
      return converter;
    }

    public Jackson2ObjectMapperBuilder jacksonBuilder() {
      return new Jackson2ObjectMapperBuilder();
    }
  }

  @Target(ElementType.TYPE)
  @Retention(RetentionPolicy.RUNTIME)
  @Documented
  @Configuration
  @Import({
      ServletWebServerFactoryAutoConfiguration.class,
      JacksonAutoConfiguration.class
  })
  protected @interface MinimalWebConfiguration {

  }
}
