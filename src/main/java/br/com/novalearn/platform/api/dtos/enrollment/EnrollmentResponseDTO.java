package br.com.novalearn.platform.api.dtos.enrollment;

import br.com.novalearn.platform.domain.enums.EnrollmentStatus;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class EnrollmentResponseDTO {
    private Long enrollmentId;
    private Long courseId;
    private String courseName;
    private EnrollmentStatus enrollmentStatus;
    private LocalDateTime enrolledAt;
    private Integer progressPercent;

    public EnrollmentResponseDTO() {}

    public EnrollmentResponseDTO(
            Long enrollmentId,
            Long courseId,
            String courseName,
            EnrollmentStatus enrollmentStatus,
            LocalDateTime enrolledAt,
            Integer progressPercent
    ) {
        this.enrollmentId = enrollmentId;
        this.courseId = courseId;
        this.courseName = courseName;
        this.enrollmentStatus = enrollmentStatus;
        this.enrolledAt = enrolledAt;
        this.progressPercent = progressPercent;
    }

    public Long getEnrollmentId() { return enrollmentId; }
    public Long getCourseId() { return courseId; }
    public String getCourseName() { return courseName; }
    public EnrollmentStatus getEnrollmentStatus() { return enrollmentStatus; }
    public LocalDateTime getEnrolledAt() { return enrolledAt; }
    public Integer getProgressPercent() { return progressPercent; }

    public void setEnrollmentId(Long enrollmentId) { this.enrollmentId = enrollmentId; }
    public void setCourseId(Long courseId) { this.courseId = courseId; }
    public void setCourseName(String courseName) { this.courseName = courseName; }
    public void setEnrollmentStatus(EnrollmentStatus enrollmentStatus) { this.enrollmentStatus = enrollmentStatus; }
    public void setEnrolledAt(LocalDateTime enrolledAt) { this.enrolledAt = enrolledAt; }
    public void setProgressPercent(Integer progressPercent) { this.progressPercent = progressPercent; }
}