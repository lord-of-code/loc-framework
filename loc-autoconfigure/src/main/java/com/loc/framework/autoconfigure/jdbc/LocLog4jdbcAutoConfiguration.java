package com.loc.framework.autoconfigure.jdbc;

import net.sf.log4jdbc.sql.jdbcapi.DataSourceSpy;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created on 2017/12/27.
 */
@Configuration
@ConditionalOnClass(DataSourceSpy.class)
@AutoConfigureAfter(LocDataSourceAutoConfiguration.class)
public class LocLog4jdbcAutoConfiguration {

  @Bean
  public static LocLog4jdbcBeanPostProcessor locLog4jdbcBeanPostProcessor() {
    return new LocLog4jdbcBeanPostProcessor();
  }
}
