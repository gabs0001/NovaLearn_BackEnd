package br.com.novalearn.platform.factories.dtos.enrollment;

import br.com.novalearn.platform.api.dtos.enrollment.history.EnrollmentHistoryResponseDTO;
import br.com.novalearn.platform.domain.enums.EnrollmentStatus;

import java.time.LocalDateTime;

public final class EnrollmentHistoryTestFactory {
    public static EnrollmentHistoryResponseDTO historyResponse() {
        return new EnrollmentHistoryResponseDTO(
                1L,
                "Java Fundamentals",
                "Java course for beginners",
                EnrollmentStatus.IN_PROGRESS,
                LocalDateTime.now().minusDays(10),
                null,
                5,
                false
        );
    }
}