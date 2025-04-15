package com.sparta.product.presentation.controller;

import com.sparta.product.application.stock.StockService;
import com.sparta.product.presentation.request.StockUpdateRequest;
import com.sparta.product.presentation.response.StockHistoryResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@Validated
public class StockController {
  private final StockService stockService;

  @PutMapping("/stock")
  public StockHistoryResponse stockUpdate(@RequestBody StockUpdateRequest request) {
    return stockService.updateStock(request.productId(), request.type(), request.quantity());
  }
}
