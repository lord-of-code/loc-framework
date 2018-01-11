package com.loc.framework.redis.sample;

import com.loc.framework.autoconfigure.springmvc.BasicResult;
import com.loc.framework.redis.sample.service.RedisService;
import com.loc.framework.redis.sample.service.RedisService.RedisDemoInfo;
import java.nio.charset.StandardCharsets;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created on 2017/12/29.
 */
@SpringBootApplication
public class Application {

  public static void main(String[] args) {
    SpringApplication.run(Application.class, args);
  }

  @Slf4j
  @RestController
  public static class RedisController {

    @Autowired
    private RedisConnectionFactory redisConnectionFactory;

    @Autowired
    private RedisService redisService;

    @GetMapping(value = "/getRedisCluster")
    public BasicResult getRedisCluster() {
      byte[] bytes = redisConnectionFactory.getClusterConnection().stringCommands().get("key".getBytes());
      if(bytes != null) {
        String value = new String(bytes, StandardCharsets.UTF_8);
        return BasicResult.success(value);
      } else {
        return BasicResult.fail(10000, "value is null");
      }
    }

    @PostMapping(value = "/setRedisCluster")
    public BasicResult setRedisCluster() {
      redisConnectionFactory.getClusterConnection().stringCommands().set("key".getBytes(), "value".getBytes());
      return BasicResult.success();
    }

    @GetMapping(value = "/getRedis")
    public BasicResult getRedis() {
      byte[] bytes = redisConnectionFactory.getConnection().stringCommands().get("key".getBytes());
      if(bytes != null) {
        String value = new String(bytes, StandardCharsets.UTF_8);
        return BasicResult.success(value);
      } else {
        return BasicResult.fail(10000, "value is null");
      }
    }

    @PostMapping(value = "/setRedis")
    public BasicResult setRedis() {
      redisConnectionFactory.getConnection().stringCommands().set("key".getBytes(), "value".getBytes());
      return BasicResult.success();
    }

    @GetMapping(value = "/getRedisCache")
    public BasicResult getRedisCache() {
      RedisDemoInfo redisDemoInfo = redisService.getDemoInfoLocKey("loc", 30);
      log.info("redis demo info is {}", redisDemoInfo);
      return BasicResult.success();
    }
  }

}
