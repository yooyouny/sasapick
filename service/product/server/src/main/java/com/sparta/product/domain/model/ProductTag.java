package com.sparta.product.domain.model;

import java.util.stream.Stream;
import lombok.Getter;

@Getter
public enum ProductTag {
  BEST_SELLER("베스트셀러"),
  NEW_ARRIVAL("신상품"),
  LIMITED_EDITION("한정판"),
  SALE("할인"),
  TRENDING("인기상품"),
  ORGANIC("유기농"),
  ECO_FRIENDLY("친환경"),
  HANDMADE("수제품"),
  PREMIUM("프리미엄"),
  IMPORTED("수입품"),
  COUPON("쿠폰적용"),
  EXPRESS_DELIVERY("빠른배송"),
  FREE_SHIPPING("무료배송"),
  DISCOUNT("할인상품"),
  NEW("신규"),
  ONE_PLUS_ONE("1+1"),
  RECOMMEND("추천상품");
  
  private final String displayName;
  
  ProductTag(String displayName) {
    this.displayName = displayName;
  }
  
  public static ProductTag getByDisplayName(String displayName) {
    return Stream.of(values())
        .filter(tag -> tag.getDisplayName().equals(displayName))
        .findFirst()
        .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 태그 표시명입니다: " + displayName));
  }
}
