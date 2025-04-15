package com.sparta.product.infrastructure.utils;

import jakarta.validation.constraints.NotNull;

public record StockRedisDto(
    @NotNull(message = "상품 ID는 필수입니다") Long productId,
    @NotNull(message = "작업 성공여부는 필수입니다") Boolean success,
    @NotNull(message = "수량 변경은 필수입니다") Integer quantityChange,
    @NotNull(message = "현재 수량은 필수입니다") Integer currentQuantity
) {}
