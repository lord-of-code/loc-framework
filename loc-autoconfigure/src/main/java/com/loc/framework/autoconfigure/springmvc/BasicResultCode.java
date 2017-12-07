package com.loc.framework.autoconfigure.springmvc;

import lombok.Getter;

/**
 * Created on 2017/12/6.
 */
@Getter
public enum BasicResultCode {

  SUCCESS(200_000, "success"),

  UNKNOWN_ERROR(500_000, "unknown exception"),

  METHOD_ARGUMENT_ERROR(400_000, "method argument error")
  ;

  private int code;
  private String msg;

  BasicResultCode(int code, String msg) {
    this.code = code;
    this.msg = msg;
  }
}
