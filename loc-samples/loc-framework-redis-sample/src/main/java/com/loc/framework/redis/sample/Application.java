package com.loc.framework.redis.sample;

import com.loc.framework.redis.sample.service.RedisService;
import com.loc.framework.redis.sample.service.RedisService.RedisDemoInfo;
import java.nio.charset.StandardCharsets;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.zalando.problem.Problem;
import org.zalando.problem.Status;

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
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private RedisService redisService;

    @GetMapping(value = "/getRedisCluster")
    public Problem getRedisCluster() {
      byte[] bytes = redisConnectionFactory.getClusterConnection().stringCommands()
          .get("key".getBytes());
      if (bytes != null) {
        String value = new String(bytes, StandardCharsets.UTF_8);
        String stringValue = stringRedisTemplate.opsForValue().get("stringKey");
        return Problem.builder().with("data", value + "_" + stringValue).build();
      } else {
        return Problem.valueOf(Status.BAD_REQUEST);
      }
    }

    @PostMapping(value = "/setRedisCluster")
    public Problem setRedisCluster() {
      redisConnectionFactory.getClusterConnection().stringCommands()
          .set("key".getBytes(), "value".getBytes());
      stringRedisTemplate.opsForValue().set("stringKey", "stringValue");
      return Problem.builder().with("data", "success").build();
    }

    @GetMapping(value = "/getRedis")
    public Problem getRedis() {
      redisConnectionFactory.getConnection().getNativeConnection();
      byte[] bytes = redisConnectionFactory.getConnection().stringCommands().get("key".getBytes());
      if (bytes != null) {
        String value = new String(bytes, StandardCharsets.UTF_8);
        String stringValue = stringRedisTemplate.opsForValue().get("stringKey");
        return Problem.builder().with("data", value + "_" + stringValue).build();
      } else {
        return Problem.valueOf(Status.BAD_REQUEST);
      }
    }

    @PostMapping(value = "/setRedis")
    public Problem setRedis() {
      redisConnectionFactory.getConnection().stringCommands()
          .set("key".getBytes(), "value".getBytes());
      stringRedisTemplate.opsForValue().set("stringKey", "stringValue");
      return Problem.builder().with("data", "success").build();
    }

    @GetMapping(value = "/getRedisCache")
    public Problem getRedisCache() {
      RedisDemoInfo redisDemoInfo = redisService.getDemoInfoLocKey("loc", 30);
      log.info("redis demo info is {}", redisDemoInfo);
      return Problem.builder().with("data", redisDemoInfo).build();
    }
  }

}
