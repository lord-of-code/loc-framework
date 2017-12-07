package com.loc.framework.autoconfigure.test.springmvc;

import com.google.common.collect.Lists;
import com.loc.framework.autoconfigure.springmvc.BasicResult;
import com.loc.framework.autoconfigure.springmvc.LocAccessLogFilter;
import com.loc.framework.autoconfigure.springmvc.LocSpringMvcProperties;
import com.loc.framework.autoconfigure.test.springmvc.LocAccessLogTest.GetController;
import com.loc.framework.autoconfigure.test.springmvc.LocAccessLogTest.MinimalWebConfiguration;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.List;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.ServletWebServerFactoryAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
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

    this.mockMvc = MockMvcBuilders
        .standaloneSetup(new GetController())
        .setMessageConverters(jackson2HttpMessageConverter)
        .addFilters(new LocAccessLogFilter(requestProperties))
        .build();
  }


  @MinimalWebConfiguration
  @RestController
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

    @GetMapping(value = "/requestParam/fail")
    public BasicResult<Demo> responseParamFail(
        @RequestParam @NotNull String name,
        @RequestParam @NotNull int age,
        @RequestParam @NotNull List<String> address) {
      Demo demo = new Demo();
      demo.setName(name);
      demo.setAge(age);
      demo.setAddress(address);
      return BasicResult.success(demo);
    }

    @PostMapping(value = "/formParam/fail")
    public BasicResult<Demo> responseFormFail(
        @Validated @RequestBody Demo demo) {
      return BasicResult.success(demo);
    }

    @GetMapping(value = "/runtime/exception")
    public BasicResult<Demo> runtimeException() {
      throw new RuntimeException("runtime fail");
    }

    @GetMapping(value = "/loc/exception")
    public BasicResult<Demo> locException() {
      throw new RuntimeException("runtime fail");
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
