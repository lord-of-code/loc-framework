package com.loc.framework.autoconfigure.springmvc;

import lombok.Data;

/**
 * Created on 2017/12/7.
 */
@Data
public class LocCommonException extends RuntimeException {

  private int code;
  private String msg;
  private String detailMsg;

  public LocCommonException(int code, String msg) {
    super(msg);
    this.code = code;
    this.msg = msg;
    this.detailMsg = msg;
  }

  public LocCommonException(int code, String msg, Throwable t) {
    super(msg, t);
    this.code = code;
    this.msg = msg;
    this.detailMsg = msg;
  }

  public LocCommonException(int code, String msg, String detailMsg) {
    super(msg);
    this.code = code;
    this.msg = msg;
    this.detailMsg = detailMsg;
  }

  public LocCommonException(int code, String msg, String detailMsg, Throwable t) {
    super(msg, t);
    this.code = code;
    this.msg = msg;
    this.detailMsg = detailMsg;
  }
}
