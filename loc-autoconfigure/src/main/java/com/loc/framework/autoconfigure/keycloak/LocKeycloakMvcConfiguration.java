package com.loc.framework.autoconfigure.keycloak;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Created on 2018/1/27.
 */
public class LocKeycloakMvcConfiguration implements WebMvcConfigurer {

  private final ObjectMapper objectMapper;

  LocKeycloakMvcConfiguration(ObjectMapper objectMapper) {
    this.objectMapper = objectMapper;
  }

  @Bean
  public LocKeycloakLogInterceptor locKeycloakLogInterceptor() {
    return new LocKeycloakLogInterceptor(new LocKeycloakLog(objectMapper));
  }

  @Override
  public void addInterceptors(InterceptorRegistry registry) {
    registry.addWebRequestInterceptor(locKeycloakLogInterceptor());
  }
}
