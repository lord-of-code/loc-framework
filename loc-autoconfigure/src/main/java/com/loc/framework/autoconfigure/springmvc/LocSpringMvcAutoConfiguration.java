package com.loc.framework.autoconfigure.springmvc;

import javax.servlet.Filter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.OncePerRequestFilter;


/**
 * Created on 2017/11/30.
 */
@Configuration
@ConditionalOnWebApplication
@EnableConfigurationProperties({LocSpringMvcLogProperties.class})
public class LocSpringMvcAutoConfiguration {

  @Bean
  @ConditionalOnClass(OncePerRequestFilter.class)
  @ConditionalOnProperty(value = "loc.web.springmvc.log.enabled", matchIfMissing = true)
  public Filter accessLogFilter(LocSpringMvcLogProperties locSpringMvcProperties) {
    return new LocAccessLogFilter(locSpringMvcProperties);
  }
}
