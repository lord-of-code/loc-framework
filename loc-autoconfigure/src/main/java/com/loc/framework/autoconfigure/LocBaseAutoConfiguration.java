package com.loc.framework.autoconfigure;

import org.springframework.beans.FatalBeanException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.context.properties.source.ConfigurationPropertySources;
import org.springframework.core.env.MutablePropertySources;

/**
 * Created on 2018/1/14.
 */
public abstract class LocBaseAutoConfiguration {

  protected void register(ConfigurableListableBeanFactory beanFactory, Object bean, String name,
      String alias) {
    beanFactory.registerSingleton(name, bean);
    if (!beanFactory.containsSingleton(alias)) {
      beanFactory.registerAlias(name, alias);
    }
  }

  // 读取配置并转换成对象
  protected <T> T resolverSetting(Class<T> clazz, MutablePropertySources propertySources) {
    return new Binder(ConfigurationPropertySources.from(propertySources))
        .bind("loc", Bindable.of(clazz))
        .orElseThrow(() -> new FatalBeanException("Could not bind properties"));
  }
}
