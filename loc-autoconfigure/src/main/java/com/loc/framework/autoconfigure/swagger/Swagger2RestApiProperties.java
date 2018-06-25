package com.loc.framework.autoconfigure.swagger;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;


@Data
@ConfigurationProperties(prefix = "loc.web.springmvc.swagger2.restapi")
public class Swagger2RestApiProperties {

  private String basePackage;
  private String paths;

}
