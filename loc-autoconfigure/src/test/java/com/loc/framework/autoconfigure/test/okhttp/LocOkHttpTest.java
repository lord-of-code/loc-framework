package com.loc.framework.autoconfigure.test.okhttp;

import static org.assertj.core.api.Assertions.assertThat;

import com.loc.framework.autoconfigure.okhttp.OkHttpAutoConfiguration;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import okhttp3.OkHttpClient;
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
import org.zalando.problem.Problem;

/**
 * Created on 2018/1/4.
 */
@RunWith(SpringRunner.class)
@WebMvcTest
@TestPropertySource(properties = {
    "loc.okhttp.connectTimeout = 3000",
    "loc.okhttp.followRedirects = false",
    "loc.okhttp.connection.maxIdleConnections = 20",
    "loc.okhttp.level = BODY",
})
@DirtiesContext
public class LocOkHttpTest {

  @Autowired
  private OkHttpClient okHttpClient;


  @Test
  public void testOkHttp() throws Exception {
    assertThat(okHttpClient).isNotNull();
    assertThat(okHttpClient.connectTimeoutMillis()).isEqualTo(3000);
    assertThat(okHttpClient.followRedirects()).isEqualTo(false);
  }


  @MinimalWebConfiguration
  @RestController
  @Validated
  public static class OkHttpController {

    @GetMapping(value = "/okhttp/get")
    public Problem okhttpGet() {
      return Problem.builder().build();
    }
  }

  @Target(ElementType.TYPE)
  @Retention(RetentionPolicy.RUNTIME)
  @Documented
  @Configuration
  @Import({
      ServletWebServerFactoryAutoConfiguration.class,
      JacksonAutoConfiguration.class,
      OkHttpAutoConfiguration.class
  })
  protected @interface MinimalWebConfiguration {

  }
}
