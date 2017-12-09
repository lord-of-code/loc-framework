package com.loc.framework.autoconfigure.springmvc;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;


/**
 * Created on 2017/11/30.
 */
@Data
@ConfigurationProperties(prefix = "loc.web.springmvc")
public class LocSpringMvcProperties {
  private boolean includeRequest = true;
  private boolean includeResponse = false;

  private int requestBodyLength = 8192;
  private int responseBodyLength = 8192;
}
