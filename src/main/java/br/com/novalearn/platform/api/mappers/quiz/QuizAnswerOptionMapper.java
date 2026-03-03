package br.com.novalearn.platform.api.mappers.quiz;

import br.com.novalearn.platform.api.dtos.quiz.answeroption.QuizAnswerOptionListResponseDTO;
import br.com.novalearn.platform.api.dtos.quiz.answeroption.QuizAnswerOptionResponseDTO;
import br.com.novalearn.platform.domain.entities.quiz.QuizAnswerOption;
import org.springframework.stereotype.Component;

@Component
public class QuizAnswerOptionMapper {
    public QuizAnswerOptionResponseDTO toResponseDTO(QuizAnswerOption entity) {
        return new QuizAnswerOptionResponseDTO(
                entity.getId(),
                entity.isActive(),
                entity.isDeleted(),
                entity.getCreatedAt(),
                entity.getUpdatedAt(),
                entity.getObservations(),
                entity.getQuizQuestion().getId(),
                entity.getSequence(),
                entity.getOptionText(),
                entity.isCorrect(),
                entity.getCreatedBy(),
                entity.getUpdatedBy()
        );
    }

    public QuizAnswerOptionListResponseDTO toListResponseDTO(QuizAnswerOption entity) {
        return new QuizAnswerOptionListResponseDTO(
                entity.getId(),
                entity.getQuizQuestion().getId(),
                entity.getSequence(),
                entity.getOptionText(),
                entity.isCorrect(),
                entity.isActive(),
                entity.isDeleted()
        );
    }
}