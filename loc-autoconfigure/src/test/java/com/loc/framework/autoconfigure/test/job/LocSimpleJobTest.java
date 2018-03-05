package com.loc.framework.autoconfigure.test.job;

import static org.assertj.core.api.Assertions.assertThat;

import com.dangdang.ddframe.job.api.ShardingContext;
import com.dangdang.ddframe.job.api.dataflow.DataflowJob;
import com.dangdang.ddframe.job.api.simple.SimpleJob;
import com.loc.framework.autoconfigure.elasticjob.LocElasticJob;
import com.loc.framework.autoconfigure.elasticjob.LocElasticJobAutoConfiguration;
import com.loc.framework.autoconfigure.springmvc.BasicResult;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.util.Lists;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.ServletWebServerFactoryAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.Environment;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created on 2018/3/5.
 */
@RunWith(SpringRunner.class)
@WebMvcTest
@TestPropertySource(properties = {
    "loc.elasticjob.serverList = 127.0.0.1:2181",
    "loc.elasticjob.namespace = jobs/job_unit_test",
    "loc.elasticjob.maxRetries = 1",
})
@DirtiesContext
public class LocSimpleJobTest {

  @Autowired
  private Environment environment;

  @Test
  public void testJob() {
    assertThat(environment.getProperty("loc.elasticjob.serverList")).isEqualTo("127.0.0.1:2181");
    assertThat(environment.getProperty("loc.elasticjob.namespace")).isEqualTo("jobs/job_unit_test");
    assertThat(environment.getProperty("loc.elasticjob.maxRetries")).isEqualTo("1");
  }

  @Slf4j
  @LocElasticJob(cron = "0/1 * * * * ?")
  public static class LocSimpleJob implements SimpleJob {

    @Override
    public void execute(ShardingContext shardingContext) {
      log.info("execute simple job");
    }
  }

  @Slf4j
  @LocElasticJob(cron = "* 0/1 * * * ?")
  public static class LocDataFlowJob implements DataflowJob {

    @Override
    public List fetchData(ShardingContext shardingContext) {
      log.info("data flow job fetch data");
      return Lists.newArrayList("111", "222");
    }

    @Override
    public void processData(ShardingContext shardingContext, List list) {
      log.info("data flow job process data, list is {}", list.stream().collect(Collectors.joining(",")));
    }
  }

  @MinimalWebConfiguration
  @RestController
  @Validated
  public static class JdbcController {


    @GetMapping(value = "/job/get")
    public BasicResult<String> jobGet() {
      return BasicResult.success();
    }
  }

  @Target(ElementType.TYPE)
  @Retention(RetentionPolicy.RUNTIME)
  @Documented
  @Configuration
  @Import({
      ServletWebServerFactoryAutoConfiguration.class,
      JacksonAutoConfiguration.class,
      LocElasticJobAutoConfiguration.class
  })
  protected @interface MinimalWebConfiguration {

  }
}
