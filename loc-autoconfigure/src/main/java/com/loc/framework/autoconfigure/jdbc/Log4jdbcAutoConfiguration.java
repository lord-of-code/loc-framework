package com.loc.framework.autoconfigure.jdbc;

import javax.annotation.PostConstruct;
import net.sf.log4jdbc.log.slf4j.Slf4jSpyLogDelegator;
import net.sf.log4jdbc.sql.jdbcapi.DataSourceSpy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.ConfigurableEnvironment;

/**
 * Created on 2017/12/27.
 */
@Configuration
@ConditionalOnClass(DataSourceSpy.class)
@AutoConfigureAfter(LocDataSourceAutoConfiguration.class)
public class Log4jdbcAutoConfiguration {

  @Bean
  public Log4jdbcBeanPostProcessor log4jdbcBeanPostProcessor() {
    return new Log4jdbcBeanPostProcessor();
  }

  static class Log4jdbcBeanPostProcessor {

    @Autowired
    private ConfigurableEnvironment environment;

    private static final String[] PROPERTIES_TO_COPY = {
        "log4jdbc.debug.stack.prefix",
        "log4jdbc.sqltiming.warn.threshold",
        "log4jdbc.sqltiming.error.threshold",
    };

    @PostConstruct
    public void postConstruct() {
      for (final String property : PROPERTIES_TO_COPY) {
        if (this.environment.containsProperty(property)) {
          System.setProperty(property, this.environment.getProperty(property));
        }
      }
      System.setProperty("log4jdbc.spylogdelegator.name", this.environment
          .getProperty("log4jdbc.spylogdelegator.name", Slf4jSpyLogDelegator.class.getName()));
    }
  }
}
