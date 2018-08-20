package com.loc.framework.autoconfigure;

import lombok.Getter;

@Getter
public class LocServiceException extends RuntimeException {

  private String msg;

  private int code = -1;


  public LocServiceException(String msg) {
    super(msg);
    this.msg = msg;
  }

  public LocServiceException(String msg, Throwable e) {
    super(msg, e);
    this.msg = msg;
  }

  public LocServiceException(int code, String msg) {
    super(msg);
    this.msg = msg;
    this.code = code;
  }

  public LocServiceException(int code, String msg, Throwable e) {
    super(msg, e);
    this.msg = msg;
    this.code = code;
  }

}
