package br.com.novalearn.platform.api.mappers.lesson;

import br.com.novalearn.platform.api.dtos.lesson.LessonCreateRequestDTO;
import br.com.novalearn.platform.api.dtos.lesson.LessonListResponseDTO;
import br.com.novalearn.platform.api.dtos.lesson.LessonResponseDTO;
import br.com.novalearn.platform.domain.entities.lesson.Lesson;
import br.com.novalearn.platform.domain.entities.module.Module;
import br.com.novalearn.platform.infra.time.provider.TimeProvider;
import org.springframework.stereotype.Component;

@Component
public class LessonMapper {
    public Lesson toEntity(LessonCreateRequestDTO dto, Module module) {
        return Lesson.create(
                module,
                dto.getName(),
                dto.getDescription(),
                dto.getSequence(),
                dto.getDurationSeconds(),
                dto.getRequireCompletion(),
                dto.getVisible(),
                dto.getPreviewUrl(),
                dto.getNotes(),
                dto.getObservations()
        );
    }

    public LessonResponseDTO toResponseDTO(Lesson entity) {
        return new LessonResponseDTO(
                entity.getId(),
                entity.isActive(),
                entity.isDeleted(),
                entity.getCreatedAt(),
                entity.getUpdatedAt(),
                entity.getObservations(),
                entity.getModule() != null ? entity.getModule().getId() : null,
                entity.getName(),
                entity.getDescription(),
                entity.getSequence(),
                entity.getDurationSeconds(),
                entity.isRequireCompletion(),
                entity.isVisible(),
                entity.getPreviewUrl(),
                entity.getNotes(),
                entity.getCreatedBy(),
                entity.getUpdatedBy()
        );
    }

    public LessonListResponseDTO toListResponseDTO(Lesson entity) {
        return new LessonListResponseDTO(
                entity.getId(),
                entity.getModule() != null ? entity.getModule().getId() : null,
                entity.getName(),
                entity.getSequence(),
                entity.getDurationSeconds(),
                entity.isVisible(),
                entity.isActive(),
                entity.isDeleted()
        );
    }
}