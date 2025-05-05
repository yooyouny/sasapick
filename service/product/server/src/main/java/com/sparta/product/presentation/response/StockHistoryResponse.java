package com.sparta.product.presentation.response;

import com.sparta.product.domain.model.StockHistory;
import java.time.LocalDateTime;

public record StockHistoryResponse(
    Long id,
    Long productId,
    Integer quantityChange,
    Integer currentQuantity,
    String stockEventType,
    Long referenceId,
    LocalDateTime createdAt
) {
  public static StockHistoryResponse from(StockHistory stockHistory) {
    return new StockHistoryResponse(
        stockHistory.getId(),
        stockHistory.getProductId(),
        stockHistory.getQuantityChange(),
        stockHistory.getCurrentQuantity(),
        stockHistory.getStockEventType().name(),
        stockHistory.getReferenceId(),
        stockHistory.getCreatedAt()
    );
  }
}
