package com.loc.framework.autoconfigure.springmvc;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

/**
 * Created on 2017/11/30.
 */
@Data
@ConfigurationProperties(prefix = "loc.web.springmvc")
public class LocSpringMvcProperties {
  private boolean includeRequestHeaders = true;
  private boolean includeRequestBody = true;
  private boolean includeResponseHeaders = false;
  private boolean includeResponseBody = false;

  private int requestBodyLength = 8192;
  private int responseBodyLength = 8192;
}
