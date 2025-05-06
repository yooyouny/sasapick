package com.sparta.bff.feign;

public record ProductDto(
    Long productId,
    String productName,
    Long displayPrice,
    String thumbnailUrl
) {}
