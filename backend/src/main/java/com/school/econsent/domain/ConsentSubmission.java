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
@Table(name = "consent_submission")
public class ConsentSubmission {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "campaign_id", nullable = false)
  private Campaign campaign;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "student_id", nullable = false)
  private Student student;

  @Column(nullable = false, length = 20)
  private String decision;

  @Column(name = "scope_note", length = 500)
  private String scopeNote;

  @Column(nullable = false, length = 20)
  private String status;

  @Column(name = "submitted_at", nullable = false)
  private Instant submittedAt;

  public Long getId() {
    return id;
  }

  public Campaign getCampaign() {
    return campaign;
  }

  public Student getStudent() {
    return student;
  }

  public String getDecision() {
    return decision;
  }

  public String getScopeNote() {
    return scopeNote;
  }

  public String getStatus() {
    return status;
  }

  public Instant getSubmittedAt() {
    return submittedAt;
  }

  public void setDecision(String decision) {
    this.decision = decision;
  }

  public void setScopeNote(String scopeNote) {
    this.scopeNote = scopeNote;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public void setSubmittedAt(Instant submittedAt) {
    this.submittedAt = submittedAt;
  }

  public void setCampaign(Campaign campaign) {
    this.campaign = campaign;
  }

  public void setStudent(Student student) {
    this.student = student;
  }
}
