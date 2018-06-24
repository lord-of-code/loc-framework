package com.loc.framework.autoconfigure.test.springmvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.loc.framework.autoconfigure.keycloak.LocNoSecurityAutoConfiguration;
import com.loc.framework.autoconfigure.shutdown.TomcatGracefulShutdownAutoConfiguration;
import com.loc.framework.autoconfigure.springmvc.LocAdviceErrorAutoConfiguration;
import com.loc.framework.autoconfigure.springmvc.LocSpringMvcAutoConfiguration;
import com.loc.framework.autoconfigure.test.springmvc.LocBasicResultTest.BasicRequestController;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.List;
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
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.zalando.problem.Problem;

/**
 * Created on 2017/12/6.
 */
@RunWith(SpringRunner.class)
@WebMvcTest(value = BasicRequestController.class)
@TestPropertySource(properties = {
    "loc.tomcat.shutdown.waitTime = 5"
})
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
    public Problem responseBasicSuccess() {
      Demo demo = new Demo();
      demo.setName("thomas");
      demo.setAge(29);
      demo.setAddress(Lists.newArrayList("a1", "a2"));
      return Problem.builder().with("data", demo).build();
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
      LocAdviceErrorAutoConfiguration.class,
      LocSpringMvcAutoConfiguration.class,
      TomcatGracefulShutdownAutoConfiguration.class,
      LocNoSecurityAutoConfiguration.class
  })
  protected @interface MinimalWebConfiguration {

  }
}
