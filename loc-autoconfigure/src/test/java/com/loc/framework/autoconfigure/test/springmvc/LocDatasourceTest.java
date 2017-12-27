package com.loc.framework.autoconfigure.test.springmvc;

import static org.assertj.core.api.Assertions.assertThat;

import com.loc.framework.autoconfigure.jdbc.LocDataSourceAutoConfiguration;
import com.loc.framework.autoconfigure.springmvc.BasicResult;
import com.zaxxer.hikari.HikariDataSource;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.junit.Test;
import org.junit.runner.RunWith;
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
    "loc.dataSource.firstDs.username = root",
    "loc.dataSource.firstDs.password = ",
    "loc.dataSource.firstDs.jdbcUrl = jdbc:log4jdbc:mysql://127.0.0.1:3306/test?useUnicode=true&characterEncoding=utf8&autoReconnect=true&failOverReadOnly=false",
    "loc.dataSource.firstDs.jdbcPool.autoCommit = false",
    "loc.dataSource.firstDs.jdbcPool.maximumPoolSize = 30"
})
@DirtiesContext
public class LocDatasourceTest {

  @Autowired
  private HikariDataSource dataSource;


  @Test
  public void testDataSource() throws Exception {
    assertThat(dataSource).isNotNull();
    assertThat(dataSource.getUsername()).isEqualTo("root");
    assertThat(dataSource.getPassword()).isEqualTo("");
    assertThat(dataSource.getJdbcUrl()).isEqualTo("jdbc:log4jdbc:mysql://127.0.0.1:3306/test?useUnicode=true&characterEncoding=utf8&autoReconnect=true&failOverReadOnly=false");

    assertThat(dataSource.isAutoCommit()).isEqualTo(false);
    assertThat(dataSource.getConnectionTimeout()).isEqualTo(30000);
    assertThat(dataSource.getIdleTimeout()).isEqualTo(600000);
    assertThat(dataSource.getMaxLifetime()).isEqualTo(1800000);
    assertThat(dataSource.getMaximumPoolSize()).isEqualTo(30);
    assertThat(dataSource.getMinimumIdle()).isEqualTo(10);
    assertThat(dataSource.getInitializationFailTimeout()).isEqualTo(1);
    assertThat(dataSource.isIsolateInternalQueries()).isEqualTo(false);
    assertThat(dataSource.isReadOnly()).isEqualTo(false);
    assertThat(dataSource.isRegisterMbeans()).isEqualTo(false);
    assertThat(dataSource.getDriverClassName()).isEqualTo(null);
    assertThat(dataSource.getValidationTimeout()).isEqualTo(5000);
    assertThat(dataSource.getLeakDetectionThreshold()).isEqualTo(0);
  }

  @MinimalWebConfiguration
  @RestController
  @Validated
  public static class JdbcController {


    @GetMapping(value = "/jdbc/get")
    public BasicResult<String> jdbcGet() {
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
      LocDataSourceAutoConfiguration.class
  })
  protected @interface MinimalWebConfiguration {

  }
}
