package com.loc.framework.autoconfigure.shutdown;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;


@ConfigurationProperties(prefix = "loc.tomcat.shutdown")
@Data
public class TomcatGracefulShutdownProperties {

  //单位秒
  private Integer waitTime = 30;


}
