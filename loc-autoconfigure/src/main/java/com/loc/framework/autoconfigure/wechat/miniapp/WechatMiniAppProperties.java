package com.loc.framework.autoconfigure.wechat.miniapp;

import javax.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

/**
 * Created on 2018/5/17.
 */
@Data
@Validated
@ConfigurationProperties(prefix = "hn.wechat.miniapp")
public class WechatMiniAppProperties {
  /**
   * 设置微信小程序的appId
   */
  @NotNull
  private String appId;

  /**
   * 设置微信小程序的Secret
   */
  @NotNull
  private String secret;

  /**
   * 设置微信小程序的token
   */
  private String token;

  /**
   * 设置微信小程序的EncodingAESKey
   */
  private String aesKey;

  /**
   * 消息格式，XML或者JSON
   */
  private String msgDataFormat;
}
