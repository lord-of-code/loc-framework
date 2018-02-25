package com.loc.framework.autoconfigure.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.loc.framework.autoconfigure.ConditionalOnPrefixProperty;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.kafka.ConcurrentKafkaListenerContainerFactoryConfigurer;
import org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.adapter.RecordFilterStrategy;
import org.springframework.kafka.listener.config.ContainerProperties;
import org.springframework.kafka.support.converter.RecordMessageConverter;
import org.springframework.kafka.support.converter.StringJsonMessageConverter;

/**
 * Created on 2018/2/24.
 */
@Slf4j
@Configuration
@ConditionalOnPrefixProperty(prefix = "spring.kafka", value = KafkaProperties.class)
@ConditionalOnClass({KafkaTemplate.class, EnableKafka.class})
@AutoConfigureAfter({KafkaAutoConfiguration.class})
public class LocKafkaAutoConfiguration {

  private final ObjectMapper objectMapper;

  public LocKafkaAutoConfiguration(ObjectMapper objectMapper) {
    this.objectMapper = objectMapper;
  }

  @Bean
  @ConditionalOnMissingBean
  public LocKafkaProducer locKafkaProducer(KafkaTemplate<byte[], byte[]> kafkaTemplate) {
    return new LocKafkaProducer(kafkaTemplate, objectMapper);
  }

  @Bean
  @ConditionalOnMissingBean
  public LocLoggingProducerListener<byte[], byte[]> locLoggingProducerListener() {
    return new LocLoggingProducerListener<>();
  }

  @Bean
  @ConditionalOnMissingBean
  public RecordMessageConverter recordMessageConverter() {
    return new StringJsonMessageConverter(objectMapper);
  }

  @Bean(name = "kafkaListenerContainerFactory")
  public ConcurrentKafkaListenerContainerFactory<?, ?> kafkaListenerContainerFactory(
      ConcurrentKafkaListenerContainerFactoryConfigurer configurer,
      ConsumerFactory<Object, Object> kafkaConsumerFactory) {
    ConcurrentKafkaListenerContainerFactory<Object, Object> factory = new ConcurrentKafkaListenerContainerFactory<>();
    ContainerProperties containerProperties = factory.getContainerProperties();
    containerProperties.setErrorHandler(new LocKafkaConsumerErrorHandler());
    factory.setRecordFilterStrategy(locMessageFilterStrategy());
    factory.setMessageConverter(recordMessageConverter());
    configurer.configure(factory, kafkaConsumerFactory);
    return factory;
  }

  private RecordFilterStrategy<Object, Object> locMessageFilterStrategy() {
    return (consumerRecord) -> {
      Object value = consumerRecord.value();
      String message;
      if (value instanceof byte[]) {
        message = KafkaUtils.getMessage((byte[])value, "consumer filter", consumerRecord);
        log.info(message);
        return false;
      } else {
        log.error("Only byte[] or string supported");
        return true;
      }
    };
  }
}
