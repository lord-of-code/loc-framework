package com.loc.framework.autoconfigure.common;

public enum BaseResultCode {
  SUCCESS_RESPONSE_CODE(0, "success"),


  UNKNOWN_THROWABLE_EXCEPTION_CODE(9999, "unknown throwable exception"),

  ;

  private int code;
  private String msg;

  BaseResultCode(int code, String msg) {
    this.code = code;
    this.msg = msg;
  }

  public String getMsg() {
    return msg;
  }

  public int getCode() {
    return code;
  }

}
