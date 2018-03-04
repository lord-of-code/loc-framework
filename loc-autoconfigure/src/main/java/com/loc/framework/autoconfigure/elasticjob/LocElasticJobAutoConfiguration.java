package com.loc.framework.autoconfigure.elasticjob;

import com.dangdang.ddframe.job.api.ElasticJob;
import com.dangdang.ddframe.job.api.dataflow.DataflowJob;
import com.dangdang.ddframe.job.api.script.ScriptJob;
import com.dangdang.ddframe.job.api.simple.SimpleJob;
import com.dangdang.ddframe.job.config.JobCoreConfiguration;
import com.dangdang.ddframe.job.config.dataflow.DataflowJobConfiguration;
import com.dangdang.ddframe.job.config.script.ScriptJobConfiguration;
import com.dangdang.ddframe.job.config.simple.SimpleJobConfiguration;
import com.dangdang.ddframe.job.event.JobEventConfiguration;
import com.dangdang.ddframe.job.event.rdb.JobEventRdbConfiguration;
import com.dangdang.ddframe.job.lite.api.listener.ElasticJobListener;
import com.dangdang.ddframe.job.lite.config.LiteJobConfiguration;
import com.dangdang.ddframe.job.lite.spring.api.SpringJobScheduler;
import com.dangdang.ddframe.job.reg.zookeeper.ZookeeperConfiguration;
import com.dangdang.ddframe.job.reg.zookeeper.ZookeeperRegistryCenter;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.base.Throwables;
import com.google.common.collect.Lists;
import com.loc.framework.autoconfigure.ConditionalOnPrefixProperty;
import com.loc.framework.autoconfigure.jdbc.LocDataSourceAutoConfiguration;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import javax.sql.DataSource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.FatalBeanException;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.lang.Nullable;

/**
 * Created on 2018/3/4.
 */
