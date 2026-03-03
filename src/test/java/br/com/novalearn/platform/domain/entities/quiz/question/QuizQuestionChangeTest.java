package br.com.novalearn.platform.domain.entities.quiz.question;

import br.com.novalearn.platform.core.exception.business.ValidationException;
import br.com.novalearn.platform.domain.entities.quiz.QuizQuestion;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static br.com.novalearn.platform.factories.entities.quiz.CreateQuestionFactory.createInitializedQuestion;
import static org.junit.jupiter.api.Assertions.*;

public class QuizQuestionChangeTest {
    private QuizQuestion question;

    @BeforeEach
    void setUp() {
        question = createInitializedQuestion();
    }

    @Test
    void should_change_description() {
        question.changeDescription("Updated");
        assertEquals("Updated", question.getDescription());
    }

    @Test
    void should_throw_when_description_blank() {
        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> question.changeDescription("  ")
        );

        assertEquals("Question description cannot be blank.", exception.getMessage());
    }

    @Test
    void should_change_points() {
        question.changePoints(10);
        assertEquals(10, question.getPoints());
    }

    @Test
    void should_throw_when_points_invalid() {
        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> question.changePoints(0)
        );

        assertEquals("Points must be at least 1.", exception.getMessage());
    }
}