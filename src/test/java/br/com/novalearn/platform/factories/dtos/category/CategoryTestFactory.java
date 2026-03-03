package br.com.novalearn.platform.factories.dtos.category;

import br.com.novalearn.platform.api.dtos.category.*;

import java.time.LocalDateTime;

public final class CategoryTestFactory {
    public static CategoryCreateRequestDTO categoryCreateRequest() {
        return new CategoryCreateRequestDTO(
                10L,
                "Backend",
                "BCK",
                "Backend Development",
                "Observations"
        );
    }

    public static CategoryUpdateRequestDTO categoryUpdateRequest() {
        return new CategoryUpdateRequestDTO(
                10L,
                "Back-end",
                "BE",
                "Backend Development Category",
                "Observations"
        );
    }

    public static CategoryListResponseDTO categoryListResponse() {
        return new CategoryListResponseDTO(
                10L,
                10L,
                "Backend",
                "BCK",
                true,
                false
        );
    }

    public static CategoryResponseDTO categoryResponse() {
        return new CategoryResponseDTO(
                10L,
                true,
                false,
                LocalDateTime.now().minusDays(5),
                null,
                "Observations",
                "Backend",
                "Backend Development",
                "BCK",
                10L
        );
    }
}