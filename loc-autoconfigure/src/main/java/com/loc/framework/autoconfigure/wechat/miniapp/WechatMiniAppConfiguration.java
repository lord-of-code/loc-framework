package com.loc.framework.autoconfigure.wechat.miniapp;

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.api.impl.WxMaServiceImpl;
import cn.binarywang.wx.miniapp.config.WxMaConfig;
import cn.binarywang.wx.miniapp.config.WxMaInMemoryConfig;
import com.loc.framework.autoconfigure.redis.RedisCacheAutoConfiguration;
import com.loc.framework.autoconfigure.wechat.WxMiniAppInRedisTemplateConfig;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;


/**
 * Created on 2018/5/17.
 */
@Configuration
@ConditionalOnClass(WxMaService.class)
@EnableConfigurationProperties(WechatMiniAppProperties.class)
@ConditionalOnProperty(value = "loc.wechat.miniapp.enabled", matchIfMissing = true)
public class WechatMiniAppConfiguration {

  @ConditionalOnBean(StringRedisTemplate.class)
  @AutoConfigureAfter(RedisCacheAutoConfiguration.class)
  @Configuration
  static class RedisWechatMiniAppConfiguration {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private WechatMiniAppProperties properties;

    @Bean
    @ConditionalOnMissingBean
    public WxMaConfig maConfig() {
      WxMiniAppInRedisTemplateConfig config = new WxMiniAppInRedisTemplateConfig(redisTemplate);
      config.setAppid(this.properties.getAppId());
      config.setSecret(this.properties.getSecret());
      Optional.ofNullable(this.properties.getToken()).ifPresent(config::setToken);
      Optional.ofNullable(this.properties.getAesKey()).ifPresent(config::setAesKey);
      config.setMsgDataFormat(Optional.ofNullable(this.properties.getMsgDataFormat()).orElse("JSON"));
      return config;
    }

    @Bean
    @ConditionalOnMissingBean
    public WxMaService wxMaService(WxMaConfig maConfig) {
      WxMaService service = new WxMaServiceImpl();
      service.setWxMaConfig(maConfig);
      return service;
    }
  }

  @ConditionalOnMissingBean(StringRedisTemplate.class)
  @Configuration
  static class MemoryWechatMiniAppConfiguration {

    @Autowired
    private WechatMiniAppProperties properties;

    @Bean
    @ConditionalOnMissingBean
    public WxMaConfig maConfig() {
      WxMaInMemoryConfig config = new WxMaInMemoryConfig();
      config.setAppid(this.properties.getAppId());
      config.setSecret(this.properties.getSecret());
      Optional.ofNullable(this.properties.getToken()).ifPresent(config::setToken);
      Optional.ofNullable(this.properties.getAesKey()).ifPresent(config::setAesKey);
      config.setMsgDataFormat(Optional.ofNullable(this.properties.getMsgDataFormat()).orElse("JSON"));
      return config;
    }

    @Bean
    @ConditionalOnMissingBean
    public WxMaService wxMaService(WxMaConfig maConfig) {
      WxMaService service = new WxMaServiceImpl();
      service.setWxMaConfig(maConfig);
      return service;
    }
  }

}
