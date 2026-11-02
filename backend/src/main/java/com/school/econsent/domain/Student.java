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
@Table(name = "student")
public class Student {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "guardian_id", nullable = false)
  private AppUser guardian;

  @Column(name = "full_name", nullable = false)
  private String fullName;

  @Column(name = "class_name", length = 50)
  private String className;

  @Column(name = "class_number", length = 10)
  private String classNumber;

  public Long getId() {
    return id;
  }

  public AppUser getGuardian() {
    return guardian;
  }

  public String getFullName() {
    return fullName;
  }

  public String getClassName() {
    return className;
  }

  public String getClassNumber() {
    return classNumber;
  }
}
