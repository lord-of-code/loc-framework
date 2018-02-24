package com.loc.framework.autoconfigure.kafka;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.listener.ErrorHandler;

/**
 * Created on 2017/9/18.
 */
@Slf4j
@AllArgsConstructor
public class LocKafkaConsumerErrorHandler implements ErrorHandler {

  @Override
  public void handle(Exception thrownException, ConsumerRecord<?, ?> record) {
    Object value = record.value();
    String message;
    if (value instanceof byte[]) {
      message = KafkaUtils.getMessage((byte[]) value, "consumer error handler", record);
      log.error(message, thrownException);
    }
  }
}
