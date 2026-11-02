package com.school.econsent.web;

import com.school.econsent.domain.AppUser;
import com.school.econsent.repo.AppUserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
public class DemoAuth {

  private final AppUserRepository users;

  public DemoAuth(AppUserRepository users) {
    this.users = users;
  }

  public AppUser requireUser(Long userId) {
    if (userId == null) {
      throw new ApiException(HttpStatus.UNAUTHORIZED, "Missing X-Demo-User-Id header (log in first).");
    }
    return users
        .findById(userId)
        .orElseThrow(() -> new ApiException(HttpStatus.UNAUTHORIZED, "Unknown demo user."));
  }

  public AppUser requireParent(Long userId) {
    AppUser user = requireUser(userId);
    if (!"PARENT".equals(user.getRole())) {
      throw new ApiException(HttpStatus.FORBIDDEN, "Parent account required.");
    }
    return user;
  }

  public AppUser requireSchoolStaff(Long userId) {
    AppUser user = requireUser(userId);
    if (!"SCHOOL".equals(user.getRole())) {
      throw new ApiException(HttpStatus.FORBIDDEN, "School staff account required.");
    }
    if (user.getSchool() == null) {
      throw new ApiException(HttpStatus.FORBIDDEN, "School staff must be linked to a school.");
    }
    return user;
  }
}
