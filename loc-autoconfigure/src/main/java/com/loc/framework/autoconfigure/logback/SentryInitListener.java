package com.loc.framework.autoconfigure.logback;

import com.google.common.base.Strings;
import com.loc.framework.autoconfigure.utils.HostUtil;
import com.loc.framework.autoconfigure.utils.LocConstants;
import io.sentry.Sentry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.event.ApplicationPreparedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.env.ConfigurableEnvironment;

/**
 * Created on 2018/3/20.
 */
@Slf4j
@ConditionalOnClass(Sentry.class)
public class SentryInitListener implements ApplicationListener<ApplicationPreparedEvent> {

  private static String SENTRY_DSN = null;

  @Override
  public void onApplicationEvent(ApplicationPreparedEvent event) {
    if (SENTRY_DSN != null) {
      return;
    }
    ConfigurableEnvironment configurableEnvironment = event.getApplicationContext()
        .getEnvironment();
    SENTRY_DSN = configurableEnvironment.getProperty("sentry.dsn");
    if (Strings.isNullOrEmpty(SENTRY_DSN)) {
      return;
    }
    String activeProfile = configurableEnvironment.getProperty("spring.profiles.active", "local");
    if (activeProfile.equals(LocConstants.TEST_PROFILE) || activeProfile
        .equals(LocConstants.PRO_PROFILE)) {
      String appName = configurableEnvironment.getProperty("spring.application.name", "unknown");
      SENTRY_DSN =
          SENTRY_DSN + "?environment=" + activeProfile + "&servername=" + appName + "&extra=hostIp:"
              + HostUtil.HOST_IP;
      Sentry.init(SENTRY_DSN);
      log.info("hostIp is {}, active profile is {}, init sentry success, sentry dsn is {}",
          HostUtil.HOST_IP, activeProfile, SENTRY_DSN);
    } else {
      log.info("hostIp is {}, active profile is {}, not init sentry", HostUtil.HOST_IP,
          activeProfile);
    }
  }
}