@Configuration
@ConditionalOnPrefixProperty(prefix = "loc.elasticjob", value = LocElasticJobProperties.class)
@ConditionalOnClass({ZookeeperRegistryCenter.class, LiteJobConfiguration.class})
@AutoConfigureAfter(LocDataSourceAutoConfiguration.class)
@Slf4j
public class LocElasticJobAutoConfiguration implements
    ApplicationContextAware, EnvironmentAware {

  private ApplicationContext applicationContext;

  private ConfigurableEnvironment environment;

  @Override
  public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
    this.applicationContext = applicationContext;

    LocElasticJobProperties elasticJobProperties = resolverJobProperties();

    String[] jobs = this.applicationContext.getBeanNamesForAnnotation(LocElasticJob.class);
    ZookeeperRegistryCenter registryCenter = registerCenter(elasticJobProperties);
    JobEventConfiguration jobEventConfiguration = Optional
        .ofNullable(Strings.emptyToNull(elasticJobProperties.getDataSource())).map(
            s -> new JobEventRdbConfiguration(
                applicationContext.getBean(s + "Ds", DataSource.class))
        ).orElse(null);
    createBean(registryCenter, jobEventConfiguration, jobs);
  }

  private void createBean(ZookeeperRegistryCenter registryCenter,
     @Nullable JobEventConfiguration jobEventConfiguration, String[] jobs) {
    if (ArrayUtils.isNotEmpty(jobs)) {
      Arrays.stream(jobs).forEach(job -> {
        try {
          ElasticJob elasticJob = this.applicationContext.getBean(job, ElasticJob.class);
          LocElasticJob locElasticJob = elasticJob.getClass().getAnnotation(LocElasticJob.class);
          LiteJobConfiguration liteJobConfiguration;
          if (elasticJob instanceof SimpleJob) {
            liteJobConfiguration = createSimpleJobLiteJobConfiguration(elasticJob, locElasticJob);
          } else if (elasticJob instanceof DataflowJob) {
            liteJobConfiguration = createDataFlowJobConfiguration(elasticJob, locElasticJob);
          } else if (elasticJob instanceof ScriptJob) {
            liteJobConfiguration = createScriptJobConfiguration(elasticJob, locElasticJob);
          } else {
            throw new IllegalArgumentException("error elasticJob type");
          }
          Preconditions.checkNotNull(liteJobConfiguration, "liteJobConfiguration不能为空");
          ElasticJobListener[] elasticJobListeners = getElasticJobListeners(
              locElasticJob.elasticJobListeners());
          List<Object> argList = Lists.newArrayList();
          if (elasticJob instanceof ScriptJob) {
            argList.add(null);
          } else {
            argList.add(elasticJob);
          }
          argList.add(registryCenter);
          argList.add(liteJobConfiguration);
          Optional.ofNullable(jobEventConfiguration).ifPresent(argList::add);
          argList.add(elasticJobListeners);
          createSpringJobScheduler(elasticJob.getClass().getSimpleName() + "jobScheduler", argList);
        } catch (Exception e) {
          throw Throwables.propagate(e);
        }
      });
    }
  }

  private void createSpringJobScheduler(String name, List<Object> argList) {
    ConfigurableApplicationContext configurableApplicationContext = (ConfigurableApplicationContext) applicationContext;
    BeanDefinitionRegistry beanDefinitionRegistry = (BeanDefinitionRegistry) configurableApplicationContext
        .getBeanFactory();
    BeanDefinitionBuilder builder = BeanDefinitionBuilder
        .rootBeanDefinition(SpringJobScheduler.class);
    builder.setInitMethodName("init");
    for (Object arg : argList) {
      builder.addConstructorArgValue(arg);
    }
    beanDefinitionRegistry.registerBeanDefinition(name, builder.getBeanDefinition());
    this.applicationContext.getBean(name, SpringJobScheduler.class);
    log.info("spring bean name {} register success ", name);
  }

  private ElasticJobListener[] getElasticJobListeners(
      Class<? extends ElasticJobListener>[] classes) {
    ElasticJobListener[] elasticJobListeners = new ElasticJobListener[]{};
    if (ArrayUtils.isNotEmpty(classes)) {
      for (Class<? extends ElasticJobListener> clazz : classes) {
        elasticJobListeners = ArrayUtils.add(elasticJobListeners, this.applicationContext.getBean(clazz));
      }
    }
    return elasticJobListeners;
  }

  private LiteJobConfiguration createSimpleJobLiteJobConfiguration(ElasticJob elasticJob,
      LocElasticJob locElasticJob) {
    return LiteJobConfiguration
        .newBuilder(new SimpleJobConfiguration(
            JobCoreConfiguration
                .newBuilder(elasticJob.getClass().getName(), cronValue(locElasticJob.cron()),
                    locElasticJob.shardingTotalCount())
                .shardingItemParameters(Strings.emptyToNull(locElasticJob.shardingItemParameters()))
                .build(),
            elasticJob.getClass().getCanonicalName()
        )).overwrite(locElasticJob.overwrite()).disabled(locElasticJob.disabled())
        .jobShardingStrategyClass(Strings.emptyToNull(locElasticJob.jobShardingStrategyClass()))
        .build();
  }

  private LiteJobConfiguration createDataFlowJobConfiguration(ElasticJob elasticJob,
      LocElasticJob locElasticJob) {
    return LiteJobConfiguration
        .newBuilder(new DataflowJobConfiguration(JobCoreConfiguration.newBuilder(
            elasticJob.getClass().getName(), cronValue(locElasticJob.cron()),
            locElasticJob.shardingTotalCount())
            .shardingItemParameters(Strings.emptyToNull(locElasticJob.shardingItemParameters()))
            .build(),
            elasticJob.getClass().getCanonicalName(), locElasticJob.streamingProcess()))
        .overwrite(locElasticJob.overwrite()).disabled(locElasticJob.disabled())
        .jobShardingStrategyClass(Strings.emptyToNull(locElasticJob.jobShardingStrategyClass()))
        .build();
  }

  private LiteJobConfiguration createScriptJobConfiguration(ElasticJob elasticJob,
      LocElasticJob locElasticJob) {
    return LiteJobConfiguration
        .newBuilder(new ScriptJobConfiguration(JobCoreConfiguration.newBuilder(
            elasticJob.getClass().getName(), cronValue(locElasticJob.cron()),
            locElasticJob.shardingTotalCount())
            .shardingItemParameters(Strings.emptyToNull(locElasticJob.shardingItemParameters()))
            .build(), locElasticJob.scriptCommandLine()))
        .overwrite(locElasticJob.overwrite()).disabled(locElasticJob.disabled())
        .jobShardingStrategyClass(Strings.emptyToNull(locElasticJob.jobShardingStrategyClass()))
        .build();
  }

  private String cronValue(String cron) {
    if (cron.startsWith("${") && cron.endsWith("}")) {
      return this.environment
          .getProperty(StringUtils.removeEnd(StringUtils.removeStart(cron, "${"), "}"), "");
    }
    return cron;
  }

  private ZookeeperRegistryCenter registerCenter(LocElasticJobProperties elasticJobProperties) {
    ZookeeperConfiguration zookeeperConfiguration = new ZookeeperConfiguration(
        elasticJobProperties.getServerList(), elasticJobProperties.getNamespace());
    BeanUtils
        .copyProperties(elasticJobProperties, zookeeperConfiguration, "serverLists", "namespace");
    ZookeeperRegistryCenter registryCenter = new ZookeeperRegistryCenter(zookeeperConfiguration);
    registryCenter.init();
    return registryCenter;
  }

  @Override
  public void setEnvironment(Environment environment) {
    this.environment = (ConfigurableEnvironment) environment;
  }

  private LocElasticJobProperties resolverJobProperties() {
    try {
      return Binder.get(environment)
          .bind("loc.elasticjob", Bindable.of(LocElasticJobProperties.class))
          .orElseThrow(
              () -> new FatalBeanException("Could not bind DataSourceSettings properties"));
    } catch (Exception e) {
      //ignore
      log.error("Could not bind job properties, error message is {}", e.getMessage(), e);
      throw new FatalBeanException("Could not bind job properties", e);
    }
  }
}
