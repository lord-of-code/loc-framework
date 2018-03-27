package com.loc.framework.autoconfigure.springmvc;

import com.google.common.collect.Lists;
import java.util.List;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;


/**
 * Created on 2017/11/30.
 */
@Data
@ConfigurationProperties(prefix = "loc.web.springmvc.cors")
public class LocSpringMvcCorsProperties {

  private String path;

  private List<String> allowOrigins = Lists.newArrayList("*");
  private List<String> allowHeaders = Lists.newArrayList("*");
  private List<String> allowMethods = Lists.newArrayList("*");
  private List<String> allowExposeHeaders;
}
