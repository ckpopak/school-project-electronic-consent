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

@Entity
@Table(name = "app_user")
public class AppUser {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, unique = true)
  private String email;

  @Column(nullable = false)
  private String password;

  @Column(name = "display_name", nullable = false)
  private String displayName;

  @Column(nullable = false, length = 20)
  private String role;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "school_id")
  private School school;

  public Long getId() {
    return id;
  }

  public String getEmail() {
    return email;
  }

  public String getPassword() {
    return password;
  }

  public String getDisplayName() {
    return displayName;
  }

  public String getRole() {
    return role;
  }

  public School getSchool() {
    return school;
  }
}
