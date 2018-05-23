package com.loc.framework.autoconfigure.test.kafka;

import static org.assertj.core.api.Assertions.assertThat;

import com.loc.framework.autoconfigure.kafka.KafkaUtils;
import com.loc.framework.autoconfigure.kafka.LocKafkaAutoConfiguration;
import com.loc.framework.autoconfigure.kafka.LocKafkaProducer;
import com.loc.framework.autoconfigure.kafka.LocLoggingProducerListener;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.assertj.core.util.Lists;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.ServletWebServerFactoryAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.listener.AbstractMessageListenerContainer.AckMode;
import org.springframework.kafka.listener.config.ContainerProperties;
import org.springframework.kafka.support.converter.RecordMessageConverter;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created on 2018/1/4.
 */
@RunWith(SpringRunner.class)
@WebMvcTest
@TestPropertySource(properties = {
    "spring.kafka.producer.bootstrap-servers = 127.0.0.1:9092",
    "spring.kafka.consumer.bootstrap-servers = 127.0.0.1:9092",
    "spring.kafka.consumer.enable-auto-commit = false",
    "spring.kafka.consumer.group-id = loc-kafka-unittest",
    "spring.kafka.listener.ack-mode = record",
    "spring.kafka.listener.concurrency = 3",
    "spring.kafka.producer.key-serializer = org.apache.kafka.common.serialization.ByteArraySerializer",
    "spring.kafka.producer.value-serializer = org.apache.kafka.common.serialization.ByteArraySerializer",
    "spring.kafka.consumer.key-deserializer = org.apache.kafka.common.serialization.ByteArrayDeserializer",
    "spring.kafka.consumer.value-deserializer = org.apache.kafka.common.serialization.ByteArrayDeserializer",
})
@DirtiesContext
public class LocKafkaTest {

  @Autowired
  private LocKafkaProducer locKafkaProducer;

  @Autowired
  private LocLoggingProducerListener locLoggingProducerListener;

  @Autowired
  private RecordMessageConverter recordMessageConverter;

  @Autowired
  private ConcurrentKafkaListenerContainerFactory concurrentKafkaListenerContainerFactory;

  @Test
  public void testProducer() throws Exception {
    assertThat(locKafkaProducer).isNotNull();
    assertThat(locKafkaProducer.getKafkaTemplate()).isNotNull();
    assertThat(locKafkaProducer.getObjectMapper()).isNotNull();
    assertThat(locKafkaProducer.getKafkaTemplate().getMessageConverter()).isNotNull();

    assertThat(locLoggingProducerListener).isNotNull();
    assertThat(locLoggingProducerListener.isInterestedInSuccess()).isTrue();

    assertThat(recordMessageConverter).isNotNull();
  }

  @Test
  @SuppressWarnings("unchecked")
  public void testConsumer() throws Exception {
    assertThat(concurrentKafkaListenerContainerFactory).isNotNull();
    ContainerProperties containerProperties = concurrentKafkaListenerContainerFactory
        .getContainerProperties();

    assertThat(containerProperties).isNotNull();
    assertThat(containerProperties.getGenericErrorHandler()).isNotNull();
    assertThat(containerProperties.getAckMode()).isEqualTo(AckMode.RECORD);

    ConsumerFactory consumerFactory = concurrentKafkaListenerContainerFactory.getConsumerFactory();
    assertThat(consumerFactory).isNotNull();
    assertThat(consumerFactory.getConfigurationProperties()).isNotNull();
    assertThat(consumerFactory.getConfigurationProperties()).isNotNull();
    assertThat(consumerFactory.getConfigurationProperties().get("group.id"))
        .isEqualTo("loc-kafka-unittest");
    assertThat(consumerFactory.getConfigurationProperties().get("bootstrap.servers"))
        .isEqualTo(Lists.newArrayList("127.0.0.1:9092"));
    assertThat(consumerFactory.getConfigurationProperties().get("enable.auto.commit")).isEqualTo(false);
    assertThat(consumerFactory.getConfigurationProperties().get("key.deserializer"))
        .isEqualTo(org.apache.kafka.common.serialization.ByteArrayDeserializer.class);
    assertThat(consumerFactory.getConfigurationProperties().get("value.deserializer"))
        .isEqualTo(org.apache.kafka.common.serialization.ByteArrayDeserializer.class);
  }

  @Test
  public void testKafkaUtils() {
    assertThat(KafkaUtils.MAX_CONTENT_LOGGED).isEqualTo(1024);
    assertThat(KafkaUtils.MESSAGE_ID_KEY).isEqualTo("mid");
    assertThat(KafkaUtils.toDisplayString("abcdefg", 1024)).isEqualTo("abcdefg");
    assertThat(KafkaUtils.toDisplayString("abcdefg", 3)).isEqualTo("abc...");
  }


  @MinimalWebConfiguration
  @RestController
  @Validated
  public static class KafkaController {

    @GetMapping(value = "/kafka/send")
    public BasicResult<String> send() {
      return BasicResult.success();
    }
  }

  @Target(ElementType.TYPE)
  @Retention(RetentionPolicy.RUNTIME)
  @Documented
  @Configuration
  @Import({
      ServletWebServerFactoryAutoConfiguration.class,
      JacksonAutoConfiguration.class,
      KafkaAutoConfiguration.class,
      LocKafkaAutoConfiguration.class
  })
  protected @interface MinimalWebConfiguration {

  }

}
