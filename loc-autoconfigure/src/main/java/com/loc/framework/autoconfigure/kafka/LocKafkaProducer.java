package com.loc.framework.autoconfigure.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.util.concurrent.ListenableFuture;

/**
 * Created on 2018/2/24.
 */
@Data
@Slf4j
@AllArgsConstructor
public class LocKafkaProducer {

  private KafkaTemplate<byte[], byte[]> kafkaTemplate;

  private ObjectMapper objectMapper;

  public ListenableFuture<SendResult<byte[], byte[]>> send(String topic, Object message) {
    try {
      log.info("send message, topic is {}, message is {}", topic, objectMapper.writeValueAsString(message));
      ProducerRecord<byte[], byte[]> producerRecord = createRecord(topic, objectMapper.writeValueAsString(message));
      return kafkaTemplate.send(producerRecord);
    } catch (Exception e) {
      throw new LocKafkaProducerException(e.getMessage(), e);
    }
  }

  private ProducerRecord<byte[], byte[]> createRecord(String topic, String message) {
    ProducerRecord<byte[], byte[]> producerRecord = new ProducerRecord<>(topic,
        message.getBytes(StandardCharsets.UTF_8));
    producerRecord.headers().add(KafkaUtils.MESSAGE_ID_KEY,
        UUID.randomUUID().toString().getBytes(StandardCharsets.UTF_8));

    return producerRecord;
  }
}
