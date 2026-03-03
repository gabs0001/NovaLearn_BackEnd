package br.com.novalearn.platform.api.dtos.course.my;

import br.com.novalearn.platform.domain.enums.EnrollmentStatus;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class MyCourseResponseDTO {
    private Long courseId;
    private String name;
    private String shortDescription;
    private EnrollmentStatus enrollmentStatus;
    private LocalDateTime enrolledAt;

    public MyCourseResponseDTO() {}

    public MyCourseResponseDTO(
            Long courseId,
            String name,
            String shortDescription,
            EnrollmentStatus enrollmentStatus,
            LocalDateTime enrolledAt
    ) {
        this.courseId = courseId;
        this.name = name;
        this.shortDescription = shortDescription;
        this.enrollmentStatus = enrollmentStatus;
        this.enrolledAt = enrolledAt;
    }

    public Long getCourseId() { return courseId; }
    public String getName() { return name; }
    public String getShortDescription() { return shortDescription; }
    public EnrollmentStatus getEnrollmentStatus() { return enrollmentStatus; }
    public LocalDateTime getEnrolledAt() { return enrolledAt; }

    public void setCourseId(Long courseId) { this.courseId = courseId; }
    public void setName(String name) { this.name = name; }
    public void setShortDescription(String shortDescription) { this.shortDescription = shortDescription; }
    public void setEnrollmentStatus(EnrollmentStatus enrollmentStatus) { this.enrollmentStatus = enrollmentStatus; }
    public void setEnrolledAt(LocalDateTime enrolledAt) { this.enrolledAt = enrolledAt; }
}