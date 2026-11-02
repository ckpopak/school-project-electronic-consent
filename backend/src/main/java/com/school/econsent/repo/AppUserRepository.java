package com.school.econsent.repo;

import com.school.econsent.domain.AppUser;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AppUserRepository extends JpaRepository<AppUser, Long> {

  @Query("SELECT u FROM AppUser u LEFT JOIN FETCH u.school WHERE LOWER(u.email) = LOWER(:email)")
  Optional<AppUser> findByEmailIgnoreCase(@Param("email") String email);
}
