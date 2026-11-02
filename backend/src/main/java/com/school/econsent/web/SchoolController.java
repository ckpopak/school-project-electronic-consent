package com.school.econsent.web;

import com.school.econsent.domain.AppUser;
import com.school.econsent.domain.Campaign;
import com.school.econsent.domain.ConsentSubmission;
import com.school.econsent.repo.CampaignRepository;
import com.school.econsent.repo.ConsentSubmissionRepository;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/school")
public class SchoolController {

  private final DemoAuth auth;
  private final CampaignRepository campaigns;
  private final ConsentSubmissionRepository submissions;

  public SchoolController(
      DemoAuth auth, CampaignRepository campaigns, ConsentSubmissionRepository submissions) {
    this.auth = auth;
    this.campaigns = campaigns;
    this.submissions = submissions;
  }

  @GetMapping("/campaigns")
  public List<Map<String, Object>> campaigns(@RequestHeader("X-Demo-User-Id") Long userId) {
    AppUser staff = auth.requireSchoolStaff(userId);
    return campaigns.findBySchoolIdWithSchool(staff.getSchool().getId()).stream()
        .map(this::campaignDto)
        .toList();
  }

  @GetMapping("/campaigns/{id}/submissions")
  public List<Map<String, Object>> submissions(
      @RequestHeader("X-Demo-User-Id") Long userId, @PathVariable Long id) {
    AppUser staff = auth.requireSchoolStaff(userId);
    Campaign campaign = requireSchoolCampaign(staff, id);
    return submissions.findSubmittedForCampaign(campaign.getId()).stream()
        .map(this::submissionDto)
        .toList();
  }

  @GetMapping("/campaigns/{id}/export.csv")
  public ResponseEntity<byte[]> exportCsv(
      @RequestHeader("X-Demo-User-Id") Long userId, @PathVariable Long id) {
    AppUser staff = auth.requireSchoolStaff(userId);
    Campaign campaign = requireSchoolCampaign(staff, id);
    List<ConsentSubmission> rows = submissions.findSubmittedForCampaign(campaign.getId());

    StringBuilder csv = new StringBuilder();
    csv.append("student_name,class,class_number,decision,scope_note,submitted_at\n");
    for (ConsentSubmission s : rows) {
      csv.append(escape(s.getStudent().getFullName())).append(',');
      csv.append(escape(s.getStudent().getClassName())).append(',');
      csv.append(escape(s.getStudent().getClassNumber())).append(',');
      csv.append(s.getDecision()).append(',');
      csv.append(escape(s.getScopeNote())).append(',');
      csv.append(s.getSubmittedAt()).append('\n');
    }

    byte[] bytes = csv.toString().getBytes(StandardCharsets.UTF_8);
    return ResponseEntity.ok()
        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"consented-students.csv\"")
        .contentType(new MediaType("text", "csv", StandardCharsets.UTF_8))
        .body(bytes);
  }

  private Campaign requireSchoolCampaign(AppUser staff, Long campaignId) {
    Campaign campaign =
        campaigns
            .findByIdWithSchool(campaignId)
            .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Campaign not found."));
    if (!campaign.getSchool().getId().equals(staff.getSchool().getId())) {
      throw new ApiException(HttpStatus.FORBIDDEN, "Campaign belongs to another school.");
    }
    return campaign;
  }

  private String escape(String value) {
    if (value == null) {
      return "";
    }
    if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
      return "\"" + value.replace("\"", "\"\"") + "\"";
    }
    return value;
  }

  private Map<String, Object> campaignDto(Campaign c) {
    Map<String, Object> m = new HashMap<>();
    m.put("id", c.getId());
    m.put("title", c.getTitle());
    m.put("description", c.getDescription());
    m.put("qrToken", c.getQrToken());
    m.put("startsAt", c.getStartsAt().toString());
    m.put("deadlineAt", c.getDeadlineAt().toString());
    m.put("schoolName", c.getSchool().getName());
    m.put(
        "consentUrlPath",
        "/c/" + c.getQrToken());
    return m;
  }

  private Map<String, Object> submissionDto(ConsentSubmission s) {
    Map<String, Object> m = new HashMap<>();
    m.put("id", s.getId());
    m.put("studentName", s.getStudent().getFullName());
    m.put("className", s.getStudent().getClassName());
    m.put("classNumber", s.getStudent().getClassNumber());
    m.put("decision", s.getDecision());
    m.put("scopeNote", s.getScopeNote());
    m.put("submittedAt", s.getSubmittedAt().toString());
    return m;
  }
}
