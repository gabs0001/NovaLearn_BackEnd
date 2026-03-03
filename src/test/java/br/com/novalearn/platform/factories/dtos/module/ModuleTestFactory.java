package br.com.novalearn.platform.factories.dtos.module;

import br.com.novalearn.platform.api.dtos.module.*;
import br.com.novalearn.platform.api.dtos.module.progress.ModuleProgressResponseDTO;

import java.time.LocalDateTime;

public final class ModuleTestFactory {
    public static ModuleCreateRequestDTO moduleCreateRequest() {
        return new ModuleCreateRequestDTO(
                1L,
                "Backend essentials",
                "Some module description",
                1,
                "Observations"
        );
    }

    public static ModuleUpdateRequestDTO moduleUpdateRequest() {
        return new ModuleUpdateRequestDTO(
                "Backend base",
                "Some new module description",
                1,
                true,
                false,
                "Observations"
        );
    }

    public static ModuleResponseDTO moduleResponse() {
        return new ModuleResponseDTO(
                6L,
                true,
                false,
                LocalDateTime.now().minusDays(2),
                null,
                "Observations",
                1L,
                "Backend essentials",
                "Some module description",
                1,
                5L,
                null
        );
    }

    public static ModuleProgressResponseDTO moduleProgressResponse() {
        return new ModuleProgressResponseDTO(
                6L,
                1L,
                "Backend essentials",
                10,
                5,
                50
        );
    }
}