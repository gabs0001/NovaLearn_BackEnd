package br.com.novalearn.platform.domain.entities.quiz.question;

import br.com.novalearn.platform.core.exception.business.ValidationException;
import br.com.novalearn.platform.domain.entities.quiz.QuizAnswerOption;
import br.com.novalearn.platform.domain.entities.quiz.QuizQuestion;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;

import static br.com.novalearn.platform.factories.entities.quiz.CreateOptionFactory.createInitializedOption;
import static br.com.novalearn.platform.factories.entities.quiz.CreateQuestionFactory.createInitializedQuestion;
import static org.junit.jupiter.api.Assertions.*;

public class QuizQuestionOptionTest {
    private QuizQuestion question;
    private QuizAnswerOption option;

    @BeforeEach
    void setUp() {
        question = createInitializedQuestion();
        option = createInitializedOption(question, LocalDateTime.now());
        ReflectionTestUtils.setField(option, "quizQuestion", null);
    }

    @Test
    void should_add_option() {
        question.addOption(option);

        assertEquals(1, question.getOptions().size());
        assertTrue(question.getOptions().contains(option));
    }

    @Test
    void should_remove_option() {
        question.addOption(option);
        question.removeOption(option);

        assertTrue(question.getOptions().isEmpty());
    }

    @Test
    void should_throw_when_adding_null_option() {
        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> question.addOption(null)
        );

        assertEquals("Answer option is required.", exception.getMessage());
    }
}