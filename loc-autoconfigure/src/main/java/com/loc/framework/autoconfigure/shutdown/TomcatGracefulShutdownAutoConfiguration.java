package com.loc.framework.autoconfigure.shutdown;

import javax.servlet.Servlet;
import org.apache.catalina.startup.Tomcat;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
@ConditionalOnProperty(value = "loc.tomcat.shutdown.enabled", matchIfMissing = true)
@EnableConfigurationProperties(TomcatGracefulShutdownProperties.class)
@ConditionalOnBean(TomcatServletWebServerFactory.class)
@ConditionalOnClass({Servlet.class, Tomcat.class})
public class TomcatGracefulShutdownAutoConfiguration {


  private TomcatGracefulShutdownProperties tomcatGracefulShutdownProperties;

  public TomcatGracefulShutdownAutoConfiguration(
      TomcatGracefulShutdownProperties tomcatGracefulShutdownProperties) {
    this.tomcatGracefulShutdownProperties = tomcatGracefulShutdownProperties;
  }

  @Bean
  public TomcatGracefulShutdown tomcatGracefulShutdown() {
    return new TomcatGracefulShutdown(tomcatGracefulShutdownProperties);
  }

  @Bean
  public WebServerFactoryCustomizer tomcatFactoryCustomizer() {
    return server -> {
      if (server instanceof TomcatServletWebServerFactory) {
        ((TomcatServletWebServerFactory) server).addConnectorCustomizers(tomcatGracefulShutdown());
      }
    };
  }


}
