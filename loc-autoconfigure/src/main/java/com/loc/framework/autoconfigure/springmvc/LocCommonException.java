package com.loc.framework.autoconfigure.springmvc;

import lombok.Data;

/**
 * Created on 2017/12/7.
 */
@Data
public class LocCommonException extends RuntimeException {

  private int code;
  private String msg;

  public LocCommonException(int code, String msg) {
    super(msg);
    this.code = code;
    this.msg = msg;
  }

  public LocCommonException(int code, String msg, Throwable t) {
    super(msg, t);
    this.code = code;
    this.msg = msg;
  }

  public LocCommonException(Throwable t)  {
    super(BasicResultCode.RUNTIME_ERROR.getMsg(), t);
    this.code = BasicResultCode.RUNTIME_ERROR.getCode();
    this.msg = t.getMessage();
  }

  public LocCommonException(BasicResultCode basicResultCode) {
    super(basicResultCode.getMsg());
    this.code = basicResultCode.getCode();
    this.msg = basicResultCode.getMsg();
  }

  public LocCommonException(BasicResultCode basicResultCode, Throwable t) {
    super(basicResultCode.getMsg(), t);
    this.code = basicResultCode.getCode();
    this.msg = basicResultCode.getMsg();
  }
}
