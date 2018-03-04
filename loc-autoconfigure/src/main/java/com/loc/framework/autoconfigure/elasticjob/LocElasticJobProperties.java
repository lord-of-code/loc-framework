package com.loc.framework.autoconfigure.elasticjob;

import javax.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.validation.annotation.Validated;

@Validated
@Data
public class LocElasticJobProperties {

  @NotBlank(message = "serverList not blank")
  private String serverList;
  @NotBlank(message = "namespace not blank")
  private String namespace;

  private String dataSource;
  private int baseSleepTimeMilliseconds = 1000;
  private int maxSleepTimeMilliseconds = 3000;
  private int maxRetries = 3;
  private int sessionTimeoutMilliseconds = 60000;
  private int connectionTimeoutMilliseconds = 15000;

}
