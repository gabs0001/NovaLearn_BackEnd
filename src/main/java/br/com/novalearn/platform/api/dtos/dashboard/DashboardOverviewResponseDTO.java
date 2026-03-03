package br.com.novalearn.platform.api.dtos.dashboard;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.math.BigDecimal;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class DashboardOverviewResponseDTO {
    private Long totalEnrollments;
    private Long enrolledCourses;
    private Long inProgressCourses;
    private Long completedCourses;
    private Long cancelledCourses;
    private BigDecimal averageProgress;
    private Long certificatesIssued;

    public DashboardOverviewResponseDTO() {}

    public DashboardOverviewResponseDTO(
            Long totalEnrollments,
            Long enrolledCourses,
            Long inProgressCourses,
            Long completedCourses,
            Long cancelledCourses,
            BigDecimal averageProgress,
            Long certificatesIssued
    ) {
        this.totalEnrollments = totalEnrollments;
        this.enrolledCourses = enrolledCourses;
        this.inProgressCourses = inProgressCourses;
        this.completedCourses = completedCourses;
        this.cancelledCourses = cancelledCourses;
        this.averageProgress = averageProgress;
        this.certificatesIssued = certificatesIssued;
    }

    public Long getTotalEnrollments() { return totalEnrollments; }
    public Long getEnrolledCourses() { return enrolledCourses; }
    public Long getInProgressCourses() { return inProgressCourses; }
    public Long getCompletedCourses() { return completedCourses; }
    public Long getCancelledCourses() { return cancelledCourses; }
    public BigDecimal getAverageProgress() { return averageProgress; }
    public Long getCertificatesIssued() { return certificatesIssued; }
}