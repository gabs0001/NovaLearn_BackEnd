package br.com.novalearn.platform.domain.entities.quiz.answeroption;

import br.com.novalearn.platform.core.exception.business.ValidationException;
import br.com.novalearn.platform.domain.entities.quiz.QuizAnswerOption;
import br.com.novalearn.platform.domain.entities.quiz.QuizQuestion;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static br.com.novalearn.platform.factories.entities.quiz.CreateQuestionFactory.createInitializedQuestion;
import static org.junit.jupiter.api.Assertions.*;

public class QuizAnswerOptionAttachTest {
    private QuizQuestion question;
    private QuizAnswerOption option;

    @BeforeEach
    void setUp() {
        question = createInitializedQuestion();
        option = createDetachedOption();
    }

    private QuizAnswerOption createDetachedOption() {
        return QuizAnswerOption.create(
                question,
                1,
                "A",
                false,
                null,
                1L,
                LocalDateTime.now()
        );
    }

    @Test
    void should_throw_when_attach_null_question() {
        ValidationException ex = assertThrows(
                ValidationException.class,
                () -> option.attachToQuestion(null)
        );

        assertEquals("QuizQuestion cannot be null.", ex.getMessage());
    }

    @Test
    void should_throw_when_already_attached() {
        ValidationException ex = assertThrows(
                ValidationException.class,
                () -> option.attachToQuestion(question)
        );

        assertEquals("QuizAnswerOption is already attached to a QuizQuestion.", ex.getMessage());
    }
}