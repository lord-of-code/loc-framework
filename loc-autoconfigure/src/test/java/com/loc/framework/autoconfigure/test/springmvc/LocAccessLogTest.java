package com.loc.framework.autoconfigure.test.springmvc;

import com.loc.framework.autoconfigure.springmvc.LocAccessLogFilter;
import com.loc.framework.autoconfigure.springmvc.LocAccessLogger;
import com.loc.framework.autoconfigure.springmvc.LocSpringMvcConfig;
import com.loc.framework.autoconfigure.springmvc.LocSpringMvcProperties;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.autoconfigure.web.servlet.ServletWebServerFactoryAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.runner.WebApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.WebApplicationContext;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created on 2017/12/1.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext
public class LocAccessLogTest {

  private final WebApplicationContextRunner contextRunner = new WebApplicationContextRunner()
      .withConfiguration(AutoConfigurations.of(LocSpringMvcConfig.class));

  @Autowired
  private WebApplicationContext webApplicationContext;

  private MockMvc requestMockMvc;
  private MockMvc bothMockMvc;

  @Before
  public void setUp() throws Exception {
    LocSpringMvcProperties requestProperties = new LocSpringMvcProperties();
    requestMockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
        .addFilters(new LocAccessLogFilter(requestProperties)).build();

    LocSpringMvcProperties bothProperties = new LocSpringMvcProperties();
    bothProperties.setIncludeResponse(true);
    bothMockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
        .addFilters(new LocAccessLogFilter(bothProperties)).build();
  }

  @Test
  public void testLocAccessLogFilter() {
    contextRunner.withUserConfiguration(Config.class).run((context -> {
      LocAccessLogger locAccessLogger = context.getBean(LocAccessLogger.class);
      LocSpringMvcProperties properties = locAccessLogger.getProperties();
      assertThat(properties.getRequestBodyLength()).isEqualTo(8192);
      assertThat(properties.getResponseBodyLength()).isEqualTo(8192);
    }));
  }

  @Test
  public void getTest1() throws Exception {
    this.requestMockMvc.perform(get("/get/test1").accept("application/json"))
        .andExpect(status().isOk())
        .andReturn();

    this.bothMockMvc.perform(get("/get/test1").accept("application/json"))
        .andExpect(status().isOk())
        .andReturn();
  }


  @MinimalWebConfiguration
  @RestController
  static class GetController {

    @GetMapping(value = "/get/test1")
    public String responsePlainTest() {
      return "OK";
    }
  }


  @Configuration
  @Import(LocSpringMvcProperties.class)
  static class Config {

    @Bean
    public LocAccessLogger locAccessLogger(LocSpringMvcProperties locSpringMvcProperties) {
      return new LocAccessLogger(locSpringMvcProperties);
    }

  }

  @Target(ElementType.TYPE)
  @Retention(RetentionPolicy.RUNTIME)
  @Documented
  @Configuration
  @Import({ServletWebServerFactoryAutoConfiguration.class})
  protected @interface MinimalWebConfiguration {

  }

}
