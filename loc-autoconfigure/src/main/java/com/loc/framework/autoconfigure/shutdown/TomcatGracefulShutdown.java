package com.loc.framework.autoconfigure.shutdown;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.connector.Connector;
import org.apache.tomcat.util.threads.ThreadPoolExecutor;
import org.springframework.boot.web.embedded.tomcat.TomcatConnectorCustomizer;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;


@Slf4j
public class TomcatGracefulShutdown implements TomcatConnectorCustomizer,
    ApplicationListener<ContextClosedEvent> {


  private volatile Connector connector;


  private final TomcatGracefulShutdownProperties tomcatGracefulShutdownProperties;


  public TomcatGracefulShutdown(TomcatGracefulShutdownProperties tomcatGracefulShutdownProperties) {
    this.tomcatGracefulShutdownProperties = tomcatGracefulShutdownProperties;
  }

  @Override
  public void customize(Connector connector) {
    this.connector = connector;
  }

  @Override
  public void onApplicationEvent(final ContextClosedEvent event) {
    LocalDateTime startShutdown = LocalDateTime.now();
    LocalDateTime stopShutdown = LocalDateTime.now();
    try {
      log.info(
          "We are now in down mode, please wait " + tomcatGracefulShutdownProperties.getWaitTime()
              + " second(s)...");

      if (connector == null) {
        log.info("We are running unit test ... ");
        return;
      }
      connector.pause();

      final Executor executor = connector.getProtocolHandler().getExecutor();
      if (executor instanceof ThreadPoolExecutor) {
        log.info("executor is  ThreadPoolExecutor");
        final ThreadPoolExecutor threadPoolExecutor = (ThreadPoolExecutor) executor;
        threadPoolExecutor.shutdown();
        if (!threadPoolExecutor
            .awaitTermination(tomcatGracefulShutdownProperties.getWaitTime(), TimeUnit.SECONDS)) {
          log.warn("Tomcat thread pool did not shut down gracefully within "
              + tomcatGracefulShutdownProperties
              .getWaitTime() + " second(s). Proceeding with force shutdown");
        } else {
          log.debug("Tomcat thread pool is empty, we stop now");
        }
      }
      stopShutdown = LocalDateTime.now();
    } catch (final InterruptedException ex) {
      log.error("The await termination has been interrupted : " + ex.getMessage());
      Thread.currentThread().interrupt();
    } finally {
      final long seconds = Duration.between(startShutdown, stopShutdown).getSeconds();
      log.info("Shutdown performed in " + seconds + " second(s)");
    }
  }
}
