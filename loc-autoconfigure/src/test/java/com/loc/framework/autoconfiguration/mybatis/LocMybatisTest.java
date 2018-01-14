package com.loc.framework.autoconfiguration.mybatis;

import static org.assertj.core.api.Assertions.assertThat;

import com.github.pagehelper.PageInterceptor;
import com.loc.framework.autoconfigure.jdbc.LocDataSourceAutoConfiguration;
import com.loc.framework.autoconfigure.mybatis.LocMybatisAutoConfiguration;
import com.loc.framework.autoconfigure.mybatis.PageHelperAutoConfiguration;
import com.loc.framework.autoconfigure.springmvc.BasicResult;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.sql.DataSource;
import org.apache.ibatis.session.SqlSessionFactory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.ServletWebServerFactoryAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created on 2017/12/26.
 */
@RunWith(SpringRunner.class)
@WebMvcTest
@TestPropertySource(properties = {
    "loc.dataSource.first.username = root",
    "loc.dataSource.first.password = ",
    "loc.dataSource.first.jdbcUrl = jdbc:log4jdbc:mysql://127.0.0.1:3306/test?useUnicode=true&characterEncoding=utf8&autoReconnect=true&failOverReadOnly=false",
    "loc.dataSource.first.jdbcPool.autoCommit = false",
    "loc.dataSource.first.jdbcPool.maximumPoolSize = 30",
    "loc.dataSource.first.configLocation = classpath:mybatis/mybatis-config.xml",
    "loc.pagehelper.pageSizeZero = true",
    "log4jdbc.debug.stack.prefix = com.loc.framework.autoconfigure.test.springmvc",
    "log4jdbc.sqltiming.warn.threshold = 300",
    "log4jdbc.sqltiming.error.threshold = 2000",
})
@DirtiesContext
public class LocMybatisTest {


  @Autowired
  private SqlSessionFactory sqlSessionFactory;

  @Autowired
  private SqlSessionTemplate sqlSessionTemplate;

  @Test
  public void testMybatis() throws Exception {
    assertThat(sqlSessionFactory).isNotNull();
    assertThat(sqlSessionTemplate).isNotNull();

    assertThat(sqlSessionFactory.getConfiguration().getDefaultExecutorType().name()).isEqualTo("BATCH");
    assertThat(sqlSessionFactory.getConfiguration().getInterceptors().size()).isEqualTo(1);
    assertThat(sqlSessionFactory.getConfiguration().getInterceptors().get(0).getClass()).isEqualTo(
        PageInterceptor.class);

  }


  @MinimalWebConfiguration
  @RestController
  @Validated
  public static class MybatisController {


    @GetMapping(value = "/mybatis/get")
    public BasicResult<String> mybatisGet() {
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
      LocDataSourceAutoConfiguration.class,
      LocMybatisAutoConfiguration.class,
      PageHelperAutoConfiguration.class
  })
  protected @interface MinimalWebConfiguration {

  }
}
