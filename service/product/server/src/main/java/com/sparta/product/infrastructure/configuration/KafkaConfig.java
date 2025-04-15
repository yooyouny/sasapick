package com.sparta.product.infrastructure.configuration;

import com.sparta.product.infrastructure.messaging.PreOrderProducer;
import com.sparta.product.infrastructure.messaging.StockProducer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

@ConditionalOnProperty(value = "kafka.enabled", matchIfMissing = true)
@Configuration
public class KafkaConfig {
  @Bean
  public KafkaTemplate<String, Object> stringKeyKafkaTemplate(
      ProducerFactory<String, Object> producerFactory) {
    return new KafkaTemplate<>(producerFactory);
  }

  @Bean
  public KafkaTemplate<Long, Object> longKeyKafkaTemplate(
      ProducerFactory<Long, Object> producerFactory) {
    return new KafkaTemplate<>(producerFactory);
  }

  @Bean
  public PreOrderProducer preOrderProducer(KafkaTemplate<String, Object> stringKeyKafkaTemplate) {
    return new PreOrderProducer(stringKeyKafkaTemplate);
  }

  @Bean
  public StockProducer stockProducer(KafkaTemplate<Long, Object> longKeyKafkaTemplate) {
    return new StockProducer(longKeyKafkaTemplate);
  }
}
