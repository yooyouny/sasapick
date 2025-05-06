package com.sparta.bff.domain;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "P_DISPLAY_EVENT")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DisplayEvent {
  @Id
  @GeneratedValue
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "display_id")
  private Display display;

  @Column(nullable = false)
  private Integer displayOrder;

  @Column(nullable = false)
  private Long eventId;
  private String eventTitle;
  private String imageUrl;

  @OneToMany(mappedBy = "displayEvent", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<EventProduct> products = new ArrayList<>();

  private LocalDateTime lastSyncAt;

  public DisplayEvent(Display display, Integer displayOrder, Long eventId, String eventTitle, String imageUrl,
      List<EventProduct> products, LocalDateTime lastSyncAt) {
    this.display = display;
    this.eventId = eventId;
    this.displayOrder = displayOrder;
    this.eventTitle = eventTitle;
    this.imageUrl = imageUrl;
    this.products = products;
    this.lastSyncAt = lastSyncAt;
  }

  public void syncEventData(String imageUrl, String eventTitle, LocalDateTime lastSyncAt) {
    this.imageUrl = imageUrl;
    this.eventTitle = eventTitle;
    this.lastSyncAt = lastSyncAt;
  }

}
