package br.com.novalearn.platform.api.mappers.user;

import br.com.novalearn.platform.api.dtos.user.quizattempt.UserQuizAttemptListResponseDTO;
import br.com.novalearn.platform.api.dtos.user.quizattempt.UserQuizAttemptResponseDTO;
import br.com.novalearn.platform.domain.entities.user.UserQuizAttempt;
import org.springframework.stereotype.Component;

@Component
public class UserQuizAttemptMapper {
    public UserQuizAttemptResponseDTO toResponseDTO(UserQuizAttempt entity) {
        return new UserQuizAttemptResponseDTO(
                entity.getId(),
                entity.isActive(),
                entity.isDeleted(),
                entity.getCreatedAt(),
                entity.getUpdatedAt(),
                entity.getObservations(),
                entity.getQuiz() != null ? entity.getQuiz().getId() : null,
                entity.getSeqAttempt(),
                entity.getScore(),
                entity.getMaxScore(),
                entity.getStatus(),
                entity.isPassed(),
                entity.getStartedAt(),
                entity.getFinishedAt()
        );
    }

    public UserQuizAttemptListResponseDTO toListResponseDTO(UserQuizAttempt entity) {
        return new UserQuizAttemptListResponseDTO(
                entity.getId(),
                entity.getQuiz() != null ? entity.getQuiz().getId() : null,
                entity.getSeqAttempt(),
                entity.getScore(),
                entity.isPassed(),
                entity.getStatus(),
                entity.getFinishedAt()
        );
    }
}