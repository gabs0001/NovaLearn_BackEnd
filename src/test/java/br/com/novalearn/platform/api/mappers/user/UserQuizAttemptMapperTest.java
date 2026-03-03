package br.com.novalearn.platform.api.mappers.user;

import br.com.novalearn.platform.api.dtos.user.quizattempt.UserQuizAttemptListResponseDTO;
import br.com.novalearn.platform.api.dtos.user.quizattempt.UserQuizAttemptResponseDTO;
import br.com.novalearn.platform.domain.entities.quiz.Quiz;
import br.com.novalearn.platform.domain.entities.user.UserQuizAttempt;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static br.com.novalearn.platform.factories.entities.quiz.CreateQuizFactory.createInitializedQuiz;
import static br.com.novalearn.platform.factories.entities.user.CreateAttemptFactory.createInitializedAttempt;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class UserQuizAttemptMapperTest {
    private LocalDateTime now;
    private UserQuizAttemptMapper mapper;
    private Quiz quiz;
    private UserQuizAttempt attempt;

    @BeforeEach
    void setUp() {
        now = LocalDateTime.now();
        mapper = new UserQuizAttemptMapper();
        quiz = createInitializedQuiz();
        attempt = createInitializedAttempt();
    }

    @Test
    void should_map_entity_to_response_dto() {
        LocalDateTime startedAt = now.minusMinutes(10);

        attempt.finish(
                new BigDecimal("8"),
                new BigDecimal("10"),
                now,
                1L,
                now
        );

        attempt.auditCreate(5L, startedAt.minusMinutes(5));
        attempt.auditUpdate(5L, now.minusMinutes(1));

        UserQuizAttemptResponseDTO dto = mapper.toResponseDTO(attempt);

        assertThat(dto).isNotNull();

        assertThat(dto.getId()).isEqualTo(attempt.getId());
        assertThat(dto.getActive()).isEqualTo(attempt.isActive());
        assertThat(dto.getDeleted()).isEqualTo(attempt.isDeleted());

        assertThat(dto.getCreatedAt()).isEqualTo(attempt.getCreatedAt());
        assertThat(dto.getUpdatedAt()).isEqualTo(attempt.getUpdatedAt());
        assertThat(dto.getObservations()).isEqualTo(attempt.getObservations());

        assertThat(dto.getQuizId()).isEqualTo(quiz.getId());

        assertThat(dto.getSeqAttempt()).isEqualTo(attempt.getSeqAttempt());

        assertThat(dto.getScore()).isEqualTo(attempt.getScore());
        assertThat(dto.getMaxScore()).isEqualTo(attempt.getMaxScore());

        assertThat(dto.getStatus()).isEqualTo(attempt.getStatus());

        assertThat(dto.isPassed()).isEqualTo(attempt.isPassed());

        assertThat(dto.getStartedAt()).isEqualTo(attempt.getStartedAt());
        assertThat(dto.getFinishedAt()).isEqualTo(attempt.getFinishedAt());
    }

    @Test
    void should_map_entity_to_list_response_dto() {
        attempt.finish(
                new BigDecimal("6"),
                new BigDecimal("10"),
                now,
                5L,
                now
        );

        UserQuizAttemptListResponseDTO dto = mapper.toListResponseDTO(attempt);

        assertThat(dto).isNotNull();

        assertThat(dto.getId()).isEqualTo(attempt.getId());
        assertThat(dto.getQuizId()).isEqualTo(quiz.getId());
        assertThat(dto.getSeqAttempt()).isEqualTo(attempt.getSeqAttempt());
        assertThat(dto.getScore()).isEqualTo(attempt.getScore());
        assertThat(dto.getPassed()).isEqualTo(attempt.isPassed());
        assertThat(dto.getStatus()).isEqualTo(attempt.getStatus());
        assertThat(dto.getFinishedAt()).isEqualTo(attempt.getFinishedAt());
    }
}