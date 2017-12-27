package com.loc.framework.autoconfigure.jdbc;

import static com.google.common.base.Preconditions.checkArgument;

import com.google.common.base.Strings;
import com.zaxxer.hikari.HikariDataSource;
import java.util.Optional;
import javax.sql.DataSource;
import lombok.extern.slf4j.Slf4j;
import net.sf.log4jdbc.sql.jdbcapi.DataSourceSpy;
import org.springframework.beans.BeansException;
import org.springframework.beans.FatalBeanException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.context.properties.source.ConfigurationPropertySources;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.aspectj.AnnotationTransactionAspect;

/**
 * Created on 2017/12/26.
 */
@Slf4j
@Configuration
@ConditionalOnClass({
    DataSource.class, HikariDataSource.class, DataSourceSpy.class
})
public class LocDataSourceAutoConfiguration implements BeanFactoryPostProcessor, EnvironmentAware {

  private ConfigurableEnvironment environment;

  @Override
  public void postProcessBeanFactory(
      ConfigurableListableBeanFactory configurableListableBeanFactory) throws BeansException {
    LocDsProperties locDsProperties = resolverSetting(LocDsProperties.class);
    locDsProperties.getDataSource().forEach(
        (name, properties) -> createBean(configurableListableBeanFactory, name, properties));
  }

  @Override
  public void setEnvironment(Environment environment) {
    this.environment = (ConfigurableEnvironment) environment;
  }


  private void createBean(ConfigurableListableBeanFactory configurableListableBeanFactory,
      String prefixName, JdbcProperties jdbcProperties) {
    String jdbcUrl = jdbcProperties.getJdbcUrl();
    checkArgument(!Strings.isNullOrEmpty(jdbcUrl), prefixName + " url is null or empty");
    log.info("prefixName is {}, jdbc properties is {}", prefixName, jdbcProperties);

    HikariDataSource hikariDataSource = createHikariDataSource(jdbcProperties);
    DataSourceSpy dataSource = new DataSourceSpy(hikariDataSource);

    DataSourceTransactionManager transactionManager = new DataSourceTransactionManager(dataSource);
    AnnotationTransactionAspect.aspectOf().setTransactionManager(transactionManager);

    JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);

    register(configurableListableBeanFactory, dataSource, prefixName + "DataSource",
        prefixName + "Ds");
    register(configurableListableBeanFactory, jdbcTemplate, prefixName + "JdbcTemplate",
        prefixName + "Jt");
    register(configurableListableBeanFactory, transactionManager, prefixName + "TransactionManager",
        prefixName + "Tx");
  }

  private void register(ConfigurableListableBeanFactory beanFactory, Object bean, String name,
      String alias) {
    beanFactory.registerSingleton(name, bean);
    if (!beanFactory.containsSingleton(alias)) {
      beanFactory.registerAlias(name, alias);
    }
  }

  private HikariDataSource createHikariDataSource(JdbcProperties jdbcProperties) {
    HikariDataSource hikariDataSource = new HikariDataSource();
    hikariDataSource.setJdbcUrl(jdbcProperties.getJdbcUrl());
    hikariDataSource.setUsername(jdbcProperties.getUsername());
    hikariDataSource.setPassword(jdbcProperties.getPassword());

    JdbcPoolProperties jdbcPoolProperties = jdbcProperties.getJdbcPool();
    hikariDataSource.setAutoCommit(jdbcPoolProperties.isAutoCommit());
    hikariDataSource.setConnectionTimeout(jdbcPoolProperties.getConnectionTimeout());
    hikariDataSource.setIdleTimeout(jdbcPoolProperties.getIdleTimeout());
    hikariDataSource.setMaxLifetime(jdbcPoolProperties.getMaxLifetime());
    hikariDataSource.setMaximumPoolSize(jdbcPoolProperties.getMaximumPoolSize());
    hikariDataSource.setMinimumIdle(jdbcPoolProperties.getMinimumIdle());
    hikariDataSource
        .setInitializationFailTimeout(jdbcPoolProperties.getInitializationFailTimeout());
    hikariDataSource.setIsolateInternalQueries(jdbcPoolProperties.isIsolateInternalQueries());
    hikariDataSource.setReadOnly(jdbcPoolProperties.isReadOnly());
    hikariDataSource.setRegisterMbeans(jdbcPoolProperties.isRegisterMbeans());
    Optional.ofNullable(jdbcPoolProperties.getDriverClassName())
        .ifPresent(hikariDataSource::setDriverClassName);
    hikariDataSource.setValidationTimeout(jdbcPoolProperties.getValidationTimeout());
    hikariDataSource.setLeakDetectionThreshold(jdbcPoolProperties.getLeakDetectionThreshold());
    return hikariDataSource;
  }

  // 读取配置并转换成对象
  private <T> T resolverSetting(Class<T> clazz) {
    return new Binder(ConfigurationPropertySources.from(environment.getPropertySources()))
        .bind("loc", Bindable.of(clazz))
        .orElseThrow(() -> new FatalBeanException("Could not bind DataSourceSettings properties"));

  }
}
