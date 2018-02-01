package com.loc.framework.autoconfigure.test.springmvc;

import static org.assertj.core.api.Assertions.assertThat;

import com.loc.framework.autoconfigure.redis.RedisCacheAutoConfiguration;
import com.loc.framework.autoconfigure.springmvc.BasicResult;
import com.loc.framework.autoconfigure.springmvc.LocSpringMvcAutoConfiguration;
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
import org.springframework.data.redis.cache.RedisCacheConfiguration;
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
@DirtiesContext
public class LocRedisTest {

  @Autowired
  private RedisCacheConfiguration redisCacheConfiguration;


  @Test
  public void testRedisCacheConfiguration() {
    assertThat(redisCacheConfiguration).isNotNull();
    assertThat(redisCacheConfiguration.getValueSerializationPair()).isNotNull();
  }


  @Target(ElementType.TYPE)
  @Retention(RetentionPolicy.RUNTIME)
  @Documented
  @Configuration
  @Import({
      ServletWebServerFactoryAutoConfiguration.class,
      JacksonAutoConfiguration.class,
      RedisCacheAutoConfiguration.class
  })
  protected @interface MinimalWebConfiguration {

  }
}
