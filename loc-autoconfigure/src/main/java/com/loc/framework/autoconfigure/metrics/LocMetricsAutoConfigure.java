package com.loc.framework.autoconfigure.metrics;

import com.google.common.collect.Sets;
import com.loc.framework.autoconfigure.utils.HostUtil;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.config.MeterFilter;
import io.micrometer.influx.InfluxMeterRegistry;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.autoconfigure.metrics.MeterRegistryCustomizer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

@Configuration
@ConditionalOnClass(InfluxMeterRegistry.class)
@ConditionalOnWebApplication
public class LocMetricsAutoConfigure {

  private static Set<String> EXCLUDE_PATH_PREFIX = Sets.newHashSet("/metrics", "/webjars", "/favicon", "/null", "/actuator", "/**", "/swagger-");

  private static final String HOST_TAG_KEY = "host";
  private static final String APP_TAG_KEY = "appname";
  private static final String IP_TAG_KEY = "ip";
  private static final String URI = "uri";

  @Value("${spring.application.name}")
  private String appName;

  @Bean
  public MeterRegistryCustomizer<MeterRegistry> metricsCommonTags() {
    return registry -> {
      registry.config().commonTags(APP_TAG_KEY, appName);
      registry.config().commonTags(HOST_TAG_KEY, HostUtil.getHost(HostUtil.getMXBeanName()));
      registry.config().commonTags(IP_TAG_KEY, registry.config().namingConvention().tagKey(HostUtil.HOST_IP));
    };
  }

  @Bean
  @Order(1)
  public MeterFilter denyMeterFilter() {
    return MeterFilter.deny(id -> {
      String uri = id.getTag(URI);
      return StringUtils.isNoneBlank(uri) && EXCLUDE_PATH_PREFIX.stream().anyMatch(uri::startsWith);
    });
  }
}
