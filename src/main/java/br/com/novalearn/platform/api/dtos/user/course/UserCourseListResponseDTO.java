package br.com.novalearn.platform.api.dtos.user.course;

import br.com.novalearn.platform.domain.enums.EnrollmentStatus;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserCourseListResponseDTO {
    private Long id;
    private Long userId;
    private Long courseId;
    private EnrollmentStatus enrollmentStatus;
    private Integer progressPercent;
    private Boolean active;
    private Boolean deleted;

    public UserCourseListResponseDTO() {}

    public UserCourseListResponseDTO(
            Long id,
            Long userId,
            Long courseId,
            EnrollmentStatus enrollmentStatus,
            Integer progressPercent,
            Boolean active,
            Boolean deleted
    ) {
        this.id = id;
        this.userId = userId;
        this.courseId = courseId;
        this.enrollmentStatus = enrollmentStatus;
        this.progressPercent = progressPercent;
        this.active = active;
        this.deleted = deleted;
    }

    public Long getId() { return id; }
    public Long getUserId() { return userId; }
    public Long getCourseId() { return courseId; }
    public EnrollmentStatus getEnrollmentStatus() { return enrollmentStatus; }
    public Integer getProgressPercent() { return progressPercent; }
    public Boolean getActive() { return active; }
    public Boolean getDeleted() { return deleted; }

    public void setId(Long id) { this.id = id; }
    public void setUserId(Long userId) { this.userId = userId; }
    public void setCourseId(Long courseId) { this.courseId = courseId; }
    public void setEnrollmentStatus(EnrollmentStatus enrollmentStatus) { this.enrollmentStatus = enrollmentStatus; }
    public void setProgressPercent(Integer progressPercent) { this.progressPercent = progressPercent; }
    public void setActive(Boolean active) { this.active = active; }
    public void setDeleted(Boolean deleted) { this.deleted = deleted; }
}