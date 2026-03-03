package br.com.novalearn.platform.domain.entities.quiz.answeroption;

import br.com.novalearn.platform.core.exception.business.ValidationException;
import br.com.novalearn.platform.domain.entities.quiz.QuizAnswerOption;
import br.com.novalearn.platform.domain.entities.quiz.QuizQuestion;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static br.com.novalearn.platform.factories.entities.quiz.CreateQuestionFactory.createInitializedQuestion;
import static org.junit.jupiter.api.Assertions.*;

public class QuizAnswerOptionCreateTest {
    private LocalDateTime now;
    private QuizQuestion question;

    @BeforeEach
    void setUp() {
        now = LocalDateTime.now();
        question = createInitializedQuestion();
    }

    @Test
    void should_create_option_successfully() {
        Long actorId = 10L;

        QuizAnswerOption option = QuizAnswerOption.create(
                question,
                1,
                "Option A",
                true,
                "obs",
                actorId,
                now
        );

        assertNotNull(option);

        assertEquals(question, option.getQuizQuestion());
        assertEquals(1, option.getSequence());
        assertEquals("Option A", option.getOptionText());
        assertTrue(option.isCorrect());
        assertEquals("obs", option.getObservations());

        assertEquals(actorId, option.getCreatedBy());
        assertEquals(now, option.getCreatedAt());
    }

    @Test
    void should_trim_option_text() {
        QuizAnswerOption option = QuizAnswerOption.create(
                question,
                1,
                "  Option  ",
                false,
                null,
                1L,
                now
        );

        assertEquals("Option", option.getOptionText());
    }

    @Test
    void should_throw_when_question_null() {
        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> QuizAnswerOption.create(
                        null,
                        1,
                        "A",
                        false,
                        null,
                        1L,
                        now
                )
        );

        assertEquals("QuizQuestion is required.", exception.getMessage());
    }

    @Test
    void should_throw_when_sequence_invalid() {
        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> QuizAnswerOption.create(
                    question,
                    0,
                    "A",
                    false,
                    null,
                    1L,
                    now
                )
        );

        assertEquals("Sequence must be >= 1.", exception.getMessage());
    }

    @Test
    void should_throw_when_text_blank() {
        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> QuizAnswerOption.create(
                        question,
                        1,
                        "   ",
                        false,
                        null,
                        1L,
                        now
                )
        );

        assertEquals("Option text cannot be empty.", exception.getMessage());
    }
}