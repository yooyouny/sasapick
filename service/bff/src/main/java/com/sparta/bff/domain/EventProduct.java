package com.sparta.bff.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "P_EVENT_PRODUCT")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EventProduct {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private Integer displayOrder;

  @Column(nullable = false)
  private Long productId;

  @ManyToOne(fetch = FetchType.LAZY)
  private DisplayEvent displayEvent;

  String productName;
  String thumbnailUrl;
  Long displayPrice;

  private LocalDateTime lastSyncAt;

  public EventProduct(
      Integer displayOrder,
      Long productId,
      String productName,
      String thumbnailUrl,
      Long displayPrice,
      LocalDateTime lastSyncAt) {
    this.displayOrder = displayOrder;
    this.productId = productId;
    this.productName = productName;
    this.thumbnailUrl = thumbnailUrl;
    this.displayPrice = displayPrice;
    this.lastSyncAt = lastSyncAt;
  }

  public void syncProductData(
      String productName, String thumbnailUrl, Long displayPrice, LocalDateTime lastSyncAt) {
    this.productName = productName;
    this.thumbnailUrl = thumbnailUrl;
    this.displayPrice = displayPrice;
    this.lastSyncAt = lastSyncAt;
  }
}
