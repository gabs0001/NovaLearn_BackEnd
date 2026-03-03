package br.com.novalearn.platform.api.mappers.user;

import br.com.novalearn.platform.api.dtos.user.quizanswer.UserQuizAnswerListResponseDTO;
import br.com.novalearn.platform.api.dtos.user.quizanswer.UserQuizAnswerResponseDTO;
import br.com.novalearn.platform.domain.entities.quiz.QuizAnswerOption;
import br.com.novalearn.platform.domain.entities.quiz.QuizQuestion;
import br.com.novalearn.platform.domain.entities.user.User;
import br.com.novalearn.platform.domain.entities.user.UserQuizAnswer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;

import static br.com.novalearn.platform.factories.entities.quiz.CreateOptionFactory.createInitializedOption;
import static br.com.novalearn.platform.factories.entities.quiz.CreateQuestionFactory.createInitializedQuestion;
import static br.com.novalearn.platform.factories.entities.user.CreateAnswerFactory.createInitializedAnswer;
import static br.com.novalearn.platform.factories.entities.user.CreateUserFactory.createInitializedUser;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class UserQuizAnswerMapperTest {
    private LocalDateTime now;
    private UserQuizAnswerMapper mapper;
    private User user;
    private QuizQuestion question;
    private QuizAnswerOption option;
    private UserQuizAnswer answer;

    @BeforeEach
    void setUp() {
        now = LocalDateTime.now();
        mapper = new UserQuizAnswerMapper();

        user = createInitializedUser();
        ReflectionTestUtils.setField(user, "id", 5L);

        question = createInitializedQuestion();
        ReflectionTestUtils.setField(question, "id", 20L);

        option = createInitializedOption(question, now);
        ReflectionTestUtils.setField(option, "id", 15L);

        ReflectionTestUtils.setField(option, "quizQuestion", question);

        answer = createInitializedAnswer(user, question, option, now);
    }

    @Test
    void should_map_entity_to_response_dto() {
        answer.auditCreate(5L, now.minusMinutes(5));
        answer.auditUpdate(5L, now.minusMinutes(1));

        UserQuizAnswerResponseDTO dto = mapper.toResponseDTO(answer);

        assertThat(dto).isNotNull();

        assertThat(dto.getId()).isEqualTo(answer.getId());
        assertThat(dto.getActive()).isEqualTo(answer.isActive());
        assertThat(dto.getDeleted()).isEqualTo(answer.isDeleted());

        assertThat(dto.getCreatedAt()).isEqualTo(answer.getCreatedAt());
        assertThat(dto.getUpdatedAt()).isEqualTo(answer.getUpdatedAt());
        assertThat(dto.getObservations()).isEqualTo(answer.getObservations());

        assertThat(dto.getUserId()).isEqualTo(user.getId());
        assertThat(dto.getQuizQuestionId()).isEqualTo(question.getId());
        assertThat(dto.getQuizAnswerOptionId()).isEqualTo(option.getId());

        assertThat(dto.getCorrect()).isEqualTo(answer.isCorrect());
        assertThat(dto.getAnsweredAt()).isEqualTo(answer.getAnsweredAt());

        assertThat(dto.getCreatedBy()).isEqualTo(answer.getCreatedBy());
        assertThat(dto.getUpdatedBy()).isEqualTo(answer.getUpdatedBy());
    }

    @Test
    void should_map_entity_to_list_response_dto() {
        UserQuizAnswerListResponseDTO dto = mapper.toListResponseDTO(answer);

        assertThat(dto).isNotNull();

        assertThat(dto.getId()).isEqualTo(answer.getId());

        assertThat(dto.getUserId()).isEqualTo(user.getId());
        assertThat(dto.getQuizQuestionId()).isEqualTo(question.getId());
        assertThat(dto.getQuizAnswerOptionId()).isEqualTo(option.getId());

        assertThat(dto.getCorrect()).isEqualTo(answer.isCorrect());
        assertThat(dto.getAnsweredAt()).isEqualTo(answer.getAnsweredAt());

        assertThat(dto.getActive()).isEqualTo(answer.isActive());
        assertThat(dto.getDeleted()).isEqualTo(answer.isDeleted());
    }
}