package com.loc.framework.autoconfigure.springmvc;

import lombok.Getter;

/**
 * Created on 2017/12/6.
 */
@Getter
public enum BasicResultCode {

  SUCCESS(200_000, "success"),

  THROWABLE_ERROR(500_000, "throwable error"),
  RUNTIME_ERROR(500_001, "runtime exception"),
  SQL_ERROR(500_002, "sql exception"),
  REDIS_ERROR(500_003, "redis exception"),

  METHOD_ARGUMENT_MISS_ERROR(400_000, "method argument miss error"),
  METHOD_ARGUMENT_VALIDATE_ERROR(400_001, "method argument validate error"),
  METHOD_NOT_ALLOW_ERROR(405_000, "method not allow error"),

  UNSUPPORTED_MEDIA_TYPE_ERROR(415_000, "unsupported media type error"),

  SECURITY_ACCESS_DENIED(401_000, "未授权访问!"),
  SECURITY_AUTHENTICATION(401_001, "身份验证失败,请输入正确信息!"),
  SECURITY_TOKEN_NOT_ACTIVE(401_002, "TOKEN 已失效，刷新token或者重新获取!"),
  ;

  private int code;
  private String msg;
  private String detailMsg;

  BasicResultCode(int code, String msg) {
    this.code = code;
    this.msg = msg;
    this.detailMsg = msg;
  }

  BasicResultCode(int code, String msg, String detailMsg) {
    this.code = code;
    this.msg = msg;
    this.detailMsg = detailMsg;
  }
}
