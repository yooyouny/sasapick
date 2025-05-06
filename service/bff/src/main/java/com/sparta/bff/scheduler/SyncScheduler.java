package com.sparta.bff.scheduler;

import com.sparta.bff.domain.DisplayEvent;
import com.sparta.bff.domain.EventProduct;
import com.sparta.bff.feign.BFFClient;
import com.sparta.bff.feign.EventDto;
import com.sparta.bff.feign.ProductDto;
import com.sparta.bff.repository.DisplayEventRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j(topic = "SyncScheduler")
public class SyncScheduler {
  private final BFFClient bffClient;
  private final DisplayEventRepository displayEventRepository;

  @Scheduled(fixedRate = 1800000) // 30분 주기
  public void syncProductData() {
    log.info("Syncing product data at {}", LocalDateTime.now());
    List<DisplayEvent> displayAllEvents = displayEventRepository.findAll();

    for(DisplayEvent displayEvent : displayAllEvents) {
      EventDto eventDto = bffClient.getEventById(displayEvent.getEventId());
      displayEvent.syncEventData(eventDto.imgUrl(), eventDto.eventTitle(), LocalDateTime.now());

      syncProductDataByEvent(displayEvent);
      displayEventRepository.save(displayEvent);
    }

    log.info("Product sync completed at {}", LocalDateTime.now());
  }
  public void syncProductDataByEvent(DisplayEvent event){
    List<EventProduct> savedProducts = event.getProducts(); // 기존에 저장된 product 정보들
    List<Long> productIds = savedProducts.stream()
        .map(EventProduct::getProductId).collect(Collectors.toList());

    List<ProductDto> updatedProducts = bffClient.getProductList(productIds);// 새로 불러온 product 정보들
    updatedProducts.forEach(newProduct ->
      savedProducts.stream()
          .filter(oldProduct -> oldProduct.getProductId().equals(newProduct.productId()))
          .findFirst()
          .ifPresent(oldProduct ->
              oldProduct.syncProductData(newProduct.productName(),
                newProduct.thumbnailUrl(),
                newProduct.displayPrice(),
                LocalDateTime.now()
            )
          )
    );
  }
}
