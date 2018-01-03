package com.loc.framework.autoconfigure;

import org.springframework.beans.FatalBeanException;
import org.springframework.boot.autoconfigure.condition.ConditionOutcome;
import org.springframework.boot.autoconfigure.condition.SpringBootCondition;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.context.properties.source.ConfigurationPropertySources;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.type.AnnotatedTypeMetadata;


class PrefixPropertyCondition extends SpringBootCondition {

  @Override
  public ConditionOutcome getMatchOutcome(ConditionContext context,
      AnnotatedTypeMetadata metadata) {
    String prefix = (String) attribute(metadata, "prefix");
    Class<?> value = (Class<?>) attribute(metadata, "value");
    ConfigurableEnvironment environment = (ConfigurableEnvironment) context.getEnvironment();
    try {
      new Binder(ConfigurationPropertySources.from(environment.getPropertySources()))
          .bind(prefix, Bindable.of(value))
          .orElseThrow(
              () -> new FatalBeanException("Could not bind DataSourceSettings properties"));
      return new ConditionOutcome(true, String.format("Map property [%s] is not empty", prefix));
    } catch (Exception e) {
      //ignore
    }
    return new ConditionOutcome(false, String.format("Map property [%s] is empty", prefix));
  }

  private static Object attribute(AnnotatedTypeMetadata metadata, String name) {
    return metadata.getAnnotationAttributes(ConditionalOnPrefixProperty.class.getName()).get(name);
  }
}
