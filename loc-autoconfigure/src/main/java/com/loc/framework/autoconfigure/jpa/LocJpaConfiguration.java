package com.loc.framework.autoconfigure.jpa;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.hibernate.boot.model.naming.ImplicitNamingStrategy;
import org.hibernate.boot.model.naming.PhysicalNamingStrategy;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.orm.jpa.HibernatePropertiesCustomizer;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateSettings;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.persistenceunit.PersistenceUnitManager;
import org.springframework.orm.jpa.vendor.AbstractJpaVendorAdapter;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

/**
 * Created on 2018/3/16.
 */

@EnableConfigurationProperties(JpaProperties.class)
public abstract class LocJpaConfiguration {

  @Autowired(required = false)
  private ImplicitNamingStrategy implicitNamingStrategy;

  @Autowired(required = false)
  private PhysicalNamingStrategy physicalNamingStrategy;

  @Autowired(required = false)
  private Collection<HibernatePropertiesCustomizer> hibernatePropertiesCustomizers;

  @Autowired
  private JpaProperties jpaProperties;

  @Bean
  public EntityManagerFactoryBuilder entityManagerFactoryBuilder(
      JpaVendorAdapter jpaVendorAdapter,
      ObjectProvider<PersistenceUnitManager> persistenceUnitManager) {
    return new EntityManagerFactoryBuilder(
        jpaVendorAdapter, jpaProperties.getProperties(),
        persistenceUnitManager.getIfAvailable());
  }

  @Bean
  public JpaVendorAdapter jpaVendorAdapter() {
    AbstractJpaVendorAdapter adapter = new HibernateJpaVendorAdapter();
    adapter.setShowSql(jpaProperties.isShowSql());
    adapter.setDatabase(Database.MYSQL);
    adapter.setDatabasePlatform(jpaProperties.getDatabasePlatform());
    adapter.setGenerateDdl(jpaProperties.isGenerateDdl());
    return adapter;
  }

  protected Map<String, Object> getVendorProperties() {
    String defaultDdlMode = "none";
    LinkedList<HibernatePropertiesCustomizer> customizers = new LinkedList<>();
    if(hibernatePropertiesCustomizers != null) {
      customizers.addAll(hibernatePropertiesCustomizers);
    }
    customizers.addFirst(new LocJpaConfiguration.NamingStrategiesHibernatePropertiesCustomizer(physicalNamingStrategy, implicitNamingStrategy));
    return new LinkedHashMap<>(jpaProperties
        .getHibernateProperties(new HibernateSettings().ddlAuto(() -> defaultDdlMode)
            .hibernatePropertiesCustomizers(customizers)));
  }

  protected String[] getMappingResources() {
    List<String> mappingResources = jpaProperties.getMappingResources();
    return (!ObjectUtils.isEmpty(mappingResources)
        ? StringUtils.toStringArray(mappingResources) : null);
  }

  private static class NamingStrategiesHibernatePropertiesCustomizer implements
      HibernatePropertiesCustomizer {
    private final PhysicalNamingStrategy physicalNamingStrategy;
    private final ImplicitNamingStrategy implicitNamingStrategy;

    NamingStrategiesHibernatePropertiesCustomizer(PhysicalNamingStrategy physicalNamingStrategy, ImplicitNamingStrategy implicitNamingStrategy) {
      this.physicalNamingStrategy = physicalNamingStrategy;
      this.implicitNamingStrategy = implicitNamingStrategy;
    }

    public void customize(Map<String, Object> hibernateProperties) {
      if (this.physicalNamingStrategy != null) {
        hibernateProperties.put("hibernate.physical_naming_strategy", this.physicalNamingStrategy);
      }

      if (this.implicitNamingStrategy != null) {
        hibernateProperties.put("hibernate.implicit_naming_strategy", this.implicitNamingStrategy);
      }

    }
  }
}
