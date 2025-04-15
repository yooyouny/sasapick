package com.sparta.product.infrastructure.messaging;

import com.sparta.common.domain.entity.KafkaTopicConstant;
import com.sparta.product.domain.model.StockEventType;
import com.sparta.product.infrastructure.utils.StockRedisDto;
import com.sparta.product.application.stock.StockService;
import com.sparta.product.application.stock.StockRedisService;
import com.sparta.product.domain.repository.StockRepository;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j(topic = "StockConsumer")
public class StockConsumer {
  private final StockRepository stockRepository;
  private final StockRedisService stockRedisService;
  private final StockProducer stockProducer;
  private final StockService stockService;

  @KafkaListener(topics = KafkaTopicConstant.STOCK_SAVED, groupId = "stock")
  public void stockCreate(
      @Payload Integer stock, @Header(KafkaHeaders.RECEIVED_KEY) Long productId) {
    stockRepository.setStock(productId, stock);
    log.info("save stock of product {}", productId);
  }

  @KafkaListener(topics = KafkaTopicConstant.STOCK_DELETED, groupId = "stock")
  public void stockDelete(@Header(KafkaHeaders.RECEIVED_KEY) Long productId) {
    stockRepository.deleteStock(productId);
    log.info("delete stock of product {}", productId);
  }

  @KafkaListener(topics = KafkaTopicConstant.STOCK_INCREASE, groupId = "stock")
  public void increaseStock(
      @Payload Map<Long, Integer> orderProducts,
      @Header(KafkaHeaders.RECEIVED_KEY) String orderId) {
    List<StockRedisDto> result = stockRedisService.increaseStock(orderProducts);
    stockProducer.saveHistory(
        KafkaTopicConstant.STOCK_HISTORY_SAVED, orderId, StockEventType.RETURN, result);
    log.info(" rollback of order {} in Product Server", orderId);
  }

  @KafkaListener(topics = KafkaTopicConstant.STOCK_DECREASE, groupId = "stock")
  public void decreaseStock(
      @Payload Map<Long, Integer> orderProducts,
      @Header(KafkaHeaders.RECEIVED_KEY) String orderId) {
    List<StockRedisDto> historyResult = stockRedisService.decreaseStock(orderProducts);
    Map<Long, Boolean> orderResult = convertToOrderMap(historyResult);
    stockProducer.sendOrderResult(KafkaTopicConstant.STOCK_COMPLETED, orderId, orderResult);
    stockProducer.saveHistory(
        KafkaTopicConstant.STOCK_HISTORY_SAVED, orderId, StockEventType.ORDER, historyResult);
    log.info("decrease stock by order {} in ProductServer", orderId);
  }

  @KafkaListener(topics = KafkaTopicConstant.STOCK_HISTORY_SAVED, groupId = "stock")
  public void saveHistory(
      @Payload List<StockRedisDto> stockHistoryList,
      @Header(KafkaHeaders.RECEIVED_KEY) String orderId,
      @Header("event_type") String eventTypeName) {
    StockEventType eventType = StockEventType.valueOf(eventTypeName);
    stockService.createHistory(Long.valueOf(orderId), eventType, stockHistoryList);
    log.info("decrease stock by order {} in ProductServer", orderId);
  }

  private Map<Long, Boolean> convertToOrderMap(List<StockRedisDto> results) {
    return results.stream()
        .collect(Collectors.toMap(StockRedisDto::productId, StockRedisDto::success));
  }
}
