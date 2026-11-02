package com.school.econsent.web;

import com.school.econsent.domain.AppUser;
import com.school.econsent.repo.AppUserRepository;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

  private final AppUserRepository users;

  public AuthController(AppUserRepository users) {
    this.users = users;
  }

  public record LoginRequest(String email, String password) {}

  @PostMapping("/login")
  public Map<String, Object> login(@RequestBody LoginRequest body) {
    AppUser user =
        users
            .findByEmailIgnoreCase(body.email())
            .filter(u -> u.getPassword().equals(body.password()))
            .orElseThrow(() -> new ApiException(HttpStatus.UNAUTHORIZED, "Invalid email or password."));

    java.util.HashMap<String, Object> response = new java.util.HashMap<>();
    response.put("userId", user.getId());
    response.put("email", user.getEmail());
    response.put("displayName", user.getDisplayName());
    response.put("role", user.getRole());
    if (user.getSchool() != null) {
      response.put("schoolId", user.getSchool().getId());
      response.put("schoolName", user.getSchool().getName());
    }
    return response;
  }
}
