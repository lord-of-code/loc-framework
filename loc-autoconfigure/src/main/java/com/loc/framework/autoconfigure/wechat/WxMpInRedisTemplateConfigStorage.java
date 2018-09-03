package com.loc.framework.autoconfigure.wechat;

import com.loc.framework.autoconfigure.lock.DistributeLock;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import me.chanjar.weixin.mp.api.WxMpInMemoryConfigStorage;
import org.springframework.data.redis.core.StringRedisTemplate;

public class WxMpInRedisTemplateConfigStorage extends WxMpInMemoryConfigStorage {

  private final static String ACCESS_TOKEN_KEY = "wechat_access_token_";

  private final static String JSAPI_TICKET_KEY = "wechat_jsapi_ticket_";

  private final static String CARDAPI_TICKET_KEY = "wechat_cardapi_ticket_";

  private final static int EXPIRE_TIME_SECOND = 1;
  private final static int WAIT_INTERVAL_MILLISECONDS = 100;
  private final static int WAIT_TIMEOUT_MILLISECONDS = 300;


  /**
   * 使用连接池保证线程安全
   */
  protected final StringRedisTemplate redisTemplate;

  private String accessTokenKey;

  private String jsapiTicketKey;

  private String cardapiTicketKey;

  public WxMpInRedisTemplateConfigStorage(StringRedisTemplate redisTemplate) {
    this.redisTemplate = redisTemplate;
  }

  /**
   * 每个公众号生成独有的存储key
   */
  @Override
  public void setAppId(String appId) {
    super.setAppId(appId);
    this.accessTokenKey = ACCESS_TOKEN_KEY.concat(appId);
    this.jsapiTicketKey = JSAPI_TICKET_KEY.concat(appId);
    this.cardapiTicketKey = CARDAPI_TICKET_KEY.concat(appId);
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
    DistributeLock lock = new DistributeLock(redisTemplate, "accessTokenLock", EXPIRE_TIME_SECOND,
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

  @Override
  public String getJsapiTicket() {
    return redisTemplate.opsForValue().get(this.jsapiTicketKey);
  }

  @Override
  public boolean isJsapiTicketExpired() {
    return Optional.ofNullable(redisTemplate.getExpire(jsapiTicketKey, TimeUnit.SECONDS))
        .orElse(0L) < 2;
  }

  @Override
  public synchronized void updateJsapiTicket(String jsapiTicket, int expiresInSeconds) {
    DistributeLock lock = new DistributeLock(redisTemplate, "jsapiTicketLock", EXPIRE_TIME_SECOND,
        WAIT_INTERVAL_MILLISECONDS, WAIT_TIMEOUT_MILLISECONDS, false);
    lock.lock(() -> {
      redisTemplate.opsForValue()
          .set(this.jsapiTicketKey, jsapiTicket, expiresInSeconds - 200, TimeUnit.SECONDS);
      return true;
    });
  }

  @Override
  public void expireJsapiTicket() {
    redisTemplate.expire(this.jsapiTicketKey, 0, TimeUnit.SECONDS);
  }

  @Override
  public String getCardApiTicket() {
    return redisTemplate.opsForValue().get(this.cardapiTicketKey);
  }

  @Override
  public boolean isCardApiTicketExpired() {
    return Optional.ofNullable(redisTemplate.getExpire(cardapiTicketKey, TimeUnit.SECONDS))
        .orElse(0L) < 2;
  }

  @Override
  public synchronized void updateCardApiTicket(String cardApiTicket, int expiresInSeconds) {
    DistributeLock lock = new DistributeLock(redisTemplate, "cardApiTicketLock", EXPIRE_TIME_SECOND,
        WAIT_INTERVAL_MILLISECONDS, WAIT_TIMEOUT_MILLISECONDS, false);
    lock.lock(() -> {
      redisTemplate.opsForValue()
          .set(this.cardapiTicketKey, cardApiTicket, expiresInSeconds - 200, TimeUnit.SECONDS);
      return true;
    });
  }

  @Override
  public void expireCardApiTicket() {
    redisTemplate.expire(this.cardapiTicketKey, 0, TimeUnit.SECONDS);
  }
}
