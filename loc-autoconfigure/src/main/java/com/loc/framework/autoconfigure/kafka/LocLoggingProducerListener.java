package com.loc.framework.autoconfigure.kafka;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.kafka.support.LoggingProducerListener;
import org.springframework.util.ObjectUtils;

/**
 * Created on 2018/2/24.
 */
@Slf4j
public class LocLoggingProducerListener<K, V> extends LoggingProducerListener<K, V> {

  @Override
  public void onSuccess(String topic, Integer partition, K key, V value,
      RecordMetadata recordMetadata) {
    StringBuffer logOutput = new StringBuffer();
    logOutput.append("loc kafka producer sending a message");
    logOutput.append(" with key='"
        + KafkaUtils.toDisplayString(ObjectUtils.nullSafeToString(key), KafkaUtils.MAX_CONTENT_LOGGED) + "'");
    logOutput.append(" and payload='"
        + KafkaUtils.toDisplayString(ObjectUtils.nullSafeToString(value), KafkaUtils.MAX_CONTENT_LOGGED) + "'");
    logOutput.append(" to topic " + topic);
    if (partition != null) {
      logOutput.append(" and partition " + partition);
    }
    logOutput.append(":");
    log.info(logOutput.toString());
  }


  @Override
  public boolean isInterestedInSuccess() {
    return true;
  }

}
