package br.com.novalearn.platform.api.mappers.user;

import br.com.novalearn.platform.api.dtos.user.lessonprogress.UserLessonProgressListResponseDTO;
import br.com.novalearn.platform.api.dtos.user.lessonprogress.UserLessonProgressResponseDTO;
import br.com.novalearn.platform.domain.entities.user.UserLessonProgress;
import org.springframework.stereotype.Component;

@Component
public class UserLessonProgressMapper {
    public UserLessonProgressResponseDTO toResponseDTO(UserLessonProgress entity) {
        return new UserLessonProgressResponseDTO(
                entity.getId(),
                entity.isActive(),
                entity.isDeleted(),
                entity.getCreatedAt(),
                entity.getUpdatedAt(),
                entity.getObservations(),
                entity.getUser() != null ? entity.getUser().getId() : null,
                entity.getLesson() != null ? entity.getLesson().getId() : null,
                entity.isCompleted(),
                entity.getCompletedAt(),
                entity.getProgressPercent(),
                entity.getViews(),
                entity.getFirstViewAt(),
                entity.getLastViewAt(),
                entity.getCreatedBy(),
                entity.getUpdatedBy()
        );
    }

    public UserLessonProgressListResponseDTO toListResponseDTO(UserLessonProgress entity) {
        return new UserLessonProgressListResponseDTO(
                entity.getId(),
                entity.getUser() != null ? entity.getUser().getId() : null,
                entity.getLesson() != null ? entity.getLesson().getId() : null,
                entity.isCompleted(),
                entity.getProgressPercent(),
                entity.getViews(),
                entity.isActive(),
                entity.isDeleted()
       );
    }
}