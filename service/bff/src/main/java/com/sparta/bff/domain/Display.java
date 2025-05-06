package com.sparta.bff.domain;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "P_DISPLAY")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Display {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(length = 255)
  private String displayName;

  @Column(nullable = false)
  private LocalDateTime startDateTime;

  @Column(nullable = false)
  private LocalDateTime endDateTime;

  private String description;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private DisplayType type;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private DisplayState state;

  @OneToMany(mappedBy = "display", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<DisplayEvent> promotions = new ArrayList<>();

  public Display(
      String displayName,
      LocalDateTime startDateTime,
      LocalDateTime endDateTime,
      DisplayType type,
      String description) {
    this.displayName = displayName;
    this.startDateTime = startDateTime;
    this.endDateTime = endDateTime;
    this.description = description;
    this.state = DisplayState.DRAFT;
    this.type = type;
  }

  public void updateState(DisplayState state) {
    this.state = state;
  }
}
