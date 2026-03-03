package br.com.novalearn.platform.api.mappers.quiz;

import br.com.novalearn.platform.api.dtos.quiz.answeroption.QuizAnswerOptionListResponseDTO;
import br.com.novalearn.platform.api.dtos.quiz.answeroption.QuizAnswerOptionResponseDTO;
import br.com.novalearn.platform.domain.entities.quiz.QuizAnswerOption;
import br.com.novalearn.platform.domain.entities.quiz.QuizQuestion;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static br.com.novalearn.platform.factories.entities.quiz.CreateOptionFactory.createInitializedOption;
import static br.com.novalearn.platform.factories.entities.quiz.CreateQuestionFactory.createInitializedQuestion;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class QuizAnswerOptionMapperTest {
    private LocalDateTime now;
    private QuizAnswerOptionMapper mapper;
    private QuizQuestion question;
    private QuizAnswerOption option;

    @BeforeEach
    void setUp() {
        now = LocalDateTime.now();
        mapper = new QuizAnswerOptionMapper();
        question = createInitializedQuestion();
        option = createInitializedOption(question, now);
    }

    @Test
    void should_map_entity_to_response_dto() {
        option.auditCreate(1L, now);
        option.auditUpdate(2L, now);

        QuizAnswerOptionResponseDTO dto = mapper.toResponseDTO(option);

        assertThat(dto).isNotNull();

        assertThat(dto.getId()).isEqualTo(option.getId());
        assertThat(dto.getActive()).isEqualTo(option.isActive());
        assertThat(dto.getDeleted()).isEqualTo(option.isDeleted());

        assertThat(dto.getCreatedAt()).isEqualTo(option.getCreatedAt());
        assertThat(dto.getUpdatedAt()).isEqualTo(option.getUpdatedAt());
        assertThat(dto.getObservations()).isEqualTo(option.getObservations());

        assertThat(dto.getQuestionId()).isEqualTo(question.getId());

        assertThat(dto.getSequence()).isEqualTo(option.getSequence());
        assertThat(dto.getOption()).isEqualTo(option.getOptionText());
        assertThat(dto.getCorrect()).isEqualTo(option.isCorrect());

        assertThat(dto.getCreatedBy()).isEqualTo(option.getCreatedBy());
        assertThat(dto.getUpdatedBy()).isEqualTo(option.getUpdatedBy());
    }

    @Test
    void should_map_entity_to_list_response_dto() {
        QuizAnswerOptionListResponseDTO dto = mapper.toListResponseDTO(option);

        assertThat(dto).isNotNull();

        assertThat(dto.getId()).isEqualTo(option.getId());
        assertThat(dto.getQuestionId()).isEqualTo(question.getId());

        assertThat(dto.getSequence()).isEqualTo(option.getSequence());
        assertThat(dto.getOption()).isEqualTo(option.getOptionText());
        assertThat(dto.getCorrect()).isEqualTo(option.isCorrect());

        assertThat(dto.getActive()).isEqualTo(option.isActive());
        assertThat(dto.getDeleted()).isEqualTo(option.isDeleted());
    }
}