package com.loc.framework.autoconfigure.redis;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.LinkedHashSet;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.cache.CacheProperties;
import org.springframework.boot.autoconfigure.cache.CacheProperties.Redis;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.cache.RedisCacheWriter;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext.SerializationPair;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * Created on 2018/2/1.
 */
@Slf4j
@EnableCaching
@Configuration
@AutoConfigureAfter(RedisAutoConfiguration.class)
@EnableConfigurationProperties(CacheProperties.class)
@ConditionalOnClass(Redis.class)
public class RedisCacheAutoConfiguration {

  private final CacheProperties cacheProperties;

  public RedisCacheAutoConfiguration(CacheProperties cacheProperties) {
    this.cacheProperties = cacheProperties;
  }

  @Bean
  public RedisCacheManager getRedisCacheManager(RedisConnectionFactory redisConnectionFactory) {
    CustomRedisCacheWriter customRedisCacheWriter = new CustomRedisCacheWriter(
        RedisCacheWriter.lockingRedisCacheWriter(redisConnectionFactory));

    RedisCacheManager.RedisCacheManagerBuilder builder = RedisCacheManager.RedisCacheManagerBuilder
        .fromCacheWriter(customRedisCacheWriter).cacheDefaults(determineConfiguration());

    List<String> cacheNames = this.cacheProperties.getCacheNames();
    if (!cacheNames.isEmpty()) {
      builder.initialCacheNames(new LinkedHashSet<>(cacheNames));
    }
    return builder.build();
  }

  private org.springframework.data.redis.cache.RedisCacheConfiguration determineConfiguration() {
    Redis redisProperties = this.cacheProperties.getRedis();
    org.springframework.data.redis.cache.RedisCacheConfiguration config = org.springframework.data.redis.cache.RedisCacheConfiguration
        .defaultCacheConfig();

    Jackson2JsonRedisSerializer<Object> jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer<>(
        Object.class);

    ObjectMapper om = new ObjectMapper();
    om.setVisibility(PropertyAccessor.GETTER, JsonAutoDetect.Visibility.ANY);
    om.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
    om.setSerializationInclusion(Include.NON_NULL);
    om.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    jackson2JsonRedisSerializer.setObjectMapper(om);
    config = config.serializeKeysWith(SerializationPair.fromSerializer(new StringRedisSerializer()));
    config = config.serializeValuesWith(SerializationPair.fromSerializer(jackson2JsonRedisSerializer));


    if (redisProperties.getTimeToLive() != null) {
      config = config.entryTtl(redisProperties.getTimeToLive());
    }
    if (redisProperties.getKeyPrefix() != null) {
      config = config.prefixKeysWith(redisProperties.getKeyPrefix());
    }
    if (!redisProperties.isCacheNullValues()) {
      config = config.disableCachingNullValues();
    }
    if (!redisProperties.isUseKeyPrefix()) {
      config = config.disableKeyPrefix();
    }
    return config;
  }
}
