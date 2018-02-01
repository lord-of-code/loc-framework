package com.loc.framework.autoconfigure.redis;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Duration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.cache.CacheProperties.Redis;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.cache.RedisCacheWriter;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext.SerializationPair;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.lang.Nullable;

/**
 * Created on 2018/2/1.
 */
@Slf4j
@Configuration
@ConditionalOnClass(Redis.class)
public class RedisCacheAutoConfiguration extends CachingConfigurerSupport {

  @Bean
  public RedisCacheConfiguration redisCacheConfiguration() {
    Jackson2JsonRedisSerializer<Object> jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer<>(
        Object.class);

    ObjectMapper om = new ObjectMapper();
    om.setVisibility(PropertyAccessor.GETTER, JsonAutoDetect.Visibility.ANY);
    om.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
    om.setSerializationInclusion(Include.NON_NULL);
    om.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    jackson2JsonRedisSerializer.setObjectMapper(om);

    return RedisCacheConfiguration.defaultCacheConfig()
        .serializeKeysWith(SerializationPair.fromSerializer(new StringRedisSerializer()))
        .serializeValuesWith(SerializationPair.fromSerializer(jackson2JsonRedisSerializer));
  }

  @Override
  @Bean
  public CacheManager cacheManager(RedisConnectionFactory redisConnectionFactory) {

    return RedisCacheManager.RedisCacheManagerBuilder.fromCacheWriter(new RedisCacheWriter(delegate) {

      private RedisCacheManager delegate;

      public RedisCacheManager

      @Override
      public void put(String s, byte[] bytes, byte[] bytes1, @Nullable Duration duration) {

      }

      @Nullable
      @Override
      public byte[] get(String s, byte[] bytes) {
        return new byte[0];
      }

      @Nullable
      @Override
      public byte[] putIfAbsent(String s, byte[] bytes, byte[] bytes1,
          @Nullable Duration duration) {
        return new byte[0];
      }

      @Override
      public void remove(String s, byte[] bytes) {

      }

      @Override
      public void clean(String s, byte[] bytes) {

      }
    }).build();
  }

}
