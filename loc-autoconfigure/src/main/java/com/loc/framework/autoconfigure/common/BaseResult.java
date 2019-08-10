package com.loc.framework.autoconfigure.common;

import lombok.Data;

@Data
public class BaseResult<T> {
  private int code;
  private String detail;
  private String traceId;
  private T data;

  public BaseResult(T data) {
    this.code = BaseResultCode.SUCCESS_RESPONSE_CODE.getCode();
    this.detail = BaseResultCode.SUCCESS_RESPONSE_CODE.getMsg();
    this.data = data;
  }

  public BaseResult() {
    this.code = BaseResultCode.SUCCESS_RESPONSE_CODE.getCode();
    this.detail = BaseResultCode.SUCCESS_RESPONSE_CODE.getMsg();
  }

  public BaseResult(int code, String detail) {
    this.code = code;
    this.detail = detail;
  }

  public static <T> BaseResult<T> success() {
    return new BaseResult<>();
  }

  public static <T> BaseResult<T> success(T t) {
    BaseResult<T> r = new BaseResult<>();
    r.setData(t);
    return r;
  }
}
