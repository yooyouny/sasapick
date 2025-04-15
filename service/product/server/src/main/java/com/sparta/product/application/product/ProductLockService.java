package com.sparta.product.application.product;

import com.sparta.product.application.preorder.DistributedLockComponent;
import com.sparta.product.application.stock.StockRedisService;
import java.util.Map;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProductLockService {

  private final DistributedLockComponent lockComponent;
  private final StockRedisService redisService;

  @Transactional
  public void reduceStock(Map<Long, Integer> productQuantities) {

    Set<Long> productIds = productQuantities.keySet();
    lockComponent.executeForMultipleProducts(
        productIds.stream().map("stockLock_%s"::formatted).toList(),
        3000, // 락 대기 시간
        3000, // 점유 시간
        3, // 재시도 횟수
        3000, // 재시도 대기 시간
        () -> {
          redisService.decreaseStock(productQuantities);
        });
  }

  @Transactional
  public void rollbackStock(Map<Long, Integer> productQuantities) {
    Set<Long> productIds = productQuantities.keySet();
    lockComponent.executeForMultipleProducts(
        productIds.stream().map("stockLock_%s"::formatted).toList(),
        3000, // 락 대기 시간
        3000, // 점유 시간
        3, // 재시도 횟수
        3000, // 재시도 대기 시간
        () -> {
          redisService.increaseStock(productQuantities);
        });
  }
}
