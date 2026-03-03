package br.com.novalearn.platform.api.mappers.lesson;

import br.com.novalearn.platform.api.dtos.lesson.content.LessonContentListResponseDTO;
import br.com.novalearn.platform.api.dtos.lesson.content.LessonContentResponseDTO;
import br.com.novalearn.platform.domain.entities.lesson.LessonContent;
import org.springframework.stereotype.Component;

@Component
public class LessonContentMapper {
    public LessonContentResponseDTO toResponseDTO(LessonContent entity) {
        return new LessonContentResponseDTO(
                entity.getId(),
                entity.isActive(),
                entity.isDeleted(),
                entity.getCreatedAt(),
                entity.getUpdatedAt(),
                entity.getObservations(),
                entity.getLesson() != null ? entity.getLesson().getId() : null,
                entity.getVideoUrl(),
                entity.getTranscriptUrl(),
                entity.getMaterialUrl(),
                entity.getContent(),
                entity.isHasQuiz(),
                entity.isMainContent(),
                entity.getCreatedBy(),
                entity.getUpdatedBy()
        );
    }

    public LessonContentListResponseDTO toListResponseDTO(LessonContent entity) {
        return new LessonContentListResponseDTO(
                entity.getId(),
                entity.getLesson() != null ? entity.getLesson().getId() : null,
                entity.isHasQuiz(),
                entity.isMainContent(),
                entity.isActive(),
                entity.isDeleted()
        );
    }
}