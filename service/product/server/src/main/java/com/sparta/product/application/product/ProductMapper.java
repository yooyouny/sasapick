package com.sparta.product.application.product;

import com.sparta.product.application.dto.ImgDto;
import com.sparta.product.domain.model.Product;
import com.sparta.product.domain.model.StockEventType;
import com.sparta.product.domain.model.StockHistory;
import com.sparta.product.infrastructure.utils.StockRedisDto;
import com.sparta.product.presentation.request.ProductCreateRequest;
import com.sparta.product.presentation.request.ProductUpdateRequest;
import com.sparta.product_dto.ProductDto;

public class ProductMapper {

  public static ProductDto fromEntity(Product product) {
    return ProductDto.builder()
        .productId(product.getProductId())
        .productName(product.getProductName())
        .originalPrice(product.getOriginalPrice())
        .discountPercent(product.getDiscountPercent())
        .discountedPrice(product.getDiscountedPrice())
        .tags(product.getTagNames())
        .build();
  }

  public static Product toEntity(ProductCreateRequest request, ImgDto imgDto) {
    return Product.builder()
        .categoryId(request.categoryId())
        .productName(request.productName())
        .brandName(request.brandName())
        .mainColor(request.mainColor())
        .size(request.size())
        .description(request.description())
        .originalPrice(request.originalPrice())
        .discountPercent(request.discountPercent())
        .originImgUrl(imgDto.originImgUrl())
        .detailImgUrl(imgDto.detailImgUrl())
        .thumbnailImgUrl(imgDto.thumbnailImgUrl())
        .limitCountPerUser(request.limitCountPerUser())
        .tags(request.tags())
        .build();
  }

  public static void updateProduct(
      ProductUpdateRequest request, Product existingProduct, ImgDto imgUrls) {
    existingProduct.updateProduct(
        request.categoryId(),
        request.productName(),
        request.brandName(),
        request.mainColor(),
        request.size(),
        request.originalPrice(),
        request.discountPercent(),
        request.description(),
        imgUrls.originImgUrl(),
        imgUrls.detailImgUrl(),
        imgUrls.thumbnailImgUrl(),
        request.limitCountPerUser(),
        request.tags(),
        request.isPublic());
  }

  public static StockHistory toEntity(
      Long referenceId, StockEventType eventType, StockRedisDto request) {
    return StockHistory.builder()
        .productId(request.productId())
        .quantityChange(request.quantityChange())
        .currentQuantity(request.currentQuantity())
        .stockEventType(eventType)
        .referenceId(referenceId)
        .build();
  }
}
