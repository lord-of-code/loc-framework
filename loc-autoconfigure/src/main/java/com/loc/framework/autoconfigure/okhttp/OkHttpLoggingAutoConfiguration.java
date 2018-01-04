package com.loc.framework.autoconfigure.okhttp;


import java.util.Optional;
import okhttp3.logging.HttpLoggingInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnClass(HttpLoggingInterceptor.class)
@AutoConfigureBefore(OkHttpAutoConfiguration.class)
@EnableConfigurationProperties(OkHttpClientProperties.class)
public class OkHttpLoggingAutoConfiguration {

  @Autowired
  private OkHttpClientProperties loggingProperties;

  @Autowired(required = false)
  private HttpLoggingInterceptor.Logger logger;

  @Bean
  @ConditionalOnMissingBean
  public HttpLoggingInterceptor okHttp3LoggingInterceptor() {
    HttpLoggingInterceptor httpLoggingInterceptor = Optional.ofNullable(logger)
        .map(l -> new HttpLoggingInterceptor(logger)).orElseGet(HttpLoggingInterceptor::new);
    Optional.ofNullable(loggingProperties.getLevel()).ifPresent(httpLoggingInterceptor::setLevel);
    return httpLoggingInterceptor;
  }
}
