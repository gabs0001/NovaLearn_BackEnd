package br.com.novalearn.platform.api.mappers.quiz;

import br.com.novalearn.platform.api.dtos.quiz.question.QuizQuestionListResponseDTO;
import br.com.novalearn.platform.api.dtos.quiz.question.QuizQuestionResponseDTO;
import br.com.novalearn.platform.domain.entities.quiz.Quiz;
import br.com.novalearn.platform.domain.entities.quiz.QuizQuestion;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static br.com.novalearn.platform.factories.entities.quiz.CreateQuestionFactory.createInitializedQuestion;
import static br.com.novalearn.platform.factories.entities.quiz.CreateQuizFactory.createInitializedQuiz;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class QuizQuestionMapperTest {
    private QuizQuestionMapper mapper;
    private Quiz quiz;
    private QuizQuestion question;

    @BeforeEach
    void setUp() {
        mapper = new QuizQuestionMapper();
        quiz = createInitializedQuiz();
        question = createInitializedQuestion();
    }

    @Test
    void should_map_entity_to_response_dto() {
        question.auditCreate(1L, LocalDateTime.now());
        question.auditUpdate(2L, LocalDateTime.now());

        QuizQuestionResponseDTO dto = mapper.toResponseDTO(question);

        assertThat(dto).isNotNull();

        assertThat(dto.getId()).isEqualTo(question.getId());
        assertThat(dto.getActive()).isEqualTo(question.isActive());
        assertThat(dto.getDeleted()).isEqualTo(question.isDeleted());

        assertThat(dto.getCreatedAt()).isEqualTo(question.getCreatedAt());
        assertThat(dto.getUpdatedAt()).isEqualTo(question.getUpdatedAt());
        assertThat(dto.getObservations()).isEqualTo(question.getObservations());

        assertThat(dto.getQuizId()).isEqualTo(quiz.getId());

        assertThat(dto.getSequence()).isEqualTo(question.getSequence());
        assertThat(dto.getDescription()).isEqualTo(question.getDescription());
        assertThat(dto.getPoints()).isEqualTo(question.getPoints());

        assertThat(dto.getCreatedBy()).isEqualTo(question.getCreatedBy());
        assertThat(dto.getUpdatedBy()).isEqualTo(question.getUpdatedBy());
    }

    @Test
    void should_map_entity_to_list_response_dto() {
        QuizQuestionListResponseDTO dto = mapper.toListResponseDTO(question);

        assertThat(dto).isNotNull();

        assertThat(dto.getId()).isEqualTo(question.getId());
        assertThat(dto.getQuizId()).isEqualTo(quiz.getId());

        assertThat(dto.getSequence()).isEqualTo(question.getSequence());
        assertThat(dto.getDescription()).isEqualTo(question.getDescription());
        assertThat(dto.getPoints()).isEqualTo(question.getPoints());

        assertThat(dto.getActive()).isEqualTo(question.isActive());
        assertThat(dto.getDeleted()).isEqualTo(question.isDeleted());
    }
}