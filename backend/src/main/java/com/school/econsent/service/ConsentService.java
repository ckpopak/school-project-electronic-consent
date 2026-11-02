package com.school.econsent.service;

import com.school.econsent.domain.AppUser;
import com.school.econsent.domain.Campaign;
import com.school.econsent.domain.ConsentSubmission;
import com.school.econsent.domain.Student;
import com.school.econsent.repo.CampaignRepository;
import com.school.econsent.repo.ConsentSubmissionRepository;
import com.school.econsent.repo.StudentRepository;
import com.school.econsent.web.ApiException;
import java.time.Instant;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ConsentService {

  private final CampaignRepository campaigns;
  private final StudentRepository students;
  private final ConsentSubmissionRepository submissions;

  public ConsentService(
      CampaignRepository campaigns,
      StudentRepository students,
      ConsentSubmissionRepository submissions) {
    this.campaigns = campaigns;
    this.students = students;
    this.submissions = submissions;
  }

  public void assertCampaignOpen(Campaign campaign) {
    Instant now = Instant.now();
    if (now.isBefore(campaign.getStartsAt())) {
      throw new ApiException(HttpStatus.BAD_REQUEST, "This consent campaign is not open yet.");
    }
    if (now.isAfter(campaign.getDeadlineAt())) {
      throw new ApiException(HttpStatus.BAD_REQUEST, "The submission deadline has passed.");
    }
  }

  @Transactional
  public ConsentSubmission submit(
      AppUser parent, Long campaignId, Long studentId, String decision, String scopeNote) {
    Campaign campaign =
        campaigns
            .findByIdWithSchool(campaignId)
            .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Campaign not found."));
    assertCampaignOpen(campaign);

    Student student =
        students
            .findById(studentId)
            .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Student not found."));
    if (!student.getGuardian().getId().equals(parent.getId())) {
      throw new ApiException(HttpStatus.FORBIDDEN, "You can only consent for your own children.");
    }

    validateDecision(decision);

    ConsentSubmission submission =
        submissions
            .findByCampaignIdAndStudentId(campaignId, studentId)
            .orElseGet(
                () -> {
                  ConsentSubmission created = new ConsentSubmission();
                  created.setStatus("SUBMITTED");
                  return created;
                });

    if (submission.getId() != null && "SUBMITTED".equals(submission.getStatus())) {
      throw new ApiException(
          HttpStatus.CONFLICT, "A submission already exists. Withdraw it first to change.");
    }

    submission.setCampaign(campaign);
    submission.setStudent(student);
    submission.setDecision(decision);
    submission.setScopeNote(scopeNote);
    submission.setStatus("SUBMITTED");
    submission.setSubmittedAt(Instant.now());
    return submissions.save(submission);
  }

  @Transactional
  public void withdraw(AppUser parent, Long submissionId) {
    ConsentSubmission submission =
        submissions
            .findById(submissionId)
            .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Submission not found."));
    // Touch associations while session is open
    submission.getCampaign().getId();
    submission.getStudent().getGuardian().getId();
    if (!submission.getStudent().getGuardian().getId().equals(parent.getId())) {
      throw new ApiException(HttpStatus.FORBIDDEN, "Not your submission.");
    }
    assertCampaignOpen(submission.getCampaign());
    submission.setStatus("WITHDRAWN");
    submissions.save(submission);
  }

  private void validateDecision(String decision) {
    if (!decision.equals("YES") && !decision.equals("NO") && !decision.equals("LIMITED")) {
      throw new ApiException(HttpStatus.BAD_REQUEST, "Decision must be YES, NO, or LIMITED.");
    }
  }
}
