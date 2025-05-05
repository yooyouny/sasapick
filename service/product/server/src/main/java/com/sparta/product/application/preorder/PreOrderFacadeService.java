package com.sparta.product.application.preorder;

import com.sparta.common.domain.entity.KafkaTopicConstant;
import com.sparta.product.application.dto.StockDto;
import com.sparta.product.application.stock.StockService;
import com.sparta.product.infrastructure.messaging.PreOrderProducer;
import com.sparta.product.infrastructure.utils.PreOrderRedisDto;
import com.sparta.product.presentation.exception.ProductErrorCode;
import com.sparta.product.presentation.exception.ProductServerException;
import com.sparta.product.presentation.request.PreOrderCreateRequest;
import dto.OrderCreateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PreOrderFacadeService {
  private final PreOrderProducer preOrderProducer;
  private final PreOrderLockService preOrderLockService;
  private final StockService stockService;
  private final PreOrderService preOrderService;

  @Transactional
  public void preOrder(Long preOrderId, Long addressId, Long userId) {
    PreOrderRedisDto cachedData = preOrderLockService.reservation(preOrderId, userId);
    OrderCreateRequest createRequest = PreOrderMapper.toDto(cachedData.productId(), addressId);
    preOrderProducer.send(
        KafkaTopicConstant.PROCESS_PREORDER, Long.toString(userId), createRequest);
  }

  @Transactional
  public Long createPreOrder(PreOrderCreateRequest request) {
    StockDto stockDto = stockService.getStockByProductId(request.productId());
    validateStock(
        stockDto.nowQuantity(), request.availableQuantity()); // 프로덕트 수량의 일부를 프리오더 수량으로 등록 가능
    return preOrderService.createPreOrder(request);
  }

  private void validateStock(int nowQuantity, int requestStock) {
    if (nowQuantity <= requestStock)
      throw new ProductServerException(ProductErrorCode.PREORDER_QUANTITY_CONFLICT);
  }
}
