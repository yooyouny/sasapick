package com.sparta.product.domain.model;

import com.sparta.common.domain.entity.BaseEntity;
import com.sparta.product.domain.converter.ProductTagListConverter;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "P_PRODUCT")
public class Product extends BaseEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "product_id")
  private Long productId;

  @Column(nullable = false)
  private Long categoryId;

  @Column(nullable = false, length = 255)
  private String productName;

  @Column(nullable = false, length = 255)
  private String brandName;

  @Column(length = 50)
  private String mainColor;

  @Column(length = 50)
  private String size;

  @Column(columnDefinition = "TEXT")
  private String description;

  @Column(nullable = false, precision = 10, scale = 2)
  private BigDecimal originalPrice;

  @Column(precision = 10, scale = 2)
  private BigDecimal discountedPrice;

  @Column
  private Double discountPercent;

  @Column(length = 500)
  private String originImgUrl;

  @Column(length = 500)
  private String thumbnailImgUrl;

  @Column(length = 500)
  private String detailImgUrl;

  @Column private Integer limitCountPerUser = 0;
  @Column private Double averageRating = 0.0;
  @Column private Integer reviewCount = 0;
  @Column private Integer salesCount = 0;

  @Column private boolean isPublic = true;
  @Column private boolean soldout = false;
  @Column private boolean isDeleted = false;
  @Column private boolean isNew = true;

  @Convert(converter = ProductTagListConverter.class)
  @Column(length = 255)
  private List<ProductTag> productTags = new ArrayList<>();

  @Builder
  private Product(
      Long categoryId,
      String productName,
      String brandName,
      String mainColor,
      String size,
      BigDecimal originalPrice,
      Double discountPercent,
      String description,
      String originImgUrl,
      String thumbnailImgUrl,
      String detailImgUrl,
      int limitCountPerUser,
      List<ProductTag> tags) {
    this.categoryId = categoryId;
    this.productName = productName;
    this.brandName = brandName;
    this.mainColor = mainColor;
    this.size = size;
    this.originalPrice = originalPrice;
    this.discountPercent = discountPercent;
    applyDiscount(discountPercent);
    this.description = description;
    this.originImgUrl = originImgUrl;
    this.thumbnailImgUrl = thumbnailImgUrl;
    this.detailImgUrl = detailImgUrl;
    this.limitCountPerUser = limitCountPerUser;
    setProductTags(tags);
  }

  public void updateProduct(
      Long categoryId,
      String productName,
      String brandName,
      String mainColor,
      String size,
      BigDecimal originalPrice,
      Double discountPercent,
      String description,
      String originImgUrl,
      String detailImgUrl,
      String thumbnailImgUrl,
      Integer limitCountPerUser,
      List<ProductTag> tags,
      boolean isPublic) {
    this.categoryId = categoryId;
    this.productName = productName;
    this.brandName = brandName;
    this.mainColor = mainColor;
    this.size = size;
    this.originalPrice = originalPrice;
    this.discountPercent = discountPercent;
    applyDiscount(discountPercent);
    this.description = description;
    this.originImgUrl = originImgUrl;
    this.detailImgUrl = detailImgUrl;
    this.thumbnailImgUrl = thumbnailImgUrl;
    setProductTags(tags);
    this.limitCountPerUser = limitCountPerUser;
    this.isPublic = isPublic;
  }

  public void setIsNew(boolean isNew) {
    this.isNew = isNew;
  }

  public void isDelete() {
    this.isDeleted = true;
  }

  public void setSoldout(boolean status) {
    this.soldout = status;
  }

  public void applyDiscount(Double discountPercent) {
    if (discountPercent != null) {
      BigDecimal discountPercentBD = BigDecimal.valueOf(discountPercent);
      this.discountedPrice =
          this.originalPrice.multiply(
              BigDecimal.valueOf(1).subtract(discountPercentBD.divide(BigDecimal.valueOf(100))));
    } else {
      this.discountedPrice = this.originalPrice;
    }
  }

  private void setProductTags(List<ProductTag> tags) {
    this.productTags = tags != null 
        ? new ArrayList<>(tags)
        : new ArrayList<>();
  }

  public List<String> getTagNames() {
    return this.productTags.stream()
        .map(Enum::name)
        .toList();
  }
}
