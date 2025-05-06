package com.sparta.bff.feign;

import java.util.List;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "bff")
public interface BFFClient {

  @GetMapping("/internal/products")
  List<ProductDto> getProductList(@RequestParam(name = "productIds") List<Long> productIds);

  @GetMapping("/internal/events")
  EventDto getEventById(@RequestParam(name = "eventId") Long eventId);
}
