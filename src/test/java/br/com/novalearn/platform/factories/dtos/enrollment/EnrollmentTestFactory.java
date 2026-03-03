package br.com.novalearn.platform.factories.dtos.enrollment;

import br.com.novalearn.platform.api.dtos.enrollment.EnrollmentRequestDTO;
import br.com.novalearn.platform.api.dtos.enrollment.EnrollmentResponseDTO;
import br.com.novalearn.platform.api.dtos.enrollment.UpdateProgressRequestDTO;
import br.com.novalearn.platform.domain.enums.EnrollmentStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public final class EnrollmentTestFactory {
    public static EnrollmentRequestDTO enrollmentRequest() {
        return new EnrollmentRequestDTO(
                1L,
                new BigDecimal("99.9"),
                "Credit card",
                "Observations"
        );
    }

    public static UpdateProgressRequestDTO updateProgressRequest() {
        return new UpdateProgressRequestDTO(25);
    }

    public static EnrollmentResponseDTO enrollmentResponse() {
        return new EnrollmentResponseDTO(
                8L,
                1L,
                "Java Fundamentals",
                EnrollmentStatus.IN_PROGRESS,
                LocalDateTime.now().minusDays(10),
                5
        );
    }
}