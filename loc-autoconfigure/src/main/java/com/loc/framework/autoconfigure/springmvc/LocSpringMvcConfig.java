package com.loc.framework.autoconfigure.springmvc;

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.servlet.Filter;

/**
 * Created on 2017/11/30.
 */
@Configuration
@ConditionalOnWebApplication
@ConditionalOnBean(WebMvcConfigurer.class)
@EnableConfigurationProperties(LocSpringMvcProperties.class)
public class LocSpringMvcConfig {


  @Bean
  @ConditionalOnClass(OncePerRequestFilter.class)
  @ConditionalOnProperty(value = "loc.web.springmvc.log.enabled", matchIfMissing = true)
  public Filter accessLogFilter(LocSpringMvcProperties hnSpringMvcProperties) {
    return new LocAccessLogFilter(hnSpringMvcProperties);
  }
}
