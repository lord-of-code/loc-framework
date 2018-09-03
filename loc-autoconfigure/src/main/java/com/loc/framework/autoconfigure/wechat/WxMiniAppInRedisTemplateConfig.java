package com.loc.framework.autoconfigure.wechat;

import cn.binarywang.wx.miniapp.config.WxMaInMemoryConfig;
import com.loc.framework.autoconfigure.lock.DistributeLock;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import org.springframework.data.redis.core.StringRedisTemplate;

/**
 * Created on 2018/5/7.
 */
public class WxMiniAppInRedisTemplateConfig extends WxMaInMemoryConfig {

  private final static String ACCESS_TOKEN_KEY = "miniapp_access_token_";

  private final static int EXPIRE_TIME_SECOND = 1;
  private final static int WAIT_INTERVAL_MILLISECONDS = 100;
  private final static int WAIT_TIMEOUT_MILLISECONDS = 300;


  /**
   * 使用连接池保证线程安全
   */
  protected final StringRedisTemplate redisTemplate;

  private String accessTokenKey;

  public WxMiniAppInRedisTemplateConfig(StringRedisTemplate redisTemplate) {
    this.redisTemplate = redisTemplate;
  }

  /**
   * 每个公众号生成独有的存储key
   */
  @Override
  public void setAppid(String appId) {
    super.setAppid(appId);
    this.accessTokenKey = ACCESS_TOKEN_KEY.concat(appId);
  }

  @Override
  public String getAccessToken() {
    return redisTemplate.opsForValue().get(this.accessTokenKey);
  }

  @Override
  public boolean isAccessTokenExpired() {
    return Optional.ofNullable(redisTemplate.getExpire(accessTokenKey, TimeUnit.MILLISECONDS))
        .orElse(0L) < 2;
  }

  @Override
  public synchronized void updateAccessToken(String accessToken, int expiresInSeconds) {
    DistributeLock lock = new DistributeLock(redisTemplate, "miniAccessTokenLock", EXPIRE_TIME_SECOND,
        WAIT_INTERVAL_MILLISECONDS, WAIT_TIMEOUT_MILLISECONDS, false);
    lock.lock(() -> {
      redisTemplate.opsForValue()
          .set(this.accessTokenKey, accessToken, expiresInSeconds - 200, TimeUnit.SECONDS);
      return true;
    });
  }

  @Override
  public void expireAccessToken() {
    redisTemplate.expire(this.accessTokenKey, 0, TimeUnit.SECONDS);
  }
}
