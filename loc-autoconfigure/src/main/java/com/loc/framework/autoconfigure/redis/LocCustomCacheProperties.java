package com.loc.framework.autoconfigure.redis;

import java.util.Map;
import lombok.Data;

/**
 * Created on 2018/4/4.
 */
@Data
public class LocCustomCacheProperties {

  private Map<String, LocCacheProperties> customCache;
}
