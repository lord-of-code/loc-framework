package com.loc.framework.autoconfigure.kafka;

/**
 * Created by on 17/8/26.
 */
public class LocKafkaProducerException extends RuntimeException {

  public LocKafkaProducerException(String message, Throwable e){
    super(message, e);
  }

  public LocKafkaProducerException(Throwable e){
    super(e);
  }

}
