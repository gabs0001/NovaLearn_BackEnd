package br.com.novalearn.platform.factories.dtos.review;

import br.com.novalearn.platform.api.dtos.review.*;
import br.com.novalearn.platform.domain.enums.ReviewStatus;

import java.time.LocalDateTime;

public final class ReviewTestFactory {
    public static ReviewCreateRequestDTO reviewCreateRequest() {
        return new ReviewCreateRequestDTO(
                1L,
                5,
                "Some comment",
                false,
                "Observations"
        );
    }

    public static ReviewUpdateRequestDTO reviewUpdateRequest() {
        return new ReviewUpdateRequestDTO(
                4,
                "New comment",
                false,
                "New observations"
        );
    }

    public static ReviewListResponseDTO reviewListResponse() {
        return new ReviewListResponseDTO(
                8L,
                1L,
                5L,
                5,
                false,
                ReviewStatus.APPROVED,
                true,
                false
        );
    }

    public static ReviewResponseDTO reviewResponse() {
        return new ReviewResponseDTO(
                8L,
                true,
                false,
                LocalDateTime.now().minusDays(3),
                null,
                "Observations",
                1L,
                5L,
                5,
                "Some comment",
                LocalDateTime.now().minusDays(1),
                LocalDateTime.now().minusHours(2),
                false,
                ReviewStatus.APPROVED,
                5L,
                null
        );
    }
}