package com.sparta.product.infrastructure.messaging;

import com.sparta.product.domain.model.StockEventType;
import com.sparta.product.infrastructure.utils.StockRedisDto;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.kafka.core.KafkaTemplate;

@RequiredArgsConstructor
@Slf4j(topic = "StockProducer in Product server")
public class StockProducer {
  private final KafkaTemplate<Long, Object> kafkaTemplate;

  public void save(String topic, Long productId, Integer stock) {
    kafkaTemplate.send(topic, productId, stock);
    log.info("save stock of {} in product server", productId);
  }

  public void delete(String topic, Long productId) {
    kafkaTemplate.send(topic, productId);
    log.info("delete stock of {} in product server", productId);
  }

  public void sendOrderResult(String topic, String orderId, Map<Long, Boolean> result) {
    kafkaTemplate.send(topic, Long.valueOf(orderId), result);
    log.info("Success stock decreasing and send to OrderServer result of {}", orderId);
  }

  public void saveHistory(
      String topic, String orderId, StockEventType type, List<StockRedisDto> histories) {
    String eventType = type.name();
    ProducerRecord<Long, Object> record =
        new ProducerRecord<>(topic, null, Long.valueOf(orderId), histories);
    record.headers().add("event_type", eventType.getBytes(StandardCharsets.UTF_8));

    kafkaTemplate.send(record);
    log.info("save decreasing History by Order of {}", orderId);
  }
}
