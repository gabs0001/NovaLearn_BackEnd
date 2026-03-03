package br.com.novalearn.platform.api.dtos.enrollment.history;

import br.com.novalearn.platform.domain.enums.EnrollmentStatus;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class EnrollmentHistoryResponseDTO {
    private Long courseId;
    private String courseName;
    private String shortDescription;
    private EnrollmentStatus enrollmentStatus;
    private LocalDateTime enrolledAt;
    private LocalDateTime completedAt;
    private Integer progressPercent;
    private Boolean certificateIssued;

    public EnrollmentHistoryResponseDTO() {}

    public EnrollmentHistoryResponseDTO(
            Long courseId,
            String courseName,
            String shortDescription,
            EnrollmentStatus enrollmentStatus,
            LocalDateTime enrolledAt,
            LocalDateTime completedAt,
            Integer progressPercent,
            Boolean certificateIssued
    ) {
        this.courseId = courseId;
        this.courseName = courseName;
        this.shortDescription = shortDescription;
        this.enrollmentStatus = enrollmentStatus;
        this.enrolledAt = enrolledAt;
        this.completedAt = completedAt;
        this.progressPercent = progressPercent;
        this.certificateIssued = certificateIssued;
    }

    public Long getCourseId() { return courseId; }
    public String getCourseName() { return courseName; }
    public String getShortDescription() { return shortDescription; }
    public EnrollmentStatus getEnrollmentStatus() { return enrollmentStatus; }
    public LocalDateTime getEnrolledAt() { return enrolledAt; }
    public LocalDateTime getCompletedAt() { return completedAt; }
    public Integer getProgressPercent() { return progressPercent; }
    public Boolean getCertificateIssued() { return certificateIssued; }

    public void setCourseId(Long courseId) { this.courseId = courseId; }
    public void setCourseName(String courseName) { this.courseName = courseName; }
    public void setShortDescription(String shortDescription) { this.shortDescription = shortDescription; }
    public void setEnrollmentStatus(EnrollmentStatus enrollmentStatus) { this.enrollmentStatus = enrollmentStatus; }
    public void setEnrolledAt(LocalDateTime enrolledAt) { this.enrolledAt = enrolledAt; }
    public void setCompletedAt(LocalDateTime completedAt) { this.completedAt = completedAt; }
    public void setProgressPercent(Integer progressPercent) { this.progressPercent = progressPercent; }
    public void setCertificateIssued(Boolean certificateIssued) { this.certificateIssued = certificateIssued; }
}