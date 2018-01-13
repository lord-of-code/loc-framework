package com.loc.framework.autoconfigure.okhttp;


import java.util.Optional;
import okhttp3.logging.HttpLoggingInterceptor;
import org.springframework.beans.factory.ObjectProvider;
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

  private final HttpLoggingInterceptor.Logger logger;

  public OkHttpLoggingAutoConfiguration(ObjectProvider<HttpLoggingInterceptor.Logger> logger) {
    this.logger = logger.getIfAvailable();
  }

  @Bean
  @ConditionalOnMissingBean
  public HttpLoggingInterceptor okHttp3LoggingInterceptor(
      OkHttpClientProperties loggingProperties) {
    HttpLoggingInterceptor httpLoggingInterceptor = Optional.ofNullable(logger)
        .map(l -> new HttpLoggingInterceptor(logger)).orElseGet(HttpLoggingInterceptor::new);
    Optional.ofNullable(loggingProperties.getLevel()).ifPresent(httpLoggingInterceptor::setLevel);
    return httpLoggingInterceptor;
  }
}
