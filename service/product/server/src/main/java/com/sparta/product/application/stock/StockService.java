package com.sparta.product.application.stock;

import com.sparta.product.application.dto.StockDto;
import com.sparta.product.application.product.ProductMapper;
import com.sparta.product.domain.model.StockEventType;
import com.sparta.product.domain.model.StockHistory;
import com.sparta.product.domain.repository.StockHistoryRepository;
import com.sparta.product.domain.repository.StockRepository;
import com.sparta.product.infrastructure.utils.StockRedisDto;
import com.sparta.product.presentation.exception.ProductErrorCode;
import com.sparta.product.presentation.exception.ProductServerException;
import com.sparta.product.presentation.response.StockHistoryResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class StockService {
  private final StockHistoryRepository stockHistoryRepository;
  private final StockRepository stockRepository;

  public void createHistory(
      Long referenceId, StockEventType eventType, List<StockRedisDto> requestList) {
    List<StockHistory> entity =
        requestList.stream()
            .map(list -> ProductMapper.toEntity(referenceId, eventType, list))
            .toList();
    stockHistoryRepository.saveAll(entity);
  }

  @Transactional
  public StockHistoryResponse updateStock(
      Long productId, StockEventType type, Integer changeStock) {
    if (changeStock < 1) throw new ProductServerException(ProductErrorCode.ILLEGAL_ARGUMENT);
    StockHistory stockHistory =
        switch (type) {
          case IMPORT -> {
            int nowQuantity = stockRepository.increaseStock(productId, changeStock);
            yield StockHistory.of(productId, changeStock, nowQuantity, type, null);
          }
          case EXPORT -> {
            int nowQuantity = stockRepository.decreaseStock(productId, changeStock);
            yield StockHistory.of(productId, -changeStock, nowQuantity, type, null);
          }
          default -> throw new ProductServerException(ProductErrorCode.ILLEGAL_ARGUMENT);
        };
    return StockHistoryResponse.from(stockHistoryRepository.save(stockHistory));
  }

  public StockDto getStockByProductId(Long productId) {
    int nowQuantity = stockRepository.getStock(productId);
    return new StockDto(productId, nowQuantity);
  }
}
