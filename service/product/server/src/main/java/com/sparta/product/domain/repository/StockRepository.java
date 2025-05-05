package com.sparta.product.domain.repository;

import static com.sparta.product.application.stock.StockRedisService.PRODUCT_STOCK_KEY_PREFIX;
import static com.sparta.product.application.stock.StockRedisService.STOCK_FIELD;
import static com.sparta.product.presentation.exception.ProductErrorCode.DUPLICATE_STOCK_KEY;

import com.sparta.product.presentation.exception.ProductErrorCode;
import com.sparta.product.presentation.exception.ProductServerException;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class StockRepository {

  @Qualifier("stockRedisTemplate")
  private final RedisTemplate<String, Object> stockRedisTemplate;

  public void setStock(Long productId, Integer quantity) {
    String key = getKey(productId);
    Optional.of(productId)
        .filter(id -> !existsStock(key))
        .orElseThrow(() -> new ProductServerException(DUPLICATE_STOCK_KEY));
    stockRedisTemplate.opsForHash().put(key, STOCK_FIELD, quantity);
  }

  private boolean existsStock(String key) {
    return stockRedisTemplate.hasKey(key)
        && stockRedisTemplate.opsForHash().hasKey(key, STOCK_FIELD);
  }

  public void deleteStock(Long productId) {
    String key = getKey(productId);
    stockRedisTemplate.delete(key);
  }

  public Integer getStock(Long productId) {
    String key = getKey(productId);
    Object value = stockRedisTemplate.opsForHash().get(key, STOCK_FIELD);
    return value != null ? Integer.parseInt(value.toString()) : 0;
  }

  public Integer increaseStock(Long productId, int changeQuantity) {
    String key = getKey(productId);
    Long newStock = stockRedisTemplate.opsForHash().increment(key, STOCK_FIELD, changeQuantity);
    return newStock.intValue();
  }

  public Integer decreaseStock(Long productId, int changeQuantity) {
    String key = getKey(productId);
    Integer currentStock = getStock(productId);

    if (currentStock < changeQuantity) throw new ProductServerException(ProductErrorCode.STOCK_NOT_AVAILABLE);

    Long newStock = stockRedisTemplate.opsForHash().increment(key, STOCK_FIELD, -changeQuantity);
    return newStock.intValue();
  }

  private String getKey(Long productId) {
    return PRODUCT_STOCK_KEY_PREFIX + productId;
  }
}
