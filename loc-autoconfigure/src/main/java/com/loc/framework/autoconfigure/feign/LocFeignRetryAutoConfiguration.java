package com.loc.framework.autoconfigure.feign;

import com.loc.framework.autoconfigure.springmvc.BasicResult;
import com.loc.framework.autoconfigure.springmvc.BasicResultCode;
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

    public BasicResult handleException(Throwable e) {
      log.error("feign error: {}", e.getMessage(), e);
      if (e instanceof HystrixTimeoutException) {
        return BasicResult.fail(BasicResultCode.HTTP_HYSTRIX_TIME_OUT.getCode(),
            BasicResultCode.HTTP_HYSTRIX_TIME_OUT.getMsg());
      } else if (e instanceof feign.RetryableException) {
        if (StringUtils.isNotBlank(e.getLocalizedMessage())) {
          if (e.getLocalizedMessage().contains("Connection refused")) {
            return BasicResult.fail(BasicResultCode.HTTP_CONNECTION_TIME_OUT.getCode(),
                BasicResultCode.HTTP_CONNECTION_TIME_OUT.getMsg());
          } else if (e.getLocalizedMessage().contains("Read timed out")) {
            return BasicResult.fail(BasicResultCode.HTTP_HYSTRIX_TIME_OUT.getCode(),
                BasicResultCode.HTTP_HYSTRIX_TIME_OUT.getMsg());
          }
        }
      } else if (e instanceof RuntimeException) {
        if (e.getCause() != null) {
          String message = e.getCause().getLocalizedMessage();
          if (StringUtils.isNotBlank(message) && message.contains(LOAD_BALANCER_NOT_AVAILABLE)) {
            return BasicResult.fail(BasicResultCode.HTTP_SERVICE_NOT_AVAILABLE.getCode(),
                BasicResultCode.HTTP_SERVICE_NOT_AVAILABLE.getMsg());
          }
        }
      }
      return BasicResult
          .fail(BasicResultCode.HTTP_ERROR.getCode(), BasicResultCode.HTTP_ERROR.getMsg());
    }
  }
}
