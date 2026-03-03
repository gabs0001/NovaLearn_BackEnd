package br.com.novalearn.platform.api.dtos.user.course;

import br.com.novalearn.platform.api.dtos.BaseDTO;
import br.com.novalearn.platform.domain.enums.EnrollmentStatus;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserCourseResponseDTO extends BaseDTO {
    private Long userId;
    private Long courseId;
    private LocalDateTime enrolledAt;
    private LocalDateTime completedAt;
    private EnrollmentStatus enrollmentStatus;
    private Integer progressPercent;
    private BigDecimal paidAmount;
    private String paymentMethod;
    private Boolean certificateIssued;
    private Long createdBy;
    private Long updatedBy;

    public UserCourseResponseDTO() {}

    public UserCourseResponseDTO(
            Long id,
            Boolean active,
            Boolean deleted,
            LocalDateTime createdAt,
            LocalDateTime updatedAt,
            String observations,
            Long userId,
            Long courseId,
            LocalDateTime enrolledAt,
            LocalDateTime completedAt,
            EnrollmentStatus enrollmentStatus,
            Integer progressPercent,
            BigDecimal paidAmount,
            String paymentMethod,
            Boolean certificateIssued,
            Long createdBy,
            Long updatedBy
    ) {
        super(id, active, deleted, createdAt, updatedAt, observations);
        this.userId = userId;
        this.courseId = courseId;
        this.enrolledAt = enrolledAt;
        this.completedAt = completedAt;
        this.enrollmentStatus = enrollmentStatus;
        this.progressPercent = progressPercent;
        this.paidAmount = paidAmount;
        this.paymentMethod = paymentMethod;
        this.certificateIssued = certificateIssued;
        this.createdBy = createdBy;
        this.updatedBy = updatedBy;
    }

    public Long getUserId() { return userId; }
    public Long getCourseId() { return courseId; }
    public LocalDateTime getEnrolledAt() { return enrolledAt; }
    public LocalDateTime getCompletedAt() { return completedAt; }
    public EnrollmentStatus getEnrollmentStatus() { return enrollmentStatus; }
    public Integer getProgressPercent() { return progressPercent; }
    public BigDecimal getPaidAmount() { return paidAmount; }
    public String getPaymentMethod() { return paymentMethod; }
    public Boolean getCertificateIssued() { return certificateIssued; }
    public Long getCreatedBy() { return createdBy; }
    public Long getUpdatedBy() { return updatedBy; }

    public void setUserId(Long userId) { this.userId = userId; }
    public void setCourseId(Long courseId) { this.courseId = courseId; }
    public void setEnrolledAt(LocalDateTime enrolledAt) { this.enrolledAt = enrolledAt; }
    public void setCompletedAt(LocalDateTime completedAt) { this.completedAt = completedAt; }
    public void setEnrollmentStatus(EnrollmentStatus enrollmentStatus) { this.enrollmentStatus = enrollmentStatus; }
    public void setProgressPercent(Integer progressPercent) { this.progressPercent = progressPercent; }
    public void setPaidAmount(BigDecimal paidAmount) { this.paidAmount = paidAmount; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
    public void setCertificateIssued(Boolean certificateIssued) { this.certificateIssued = certificateIssued; }
    public void setCreatedBy(Long createdBy) { this.createdBy = createdBy; }
    public void setUpdatedBy(Long updatedBy) { this.updatedBy = updatedBy; }
}