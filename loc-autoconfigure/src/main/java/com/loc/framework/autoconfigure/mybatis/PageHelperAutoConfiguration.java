package com.loc.framework.autoconfigure.mybatis;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInterceptor;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import javax.annotation.PostConstruct;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Created on 2017/8/8.
 */
@Configuration
@ConditionalOnClass({PageHelper.class, SqlSessionFactory.class})
@EnableConfigurationProperties(PageHelperProperties.class)
public class PageHelperAutoConfiguration {

  private final List<SqlSessionFactory> sqlSessionFactoryList;

  private final PageHelperProperties pageHelperProperties;

  public PageHelperAutoConfiguration(ObjectProvider<List<SqlSessionFactory>> sqlSessionFactoryList,
      PageHelperProperties pageHelperProperties) {
    this.sqlSessionFactoryList = sqlSessionFactoryList.getIfAvailable();
    this.pageHelperProperties = pageHelperProperties;
  }

  @PostConstruct
  public void addPageInterceptor() {
    PageInterceptor interceptor = new PageInterceptor();
    Properties properties = pageHelperProperties.getProperties();
    interceptor.setProperties(properties);
    Optional.ofNullable(sqlSessionFactoryList).ifPresent(list -> list.forEach(
        sqlSessionFactory -> sqlSessionFactory.getConfiguration().addInterceptor(interceptor)));
  }
}
