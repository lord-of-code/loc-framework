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
import com.loc.framework.autoconfigure.utils.ProblemUtil;
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
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.zalando.problem.Problem;

/**
 * Created on 2017/12/6.
 */
@RunWith(SpringRunner.class)
@WebMvcTest(value = BasicRequestController.class)
@TestPropertySource(properties = {
    "loc.tomcat.shutdown.waitTime = 5",
    "spring.http.encoding.force = true"
})
@DirtiesContext
public class LocBasicResultTest {

  @Autowired
  private MockMvc mockMvc;

  @Test
  public void getSuccess() throws Exception {
    this.mockMvc
        .perform(get("/basic/success").accept("application/json"))
        .andExpect(jsonPath("$.code").value("0"))
        .andExpect(jsonPath("$.detail").value("success"))
        .andExpect(jsonPath("$.data.name").value("thomas"))
        .andExpect(jsonPath("$.data.age").value("29"))
        .andExpect(jsonPath("$.data.address").value(Lists.newArrayList("a1", "a2")))
        .andExpect(status().isOk()).andReturn();
  }

  @Test
  public void getFail() throws Exception {
    this.mockMvc
        .perform(get("/basic/fail").accept("application/json"))
        .andExpect(jsonPath("$.code").value("10000"))
        .andExpect(jsonPath("$.detail").value("显示的错误"))
        .andExpect(jsonPath("$.data").doesNotExist())
        .andExpect(status().isOk()).andReturn();
  }

  @Test
  public void getBodyFail() throws Exception {
    Demo demo = new Demo();
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
        .andExpect(jsonPath("$.title").value("Constraint Violation"))
        .andExpect(jsonPath("$.status").value(400))
        .andExpect(
            jsonPath("$.violations[0].message").value("name length must between in 1 and 10"))
        .andExpect(jsonPath("$.violations[0].field").value("name"))
        .andExpect(status().is4xxClientError()).andReturn();

    demo.setName("thomas");
    demo.setAddress(null);
    content = objectMapper.writeValueAsString(demo);
    this.mockMvc.perform(post("/bodyParam/fail")
        .content(content)
        .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
        .andDo(print())
        .andExpect(jsonPath("$.title").value("Constraint Violation"))
        .andExpect(jsonPath("$.status").value(400))
        .andExpect(jsonPath("$.violations[0].message").value("address must not empty"))
        .andExpect(jsonPath("$.violations[0].field").value("address"))
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
        .andExpect(jsonPath("$.title").value("Bad Request"))
        .andExpect(jsonPath("$.status").value(400))
        .andExpect(jsonPath("$.detail").value("Required String parameter 'name' is not present"))
        .andExpect(status().is4xxClientError()).andReturn();

    this.mockMvc
        .perform(post("/formParam/fail")
            .param("name", "thomas12141536")
            .param("age", "29")
            .param("address", "a1", "a2")
            .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE))
        .andDo(print())
        .andExpect(jsonPath("$.title").value("Constraint Violation"))
        .andExpect(jsonPath("$.status").value(400))
        .andExpect(
            jsonPath("$.violations[0].message").value("name length must between in 1 and 10"))
        .andExpect(jsonPath("$.violations[0].field").value("responseParamFail.name"))
        .andExpect(status().is4xxClientError()).andReturn();

    this.mockMvc
        .perform(post("/formParam/fail")
            .param("name", "thomas")
            .param("age", "29")
            .param("address", "a1", "a2", "a3", "a4", "a5", "a6")
            .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE))
        .andDo(print())
        .andExpect(jsonPath("$.title").value("Constraint Violation"))
        .andExpect(jsonPath("$.status").value(400))
        .andExpect(
            jsonPath("$.violations[0].message").value("array length must between in 1 and 3"))
        .andExpect(jsonPath("$.violations[0].field").value("responseParamFail.address"))
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
      return ProblemUtil.createProblem(demo);
    }

    @GetMapping(value = "/basic/fail")
    public Problem responseBasicFail() {
      return ProblemUtil.createProblem("显示的错误", 10000);
    }

    @PostMapping(value = "/bodyParam/fail", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public Problem responseFormFail(
        @Valid @RequestBody Demo demo) {
      return ProblemUtil.createProblem(demo);
    }

    @PostMapping(value = "/formParam/fail")
    public Problem responseParamFail(
        @RequestParam @Size(min = 1, max = 10, message = "name length must between in 1 and 10") String name,
        @NotNull(message = "age must not empty") @RequestParam int age,
        @NotNull(message = "address must not empty") @Size(min = 1, max = 3, message = "array length must between in 1 and 3") @RequestParam(required = false) List<String> address) {
      Demo demo = new Demo();
      demo.setName(name);
      demo.setAge(age);
      demo.setAddress(address);
      return ProblemUtil.createProblem(demo);
    }
  }


  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  public static class Demo {

    @Size(min = 1, max = 10, message = "name length must between in 1 and 10")
    private String name;
    private int age;
    @NotNull(message = "address must not empty")
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
