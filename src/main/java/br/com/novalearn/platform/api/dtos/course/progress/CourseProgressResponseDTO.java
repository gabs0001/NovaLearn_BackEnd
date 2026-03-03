package br.com.novalearn.platform.api.dtos.course.progress;

import br.com.novalearn.platform.domain.enums.EnrollmentStatus;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class CourseProgressResponseDTO {
    private Long id;
    private String name;
    private Integer totalLessons;
    private Integer completedLessons;
    private Integer progressPercent;
    private EnrollmentStatus enrollmentStatus;
    private LocalDateTime enrolledAt;
    private LocalDateTime completedAt;

    public CourseProgressResponseDTO() {}

    public CourseProgressResponseDTO(
            Long id,
            String name,
            Integer totalLessons,
            Integer completedLessons,
            Integer progressPercent,
            EnrollmentStatus enrollmentStatus,
            LocalDateTime enrolledAt,
            LocalDateTime completedAt
    ) {
        this.id = id;
        this.name = name;
        this.totalLessons = totalLessons;
        this.completedLessons = completedLessons;
        this.progressPercent = progressPercent;
        this.enrollmentStatus = enrollmentStatus;
        this.enrolledAt = enrolledAt;
        this.completedAt = completedAt;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public Integer getTotalLessons() { return totalLessons; }
    public Integer getCompletedLessons() { return completedLessons; }
    public Integer getProgressPercent() { return progressPercent; }
    public EnrollmentStatus getEnrollmentStatus() { return enrollmentStatus; }
    public LocalDateTime getEnrolledAt() { return enrolledAt; }
    public LocalDateTime getCompletedAt() { return completedAt; }

    public void setId(Long id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setTotalLessons(Integer totalLessons) { this.totalLessons = totalLessons; }
    public void setCompletedLessons(Integer completedLessons) { this.completedLessons = completedLessons; }
    public void setProgressPercent(Integer progressPercent) { this.progressPercent = progressPercent; }
    public void setEnrollmentStatus(EnrollmentStatus enrollmentStatus) { this.enrollmentStatus = enrollmentStatus; }
    public void setEnrolledAt(LocalDateTime enrolledAt) { this.enrolledAt = enrolledAt; }
    public void setCompletedAt(LocalDateTime completedAt) { this.completedAt = completedAt; }
}