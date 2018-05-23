package com.loc.framework.autoconfigure.feign;

import com.netflix.hystrix.exception.HystrixTimeoutException;
import feign.Feign;
import feign.Retryer;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.openfeign.FeignClientsConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.zalando.problem.Problem;
import org.zalando.problem.Status;

/**
 * Created on 2018/4/11.
 */
@ConditionalOnClass({FeignClientsConfiguration.class, Feign.class})
@Configuration
@EnableConfigurationProperties(LocFeignRetryProperties.class)
@AutoConfigureBefore(FeignClientsConfiguration.class)
@Slf4j
public class LocFeignRetryAutoConfiguration {

  @Bean
  @ConditionalOnProperty(value = "loc-feign-retry.enabled", matchIfMissing = true)
  public Retryer feignRetryer(LocFeignRetryProperties locFeignRetryProperties) {
    return new Retryer.Default(locFeignRetryProperties.getPeriod(),
        TimeUnit.MILLISECONDS.toMillis(locFeignRetryProperties.getMaxPeriod()),
        locFeignRetryProperties.getMaxAttempts());
  }

  @Bean
  public FeignExceptionHandler feignExceptionHandler() {
    return new FeignExceptionHandler();
  }


  public static class FeignExceptionHandler {

    private static final String LOAD_BALANCER_NOT_AVAILABLE = "not have available server";

    public Problem handleException(Throwable e) {
      log.error("feign error: {}", e.getMessage(), e);
      if (e instanceof HystrixTimeoutException) {
        return Problem.valueOf(Status.BAD_REQUEST, "Hystrix time out");
      } else if (e instanceof feign.RetryableException) {
        if (StringUtils.isNotBlank(e.getLocalizedMessage())) {
          if (e.getLocalizedMessage().contains("Connection refused")) {
            return Problem.valueOf(Status.REQUEST_TIMEOUT, "Connect timed out");
          } else if (e.getLocalizedMessage().contains("Read timed out")) {
            return Problem.valueOf(Status.REQUEST_TIMEOUT, "Read timed out");
          }
        }
      } else if (e instanceof RuntimeException) {
        if (e.getCause() != null) {
          String message = e.getCause().getLocalizedMessage();
          if (StringUtils.isNotBlank(message) && message.contains(LOAD_BALANCER_NOT_AVAILABLE)) {
            return Problem.valueOf(Status.SERVICE_UNAVAILABLE, "Service not available");
          }
        }
      }
      return Problem.valueOf(Status.BAD_REQUEST, "Http bad request");
    }
  }
}
