package br.com.novalearn.platform.api.mappers.quiz;

import br.com.novalearn.platform.api.dtos.quiz.QuizCreateRequestDTO;
import br.com.novalearn.platform.api.dtos.quiz.QuizListResponseDTO;
import br.com.novalearn.platform.api.dtos.quiz.QuizResponseDTO;
import br.com.novalearn.platform.domain.entities.module.Module;
import br.com.novalearn.platform.domain.entities.quiz.Quiz;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static br.com.novalearn.platform.factories.dtos.quiz.QuizTestFactory.quizCreateRequest;
import static br.com.novalearn.platform.factories.entities.course.CreateCourseFactory.createInitializedCourse;
import static br.com.novalearn.platform.factories.entities.module.CreateModuleFactory.createInitializedModule;
import static br.com.novalearn.platform.factories.entities.quiz.CreateQuizFactory.createInitializedQuiz;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class QuizMapperTest {
    private QuizMapper mapper;
    private Module module;
    private Quiz quiz;

    @BeforeEach
    void setUp() {
        mapper = new QuizMapper();
        module = createInitializedModule(createInitializedCourse());
        quiz = createInitializedQuiz();
    }

    @Test
    void should_map_create_dto_to_entity() {
        QuizCreateRequestDTO dto = quizCreateRequest();

        Quiz quiz = mapper.toEntity(dto, module);

        assertThat(quiz).isNotNull();

        assertThat(quiz.getModule()).isEqualTo(module);

        assertThat(quiz.getName()).isEqualTo(dto.getName());
        assertThat(quiz.getQtdQuestions()).isEqualTo(dto.getQtdQuestions());
        assertThat(quiz.getMinScore()).isEqualTo(dto.getMinScore());
        assertThat(quiz.getMaxAttempts()).isEqualTo(dto.getMaxAttempts());
        assertThat(quiz.isRandomOrder()).isTrue();

        assertThat(quiz.getDescription()).isEqualTo(dto.getDescription());
        assertThat(quiz.getInstructions()).isEqualTo(dto.getInstructions());
    }

    @Test
    void should_map_entity_to_list_response_dto() {
        QuizListResponseDTO dto = mapper.toListResponseDTO(quiz);

        assertThat(dto).isNotNull();

        assertThat(dto.getId()).isEqualTo(quiz.getId());
        assertThat(dto.getModuleId()).isEqualTo(module.getId());

        assertThat(dto.getName()).isEqualTo(quiz.getName());
        assertThat(dto.getQtdQuestions()).isEqualTo(quiz.getQtdQuestions());

        assertThat(dto.isActive()).isEqualTo(quiz.isActive());
    }

    @Test
    void should_map_entity_to_response_dto() {
        quiz.changeConfiguration(
                null,
                "Advanced Java Quiz",
                "Answer all questions",
                3,
                true
        );

        quiz.auditCreate(1L, LocalDateTime.now());
        quiz.auditUpdate(2L, LocalDateTime.now());

        QuizResponseDTO dto = mapper.toResponseDTO(quiz);

        assertThat(dto).isNotNull();

        assertThat(dto.getId()).isEqualTo(quiz.getId());
        assertThat(dto.getActive()).isEqualTo(quiz.isActive());
        assertThat(dto.getDeleted()).isEqualTo(quiz.isDeleted());

        assertThat(dto.getCreatedAt()).isEqualTo(quiz.getCreatedAt());
        assertThat(dto.getUpdatedAt()).isEqualTo(quiz.getUpdatedAt());
        assertThat(dto.getObservations()).isEqualTo(quiz.getObservations());

        assertThat(dto.getModuleId()).isEqualTo(module.getId());

        assertThat(dto.getName()).isEqualTo(quiz.getName());
        assertThat(dto.getDescription()).isEqualTo(quiz.getDescription());
        assertThat(dto.getInstructions()).isEqualTo(quiz.getInstructions());

        assertThat(dto.getQtdQuestions()).isEqualTo(quiz.getQtdQuestions());
        assertThat(dto.getMinScore()).isEqualTo(quiz.getMinScore());
        assertThat(dto.getMaxAttempts()).isEqualTo(quiz.getMaxAttempts());

        assertThat(dto.isRandomOrder()).isEqualTo(quiz.isRandomOrder());
    }
}