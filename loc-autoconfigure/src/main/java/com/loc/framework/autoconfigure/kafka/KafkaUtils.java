package com.loc.framework.autoconfigure.kafka;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import org.apache.kafka.clients.consumer.ConsumerRecord;

/**
 * Created on 2018/2/24.
 */
public interface KafkaUtils {
  String MESSAGE_ID_KEY = "mid";

  int MAX_CONTENT_LOGGED = 1024;

  static String toDisplayString(String original, int maxCharacters) {
    if (original.length() <= maxCharacters) {
      return original;
    }
    return original.substring(0, maxCharacters) + "...";
  }

  static String getMessage(byte[] value, String prefix, ConsumerRecord<?, ?> record) {
    String message = new String(value, StandardCharsets.UTF_8);
    StringBuffer sb = new StringBuffer();
    sb.append(prefix);
    sb.append("; topic is ").append(record.topic());
    sb.append("; partition is ").append(record.partition());
    sb.append("; offset is ").append(record.offset());
    if (record.headers() != null) {
      sb.append(" header is ");
      Arrays.stream(record.headers().toArray()).forEach(h -> sb.append(h.key()).append("=")
          .append(new String(h.value(), StandardCharsets.UTF_8)));
    }
    sb.append("; message is ").append(toDisplayString(message, 1024));
    return sb.toString();
  }
}
