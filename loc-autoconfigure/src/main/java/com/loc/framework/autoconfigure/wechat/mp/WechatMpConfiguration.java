package com.loc.framework.autoconfigure.wechat.mp;

import com.loc.framework.autoconfigure.redis.RedisCacheAutoConfiguration;
import com.loc.framework.autoconfigure.wechat.WxMpInRedisTemplateConfigStorage;
import me.chanjar.weixin.mp.api.WxMpConfigStorage;
import me.chanjar.weixin.mp.api.WxMpInMemoryConfigStorage;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.api.impl.WxMpServiceOkHttpImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;

/**
 * Created on 2018/5/7.
 */
@Configuration
@ConditionalOnClass(WxMpService.class)
@EnableConfigurationProperties(WechatMpProperties.class)
@ConditionalOnProperty(value = "loc.wechat.mp.enabled", matchIfMissing = true)
public class WechatMpConfiguration {

  @Configuration
  @ConditionalOnClass(StringRedisTemplate.class)
  @AutoConfigureAfter(RedisCacheAutoConfiguration.class)
  public class RedisWechatMpConfiguration {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private WechatMpProperties properties;

    @Bean
    @ConditionalOnMissingBean
    public WxMpConfigStorage configStorage() {
      WxMpInRedisTemplateConfigStorage configStorage = new WxMpInRedisTemplateConfigStorage(
          redisTemplate);
      configStorage.setAppId(this.properties.getAppId());
      configStorage.setSecret(this.properties.getSecret());
      configStorage.setToken(this.properties.getToken());
      configStorage.setAesKey(this.properties.getAesKey());
      return configStorage;
    }

    @Bean
    @ConditionalOnMissingBean
    public WxMpService wxMpService(WxMpConfigStorage configStorage) {
      WxMpService wxMpService = new WxMpServiceOkHttpImpl();
      wxMpService.setWxMpConfigStorage(configStorage);
      return wxMpService;
    }
  }

  @Configuration
  @ConditionalOnMissingClass("org.springframework.data.redis.core.StringRedisTemplate")
  public class MemoryWechatMpConfiguration {

    @Autowired
    private WechatMpProperties properties;

    @Bean
    @ConditionalOnMissingBean
    public WxMpConfigStorage configStorage() {
      WxMpInMemoryConfigStorage configStorage = new WxMpInMemoryConfigStorage();
      configStorage.setAppId(this.properties.getAppId());
      configStorage.setSecret(this.properties.getSecret());
      configStorage.setToken(this.properties.getToken());
      configStorage.setAesKey(this.properties.getAesKey());
      return configStorage;
    }

    @Bean
    @ConditionalOnMissingBean
    public WxMpService wxMpService(WxMpConfigStorage configStorage) {
      WxMpService wxMpService = new WxMpServiceOkHttpImpl();
      wxMpService.setWxMpConfigStorage(configStorage);
      return wxMpService;
    }
  }

}
