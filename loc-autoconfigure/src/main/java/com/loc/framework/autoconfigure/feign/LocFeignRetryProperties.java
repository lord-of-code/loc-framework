package com.loc.framework.autoconfigure.feign;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Created on 2018/4/11.
 */
@Data
@ConfigurationProperties(prefix = "loc-feign-retry")
public class LocFeignRetryProperties {

  private long period = 100;
  private long maxPeriod = 1000;
  private int maxAttempts = 0;

}
