package com.loc.framework.autoconfigure.swagger;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;


@Data
@Validated
@ConfigurationProperties(prefix = "loc.web.springmvc.swagger2.apiinfo")
public class Swagger2ApiInfoProperties {

  private String title = "project title";
  private String description = "project description";
  private String version = "v1";
  private String termsOfServiceUrl = "https://github.com/lord-of-code/loc-framework";
  private String contactName = "loc project";
  private String contactUrl = "https://github.com/lord-of-code/loc-framework";
  private String contactEmail = "dragonlong1986@126.com";
  private String license = "project license";
  private String licenseUrl = "https://github.com/lord-of-code/loc-framework";
}
