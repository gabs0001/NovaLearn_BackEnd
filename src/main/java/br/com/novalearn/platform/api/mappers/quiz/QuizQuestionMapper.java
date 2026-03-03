package br.com.novalearn.platform.api.mappers.quiz;

import br.com.novalearn.platform.api.dtos.quiz.question.QuizQuestionListResponseDTO;
import br.com.novalearn.platform.api.dtos.quiz.question.QuizQuestionResponseDTO;
import br.com.novalearn.platform.domain.entities.quiz.QuizQuestion;
import org.springframework.stereotype.Component;

@Component
public class QuizQuestionMapper {
    public QuizQuestionResponseDTO toResponseDTO(QuizQuestion entity) {
        return new QuizQuestionResponseDTO(
                entity.getId(),
                entity.isActive(),
                entity.isDeleted(),
                entity.getCreatedAt(),
                entity.getUpdatedAt(),
                entity.getObservations(),
                entity.getQuiz() != null ? entity.getQuiz().getId() : null,
                entity.getSequence(),
                entity.getDescription(),
                entity.getPoints(),
                entity.getCreatedBy(),
                entity.getUpdatedBy()
        );
    }

    public QuizQuestionListResponseDTO toListResponseDTO(QuizQuestion entity) {
        return new QuizQuestionListResponseDTO(
                entity.getId(),
                entity.getQuiz() != null ? entity.getQuiz().getId() : null,
                entity.getSequence(),
                entity.getDescription(),
                entity.getPoints(),
                entity.isActive(),
                entity.isDeleted()
        );
    }
}