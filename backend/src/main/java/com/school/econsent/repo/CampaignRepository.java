package com.school.econsent.repo;

import com.school.econsent.domain.Campaign;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CampaignRepository extends JpaRepository<Campaign, Long> {

  @Query("SELECT c FROM Campaign c JOIN FETCH c.school ORDER BY c.deadlineAt DESC")
  List<Campaign> findAllWithSchool();

  Optional<Campaign> findByQrToken(String qrToken);

  @Query("SELECT c FROM Campaign c JOIN FETCH c.school WHERE c.school.id = :schoolId ORDER BY c.deadlineAt DESC")
  List<Campaign> findBySchoolIdWithSchool(@Param("schoolId") Long schoolId);

  @Query("SELECT c FROM Campaign c JOIN FETCH c.school WHERE c.qrToken = :token")
  Optional<Campaign> findByQrTokenWithSchool(@Param("token") String token);

  @Query("SELECT c FROM Campaign c JOIN FETCH c.school WHERE c.id = :id")
  Optional<Campaign> findByIdWithSchool(@Param("id") Long id);
}
