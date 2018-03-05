package com.loc.framework.autoconfigure.kafka;

import java.nio.charset.StandardCharsets;
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
    StringBuilder logOutput = new StringBuilder();
    logOutput.append("loc kafka producer sending a message");
    logOutput.append(" with key='").append(KafkaUtils
        .toDisplayString(ObjectUtils.nullSafeToString(key), KafkaUtils.MAX_CONTENT_LOGGED))
        .append("'");
    logOutput.append(" and payload='")
        .append(KafkaUtils.toDisplayString(ObjectUtils.nullSafeToString(new String((byte[]) value,
            StandardCharsets.UTF_8)), KafkaUtils.MAX_CONTENT_LOGGED)).append("'");
    logOutput.append(" to topic ").append(topic);
    if (partition != null) {
      logOutput.append(" and partition ").append(partition);
    }
    logOutput.append(":");
    log.info(logOutput.toString());
  }


  @Override
  public boolean isInterestedInSuccess() {
    return true;
  }

}
