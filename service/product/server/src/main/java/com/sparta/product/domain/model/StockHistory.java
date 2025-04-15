package com.sparta.product.domain.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(
    name = "stock_history",
    indexes = {
      @Index(name = "idx_product_time", columnList = "product_id, created_at"),
      @Index(name = "idx_created_at", columnList = "created_at")
    })
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StockHistory {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private Long productId;

  @Column(nullable = false)
  private Integer quantityChange;

  @Column(nullable = false)
  private Integer currentQuantity;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 50)
  private StockEventType stockEventType;

  @Column(length = 100)
  private Long referenceId;

  @Column(nullable = false, updatable = false)
  private LocalDateTime createdAt;

  @PrePersist
  public void prePersist() {
    this.createdAt = LocalDateTime.now();
  }

  @Builder
  public StockHistory(
      Long productId,
      Integer quantityChange,
      Integer currentQuantity,
      StockEventType stockEventType,
      Long referenceId) {
    this.productId = productId;
    this.quantityChange = quantityChange;
    this.currentQuantity = currentQuantity;
    this.stockEventType = stockEventType;
    this.referenceId = referenceId;
  }

  public static StockHistory of(
      Long productId,
      Integer quantityChange,
      Integer currentQuantity,
      StockEventType stockEventType,
      Long referenceId) {
    return StockHistory.builder()
        .productId(productId)
        .referenceId(referenceId)
        .quantityChange(quantityChange)
        .currentQuantity(currentQuantity)
        .stockEventType(stockEventType)
        .build();
  }
}
