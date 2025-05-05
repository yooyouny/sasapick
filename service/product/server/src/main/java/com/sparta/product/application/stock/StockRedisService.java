package com.sparta.product.application.stock;

import com.sparta.product.infrastructure.utils.StockRedisDto;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class StockRedisService {
  private final RedisTemplate<String, Object> stockRedisTemplate;
  public static final String PRODUCT_STOCK_KEY_PREFIX = "stock:";
  public static final String STOCK_FIELD = "quantity";

  public List<StockRedisDto> decreaseStock(Map<Long, Integer> orderProducts) {
    if (orderProducts == null || orderProducts.isEmpty()) {
      return Collections.emptyList();
    }

    List<String> keys = new ArrayList<>();
    List<String> quantities = new ArrayList<>();
    List<Long> productIds = new ArrayList<>(orderProducts.keySet());

    for (Long productId : productIds) {
      keys.add(PRODUCT_STOCK_KEY_PREFIX + productId);
      quantities.add(String.valueOf(orderProducts.get(productId)));
    }

    String decreaseScript =
        """
        local results = {}
        for i = 1, #KEYS do
            local key = KEYS[i]
            local quantity = tonumber(ARGV[i])
            local current = tonumber(redis.call('hget', key, 'quantity')) or 0

            if current < quantity then
                table.insert(results, 0)  -- 실패
                table.insert(results, current)  -- 현재 수량
                table.insert(results, 0)  -- 변경 수량 (실패시 0)
            else
                local newQuantity = redis.call('hincrby', key, 'quantity', -quantity)
                table.insert(results, 1)  -- 성공
                table.insert(results, newQuantity)  -- 변경 후 수량
                table.insert(results, -quantity)  -- 변경 수량
            end
        end
        return results
        """;

    List<Long> results =
        stockRedisTemplate.execute(
            new DefaultRedisScript<>(decreaseScript, List.class),
            keys,
            (Object) quantities.toArray(new String[0]));

    return parseResults(results, productIds);
  }

  public List<StockRedisDto> increaseStock(Map<Long, Integer> orderProducts) {
    if (orderProducts == null || orderProducts.isEmpty()) {
      return Collections.emptyList();
    }

    List<String> keys = new ArrayList<>();
    List<String> quantities = new ArrayList<>();
    List<Long> productIds = new ArrayList<>(orderProducts.keySet());

    for (Long productId : productIds) {
      keys.add(PRODUCT_STOCK_KEY_PREFIX + productId);
      quantities.add(String.valueOf(orderProducts.get(productId)));
    }

    String increaseScript =
        """
        local results = {}
        for i = 1, #KEYS do
            local key = KEYS[i]
            local quantity = tonumber(ARGV[i])
            local exists = redis.call('exists', key)
            local current = 0

            if exists == 0 then
                redis.call('hset', key, 'quantity', quantity)
                current = quantity
                table.insert(results, 1)  -- 성공
                table.insert(results, current)  -- 현재 수량
                table.insert(results, quantity)  -- 변경 수량
            else
                current = redis.call('hincrby', key, 'quantity', quantity)
                table.insert(results, 1)  -- 성공
                table.insert(results, current)  -- 현재 수량
                table.insert(results, quantity)  -- 변경 수량
            end
        end
        return results
        """;

    List<Long> results =
        stockRedisTemplate.execute(
            new DefaultRedisScript<>(increaseScript, List.class),
            keys,
            (Object) quantities.toArray(new String[0]));

    return parseResults(results, productIds);
  }

  private List<StockRedisDto> parseResults(List<Long> results, List<Long> productIds) {
    List<StockRedisDto> stockResults = new ArrayList<>();

    if (results != null) {
      for (int i = 0; i < productIds.size(); i++) {
        int baseIndex = i * 3;
        if (baseIndex + 2 < results.size()) {
          boolean success = results.get(baseIndex) == 1;
          int currentQuantity = results.get(baseIndex + 1).intValue();
          int changeQuantity = results.get(baseIndex + 2).intValue();

          stockResults.add(
              new StockRedisDto(productIds.get(i), success, currentQuantity, changeQuantity));
        }
      }
    }

    return stockResults;
  }
}
