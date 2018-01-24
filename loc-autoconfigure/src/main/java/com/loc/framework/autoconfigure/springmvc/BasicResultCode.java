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


  UNSUPPORTED_MEDIA_TYPE_ERROR(415_000, "unsupported media type error"),;

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
