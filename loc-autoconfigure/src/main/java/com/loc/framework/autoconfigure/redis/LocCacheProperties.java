package com.loc.framework.autoconfigure.redis;

import java.time.Duration;
import lombok.Data;

/**
 * Created on 2018/4/4.
 */
@Data
public class LocCacheProperties {

  private Duration timeToLive;
  private boolean cacheNullValues = true;
  private String keyPrefix;
  private boolean useKeyPrefix = true;
}
