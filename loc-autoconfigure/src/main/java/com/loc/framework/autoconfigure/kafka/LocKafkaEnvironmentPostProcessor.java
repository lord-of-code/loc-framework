package com.loc.framework.autoconfigure.kafka;

import java.util.HashMap;
import java.util.Map;
import org.apache.kafka.common.serialization.ByteArrayDeserializer;
import org.apache.kafka.common.serialization.ByteArraySerializer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;

/**
 * Created on 2017/9/12.
 */
public class LocKafkaEnvironmentPostProcessor implements EnvironmentPostProcessor {

  private static final String KAFKA_BINDER_DEFAULT_PROPERTIES = "kafkaBinderDefaultProperties";

  public final static String SPRING_KAFKA = "spring.kafka";

  public final static String SPRING_KAFKA_PRODUCER = SPRING_KAFKA + ".producer";

  public final static String SPRING_KAFKA_CONSUMER = SPRING_KAFKA + ".consumer";

  public final static String SPRING_KAFKA_PRODUCER_KEY_SERIALIZER = SPRING_KAFKA_PRODUCER + "." + "keySerializer";

  public final static String SPRING_KAFKA_PRODUCER_VALUE_SERIALIZER = SPRING_KAFKA_PRODUCER + "." + "valueSerializer";

  public final static String SPRING_KAFKA_CONSUMER_KEY_DESERIALIZER = SPRING_KAFKA_CONSUMER + "." + "keyDeserializer";

  public final static String SPRING_KAFKA_CONSUMER_VALUE_DESERIALIZER = SPRING_KAFKA_CONSUMER + "." + "valueDeserializer";

  @Override
  public void postProcessEnvironment(ConfigurableEnvironment environment,
      SpringApplication application) {

    if (!environment.getPropertySources().contains(KAFKA_BINDER_DEFAULT_PROPERTIES)) {
      Map<String, Object> kafkaBinderDefaultProperties = new HashMap<>();
      kafkaBinderDefaultProperties.put(SPRING_KAFKA_PRODUCER_KEY_SERIALIZER, ByteArraySerializer.class.getName());
      kafkaBinderDefaultProperties.put(SPRING_KAFKA_PRODUCER_VALUE_SERIALIZER, ByteArraySerializer.class.getName());
      kafkaBinderDefaultProperties.put(SPRING_KAFKA_CONSUMER_KEY_DESERIALIZER, ByteArrayDeserializer.class.getName());
      kafkaBinderDefaultProperties.put(SPRING_KAFKA_CONSUMER_VALUE_DESERIALIZER, ByteArrayDeserializer.class.getName());
      environment.getPropertySources().addLast(new MapPropertySource(KAFKA_BINDER_DEFAULT_PROPERTIES, kafkaBinderDefaultProperties));
    }
  }
}
