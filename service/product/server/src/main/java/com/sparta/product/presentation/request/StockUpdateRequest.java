package com.sparta.product.presentation.request;

import com.sparta.product.domain.model.StockEventType;

public record StockUpdateRequest(Long productId, StockEventType type, Integer quantity) {}
