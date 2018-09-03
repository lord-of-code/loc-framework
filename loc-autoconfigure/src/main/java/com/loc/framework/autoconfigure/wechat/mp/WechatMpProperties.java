package com.loc.framework.autoconfigure.wechat.mp;

import javax.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Data
@Validated
@ConfigurationProperties(prefix = "loc.wechat.mp")
public class WechatMpProperties {

  /**
   * 设置微信公众号的appid
   */
  @NotNull
  private String appId;

  /**
   * 设置微信公众号的app secret
   */
  @NotNull
  private String secret;

  /**
   * 设置微信公众号的token
   */
  @NotNull
  private String token;

  /**
   * 设置微信公众号的EncodingAESKey
   */
  private String aesKey;

}