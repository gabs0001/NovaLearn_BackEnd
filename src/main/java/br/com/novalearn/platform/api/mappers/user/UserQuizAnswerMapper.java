package br.com.novalearn.platform.api.mappers.user;

import br.com.novalearn.platform.api.dtos.user.quizanswer.UserQuizAnswerListResponseDTO;
import br.com.novalearn.platform.api.dtos.user.quizanswer.UserQuizAnswerResponseDTO;
import br.com.novalearn.platform.domain.entities.user.UserQuizAnswer;
import org.springframework.stereotype.Component;

@Component
public class UserQuizAnswerMapper {
    public UserQuizAnswerResponseDTO toResponseDTO(UserQuizAnswer entity) {
        return new UserQuizAnswerResponseDTO(
                entity.getId(),
                entity.isActive(),
                entity.isDeleted(),
                entity.getCreatedAt(),
                entity.getUpdatedAt(),
                entity.getObservations(),
                entity.getUser() != null ? entity.getUser().getId() : null,
                entity.getQuizQuestion() != null ? entity.getQuizQuestion().getId() : null,
                entity.getQuizAnswerOption() != null ? entity.getQuizAnswerOption().getId() : null,
                entity.isCorrect(),
                entity.getAnsweredAt(),
                entity.getCreatedBy(),
                entity.getUpdatedBy()
        );
    }

    public UserQuizAnswerListResponseDTO toListResponseDTO(UserQuizAnswer entity) {
        return new UserQuizAnswerListResponseDTO(
                entity.getId(),
                entity.getUser() != null ? entity.getUser().getId() : null,
                entity.getQuizQuestion() != null ? entity.getQuizQuestion().getId() : null,
                entity.getQuizAnswerOption() != null ? entity.getQuizAnswerOption().getId() : null,
                entity.isCorrect(),
                entity.getAnsweredAt(),
                entity.isActive(),
                entity.isDeleted()
        );
    }
}