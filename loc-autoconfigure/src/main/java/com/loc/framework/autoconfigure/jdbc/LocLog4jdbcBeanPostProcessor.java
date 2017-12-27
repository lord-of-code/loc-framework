package com.loc.framework.autoconfigure.jdbc;

import com.zaxxer.hikari.HikariDataSource;
import javax.annotation.PostConstruct;
import net.sf.log4jdbc.log.slf4j.Slf4jSpyLogDelegator;
import net.sf.log4jdbc.sql.jdbcapi.DataSourceSpy;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.env.Environment;

/**
 * Created on 2017/12/27.
 */
public class LocLog4jdbcBeanPostProcessor implements BeanPostProcessor {

  @Autowired
  private Environment environment;

  private static final String[] PROPERTIES_TO_COPY = {
      "loc.log4jdbc.debug.stack.prefix",
      "loc.log4jdbc.sqltiming.warn.threshold",
      "loc.log4jdbc.sqltiming.error.threshold",
  };

  @Override
  public Object postProcessBeforeInitialization(final Object bean, final String beanName)
      throws BeansException {
    if (bean instanceof HikariDataSource) {
      return new DataSourceSpy((HikariDataSource) bean);
    } else {
      return bean;
    }
  }

  @Override
  public Object postProcessAfterInitialization(final Object bean, final String beanName)
      throws BeansException {
    return bean;
  }

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
