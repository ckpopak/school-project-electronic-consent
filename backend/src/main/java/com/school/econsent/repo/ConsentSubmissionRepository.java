package com.school.econsent.repo;

import com.school.econsent.domain.ConsentSubmission;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ConsentSubmissionRepository extends JpaRepository<ConsentSubmission, Long> {

  Optional<ConsentSubmission> findByCampaignIdAndStudentId(Long campaignId, Long studentId);

  @Query(
      """
      SELECT s FROM ConsentSubmission s
      JOIN FETCH s.student st
      JOIN FETCH s.campaign c
      WHERE c.id = :campaignId AND s.status = 'SUBMITTED'
      ORDER BY st.className, st.classNumber, st.fullName
      """)
  List<ConsentSubmission> findSubmittedForCampaign(@Param("campaignId") Long campaignId);

  @Query(
      """
      SELECT s FROM ConsentSubmission s
      JOIN FETCH s.campaign c
      JOIN FETCH s.student st
      WHERE st.guardian.id = :guardianId AND c.id = :campaignId
      """)
  List<ConsentSubmission> findByGuardianAndCampaign(
      @Param("guardianId") Long guardianId, @Param("campaignId") Long campaignId);
}
