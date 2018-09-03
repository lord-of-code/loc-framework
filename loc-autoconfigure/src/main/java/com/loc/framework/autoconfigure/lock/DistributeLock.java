package com.loc.framework.autoconfigure.lock;

import static java.lang.System.currentTimeMillis;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;

@Slf4j
@AllArgsConstructor
public class DistributeLock<T> {

  private StringRedisTemplate stringRedisTemplate;
  // 锁名称
  private final String lockName;
  // 锁的过期时间(单位为秒)
  private final long expireTime;
  // 锁占用失败等待时间
  private final int waitInterval;
  // 锁占用失败超时时间
  private final int waitTimeout;

  private final boolean returnOnError;

  public T lock(Supplier<T> redisLoader) {
    String uuid = UUID.randomUUID().toString();
    long startTime = currentTimeMillis();
    synchronized (lockName) {
      try {
        while (true) {
          if (currentTimeMillis() > startTime + waitTimeout) {
            throw new RuntimeException("操作超时");
          }
          boolean locked = tryLock(uuid);
          if (locked) {
            return redisLoader.get();
          }
          try {
            Thread.sleep(waitInterval);
          } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
          }
        }
      } finally {
        unLock(uuid);
        log.debug("lock info. name: " + lockName + " time: " + (currentTimeMillis() - startTime) + " ms");
      }
    }
  }

  private boolean tryLock(String uuid) {
    try {
      if(Optional.ofNullable(stringRedisTemplate.opsForValue().setIfAbsent(lockName, uuid)).orElse(false)) {
        stringRedisTemplate.expire(lockName, expireTime, TimeUnit.SECONDS);
        return true;
      }
      return false;
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      return returnOnError;
    }
  }

  private void unLock(String uuid) {
    try {
      if (uuid.equals(stringRedisTemplate.opsForValue().get(lockName))) {
        stringRedisTemplate.delete(lockName);
      }
    } catch (Exception e) {
      log.error(e.getMessage(), e);
    }
  }
}
