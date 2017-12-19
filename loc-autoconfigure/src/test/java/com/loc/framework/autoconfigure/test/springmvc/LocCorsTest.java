package com.loc.framework.autoconfigure.test.springmvc;

import static org.assertj.core.api.Assertions.assertThat;

import com.loc.framework.autoconfigure.springmvc.BasicResult;
import com.loc.framework.autoconfigure.springmvc.LocSpringMvcConfig;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.ServletWebServerFactoryAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.filter.CorsFilter;

/**
 * Created on 2017/12/19.
 */
@RunWith(SpringRunner.class)
@WebMvcTest
@TestPropertySource(properties = {
    "loc.web.springmvc.cors.allowMethods[0] = GET",
    "loc.web.springmvc.cors.allowMethods[1] = POST",
    "loc.web.springmvc.cors.allowMethods[2] = OPTION",
    "loc.web.springmvc.cors.allowMethods[3] = HEADER",
    "loc.web.springmvc.cors.allowHeaders[0] = X-LOC-TEST-HEADER-1",
    "loc.web.springmvc.cors.allowExposeHeaders[0] = X-LOC-TEST-HEADER-2",
    "loc.web.springmvc.cors.allowExposeHeaders[1] = X-LOC-TEST-HEADER-3",
    "loc.web.springmvc.cors.enabled = true"
})
@DirtiesContext
public class LocCorsTest {

  @Autowired
  private CorsFilter corsFilter;


  @Test
  public void testApiInfo() {
    assertThat(corsFilter).isNotNull();
  }


  @MinimalWebConfiguration
  @RestController
  @Validated
  public static class CorsController {


    @GetMapping(value = "/cors/get")
    public BasicResult<String> corsGet() {
      return BasicResult.fail(200_001, "显示的错误", "详细的错误");
    }
  }


  @Target(ElementType.TYPE)
  @Retention(RetentionPolicy.RUNTIME)
  @Documented
  @Configuration
  @Import({
      ServletWebServerFactoryAutoConfiguration.class,
      JacksonAutoConfiguration.class,
      LocSpringMvcConfig.class
  })
  protected @interface MinimalWebConfiguration {

  }
}
