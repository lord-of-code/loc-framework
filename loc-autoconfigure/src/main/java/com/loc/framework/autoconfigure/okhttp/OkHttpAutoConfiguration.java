package com.loc.framework.autoconfigure.okhttp;

import brave.okhttp3.TracingInterceptor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.loc.framework.autoconfigure.okhttp.OkHttpClientProperties.Connection;
import com.loc.framework.autoconfigure.sleuth.OkHttp3SleuthConfiguration;
import java.util.concurrent.TimeUnit;
import javax.annotation.Nullable;
import lombok.extern.slf4j.Slf4j;
import okhttp3.ConnectionPool;
import okhttp3.Dispatcher;
import okhttp3.OkHttpClient;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.zalando.logbook.Logbook;
import org.zalando.logbook.okhttp.LogbookInterceptor;

/**
 * Created on 2018/1/4.
 */
@ConditionalOnClass(OkHttpClient.class)
@Configuration
@EnableConfigurationProperties(OkHttpClientProperties.class)
@Slf4j
@AutoConfigureAfter(name = {
    "org.springframework.cloud.netflix.ribbon.okhttp.OkHttpRibbonConfiguration",
    "org.springframework.cloud.sleuth.autoconfig.TraceAutoConfiguration"
})
public class OkHttpAutoConfiguration {

  private final Logbook logbook;

  public OkHttpAutoConfiguration(Logbook logbook) {
    this.logbook = logbook;
  }

  private OkHttpClient.Builder createBuilder(OkHttpClientProperties okHttpClientProperties,
      ConnectionPool connectionPool, @Nullable OkHttp3SleuthConfiguration.OkHttpSleuth okHttpSleuth) {
    OkHttpClient.Builder builder = new okhttp3.OkHttpClient.Builder()
        .readTimeout(okHttpClientProperties.getReadTimeout(), TimeUnit.MILLISECONDS)
        .connectTimeout(okHttpClientProperties.getConnectTimeout(), TimeUnit.MILLISECONDS)
        .writeTimeout(okHttpClientProperties.getWriteTimeout(), TimeUnit.MILLISECONDS)
        .connectionPool(connectionPool).followRedirects(okHttpClientProperties.isFollowRedirects())
        .retryOnConnectionFailure(okHttpClientProperties.isRetryOnConnectionFailure())
        .addNetworkInterceptor(new LogbookInterceptor(logbook));

    if (okHttpSleuth != null) {
      builder.dispatcher(new Dispatcher(
          okHttpSleuth.getHttpTracing().tracing().currentTraceContext()
              .executorService(new Dispatcher().executorService())
      )).addNetworkInterceptor(TracingInterceptor.create(okHttpSleuth.getHttpTracing()));
    }
    return builder;
  }


  @Bean
  @ConditionalOnMissingBean
  public ConnectionPool connectionPool(OkHttpClientProperties okHttpClientProperties) {
    Connection connection = okHttpClientProperties.getConnection();
    return new ConnectionPool(connection.getMaxIdleConnections(),
        connection.getKeepAliveDuration(), TimeUnit.MILLISECONDS);
  }


  @Bean("locOkHttpClient")
  @ConditionalOnBean(ObjectMapper.class)
  public LocOkHttpClient hnOkHttpClient(OkHttpClientProperties okHttpClientProperties,
      ConnectionPool connectionPool, ObjectMapper objectMapper,
      ObjectProvider<OkHttp3SleuthConfiguration.OkHttpSleuth> okHttpSleuthObjectProvider) {
    return new LocOkHttpClient(createBuilder(okHttpClientProperties, connectionPool,
        okHttpSleuthObjectProvider.getIfAvailable()).build(), objectMapper);
  }
}
