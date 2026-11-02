package com.school.econsent.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.Instant;

@Entity
@Table(name = "campaign")
public class Campaign {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "school_id", nullable = false)
  private School school;

  @Column(nullable = false)
  private String title;

  @Column(length = 2000)
  private String description;

  @Column(name = "qr_token", nullable = false, unique = true, length = 64)
  private String qrToken;

  @Column(name = "starts_at", nullable = false)
  private Instant startsAt;

  @Column(name = "deadline_at", nullable = false)
  private Instant deadlineAt;

  public Long getId() {
    return id;
  }

  public School getSchool() {
    return school;
  }

  public String getTitle() {
    return title;
  }

  public String getDescription() {
    return description;
  }

  public String getQrToken() {
    return qrToken;
  }

  public Instant getStartsAt() {
    return startsAt;
  }

  public Instant getDeadlineAt() {
    return deadlineAt;
  }
}
