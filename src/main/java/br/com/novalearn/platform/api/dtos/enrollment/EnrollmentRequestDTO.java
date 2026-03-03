package br.com.novalearn.platform.api.dtos.enrollment;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class EnrollmentRequestDTO {
    @NotNull
    private Long courseId;

    private BigDecimal paidAmount;

    private String paymentMethod;

    private String observations;

    public EnrollmentRequestDTO() {}

    public EnrollmentRequestDTO(
            Long courseId,
            BigDecimal paidAmount,
            String paymentMethod,
            String observations
    ) {
        this.courseId = courseId;
        this.paidAmount = paidAmount;
        this.paymentMethod = paymentMethod;
        this.observations = observations;
    }

    public Long getCourseId() { return courseId; }
    public BigDecimal getPaidAmount() { return paidAmount; }
    public String getPaymentMethod() { return paymentMethod; }
    public String getObservations() { return observations; }

    public void setCourseId(Long courseId) { this.courseId = courseId; }
    public void setPaidAmount(BigDecimal paidAmount) { this.paidAmount = paidAmount; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
    public void setObservations(String observations) { this.observations = observations; }
}