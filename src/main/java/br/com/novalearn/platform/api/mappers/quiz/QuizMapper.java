package br.com.novalearn.platform.api.mappers.quiz;

import br.com.novalearn.platform.api.dtos.quiz.QuizCreateRequestDTO;
import br.com.novalearn.platform.api.dtos.quiz.QuizListResponseDTO;
import br.com.novalearn.platform.api.dtos.quiz.QuizResponseDTO;
import br.com.novalearn.platform.domain.entities.quiz.Quiz;
import br.com.novalearn.platform.domain.entities.module.Module;
import org.springframework.stereotype.Component;

@Component
public class QuizMapper {
    public Quiz toEntity(QuizCreateRequestDTO dto, Module module) {
        Quiz quiz = Quiz.create(
                module,
                dto.getName(),
                dto.getQtdQuestions(),
                dto.getMinScore(),
                dto.getMaxAttempts(),
                Boolean.TRUE.equals(dto.getRandomOrder())
        );

        quiz.changeConfiguration(
                null,
                dto.getDescription(),
                dto.getInstructions(),
                3,
                Boolean.TRUE.equals(dto.getRandomOrder())
        );

        return quiz;
    }

    public QuizListResponseDTO toListResponseDTO(Quiz entity) {
        return new QuizListResponseDTO(
                entity.getId(),
                entity.getModule().getId(),
                entity.getName(),
                entity.getQtdQuestions(),
                entity.isActive()
        );
    }

    public QuizResponseDTO toResponseDTO(Quiz entity) {
        return new QuizResponseDTO(
                entity.getId(),
                entity.isActive(),
                entity.isDeleted(),
                entity.getCreatedAt(),
                entity.getUpdatedAt(),
                entity.getObservations(),
                entity.getModule().getId(),
                entity.getName(),
                entity.getDescription(),
                entity.getInstructions(),
                entity.getQtdQuestions(),
                entity.getMinScore(),
                entity.getMaxAttempts(),
                entity.isRandomOrder(),
                entity.getCreatedBy(),
                entity.getUpdatedBy()
        );
    }
}