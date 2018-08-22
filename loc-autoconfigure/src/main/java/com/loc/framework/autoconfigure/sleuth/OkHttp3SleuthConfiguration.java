package com.loc.framework.autoconfigure.sleuth;

import brave.http.HttpTracing;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import okhttp3.OkHttpClient;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author dragon
 */
@Configuration
@ConditionalOnClass(value = {OkHttpClient.class}, name = {"brave.http.HttpTracing"})
@ConditionalOnProperty(value = {"spring.sleuth.enabled"}, matchIfMissing = true)
@AutoConfigureAfter(name = "org.springframework.cloud.sleuth.autoconfig.TraceAutoConfiguration")
@AutoConfigureBefore(name = "com.loc.framework.autoconfigure.okhttp.OkHttpAutoConfiguration")
public class OkHttp3SleuthConfiguration {

  @Bean
  public OkHttpSleuth OkHttpSleuth(HttpTracing httpTracing) {
    return new OkHttpSleuth(httpTracing);
  }

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  public static class OkHttpSleuth {
    private HttpTracing httpTracing;
  }

}
