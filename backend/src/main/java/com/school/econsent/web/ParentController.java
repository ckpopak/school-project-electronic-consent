package com.school.econsent.web;

import com.school.econsent.domain.AppUser;
import com.school.econsent.domain.Campaign;
import com.school.econsent.domain.ConsentSubmission;
import com.school.econsent.domain.Student;
import com.school.econsent.repo.CampaignRepository;
import com.school.econsent.repo.ConsentSubmissionRepository;
import com.school.econsent.repo.StudentRepository;
import com.school.econsent.service.ConsentService;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/parent")
public class ParentController {

  private final DemoAuth auth;
  private final StudentRepository students;
  private final CampaignRepository campaigns;
  private final ConsentSubmissionRepository submissions;
  private final ConsentService consentService;

  public ParentController(
      DemoAuth auth,
      StudentRepository students,
      CampaignRepository campaigns,
      ConsentSubmissionRepository submissions,
      ConsentService consentService) {
    this.auth = auth;
    this.students = students;
    this.campaigns = campaigns;
    this.submissions = submissions;
    this.consentService = consentService;
  }

  @GetMapping("/campaigns")
  public List<Map<String, Object>> listCampaigns(@RequestHeader("X-Demo-User-Id") Long userId) {
    auth.requireParent(userId);
    return campaigns.findAllWithSchool().stream().map(this::campaignDto).toList();
  }

  @GetMapping("/students")
  public List<Map<String, Object>> students(@RequestHeader("X-Demo-User-Id") Long userId) {
    AppUser parent = auth.requireParent(userId);
    return students.findByGuardianIdOrderByFullNameAsc(parent.getId()).stream()
        .map(this::studentDto)
        .toList();
  }

  @GetMapping("/campaigns/token/{token}")
  public Map<String, Object> campaignByToken(@PathVariable String token) {
    Campaign campaign =
        campaigns
            .findByQrTokenWithSchool(token)
            .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Invalid QR link."));
    return campaignDto(campaign);
  }

  @GetMapping("/campaigns/{campaignId}/submissions")
  public List<Map<String, Object>> mySubmissions(
      @RequestHeader("X-Demo-User-Id") Long userId, @PathVariable Long campaignId) {
    AppUser parent = auth.requireParent(userId);
    return submissions.findByGuardianAndCampaign(parent.getId(), campaignId).stream()
        .map(this::submissionDto)
        .toList();
  }

  public record SubmitRequest(Long campaignId, Long studentId, String decision, String scopeNote) {}

  @PostMapping("/submissions")
  public Map<String, Object> submit(
      @RequestHeader("X-Demo-User-Id") Long userId, @RequestBody SubmitRequest body) {
    AppUser parent = auth.requireParent(userId);
    ConsentSubmission saved =
        consentService.submit(
            parent, body.campaignId(), body.studentId(), body.decision(), body.scopeNote());
    return submissionDto(saved);
  }

  @DeleteMapping("/submissions/{id}")
  public Map<String, String> withdraw(
      @RequestHeader("X-Demo-User-Id") Long userId, @PathVariable Long id) {
    AppUser parent = auth.requireParent(userId);
    consentService.withdraw(parent, id);
    return Map.of("status", "WITHDRAWN");
  }

  private Map<String, Object> studentDto(Student s) {
    Map<String, Object> m = new HashMap<>();
    m.put("id", s.getId());
    m.put("fullName", s.getFullName());
    m.put("className", s.getClassName());
    m.put("classNumber", s.getClassNumber());
    return m;
  }

  private Map<String, Object> campaignDto(Campaign c) {
    Map<String, Object> m = new HashMap<>();
    m.put("id", c.getId());
    m.put("title", c.getTitle());
    m.put("description", c.getDescription());
    m.put("qrToken", c.getQrToken());
    m.put("startsAt", c.getStartsAt().toString());
    m.put("deadlineAt", c.getDeadlineAt().toString());
    m.put("schoolId", c.getSchool().getId());
    m.put("schoolName", c.getSchool().getName());
    m.put("consentUrlPath", "/c/" + c.getQrToken());
    return m;
  }

  private Map<String, Object> submissionDto(ConsentSubmission s) {
    Map<String, Object> m = new HashMap<>();
    m.put("id", s.getId());
    m.put("campaignId", s.getCampaign().getId());
    m.put("studentId", s.getStudent().getId());
    m.put("studentName", s.getStudent().getFullName());
    m.put("decision", s.getDecision());
    m.put("scopeNote", s.getScopeNote());
    m.put("status", s.getStatus());
    m.put("submittedAt", s.getSubmittedAt().toString());
    return m;
  }
}
