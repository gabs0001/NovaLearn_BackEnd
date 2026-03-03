package br.com.novalearn.platform.domain.entities.quiz;

import br.com.novalearn.platform.core.exception.business.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static br.com.novalearn.platform.factories.entities.quiz.CreateQuizFactory.createInitializedQuiz;
import static org.junit.jupiter.api.Assertions.*;

public class QuizChangeConfigurationTest {
    private Quiz quiz;

    @BeforeEach
    void setUp() {
        quiz = createInitializedQuiz();
    }

    @Test
    void should_change_configuration() {
        quiz.changeConfiguration(
                "New Name",
                "Desc",
                "Inst",
                5,
                true
        );

        assertEquals("New Name", quiz.getName());
        assertEquals("Desc", quiz.getDescription());
        assertEquals("Inst", quiz.getInstructions());
        assertEquals(5, quiz.getMaxAttempts());
        assertTrue(quiz.isRandomOrder());
    }

    @Test
    void should_throw_when_name_blank() {
        ValidationException ex = assertThrows(
                ValidationException.class,
                () -> quiz.changeConfiguration(
                        "   ",
                        null,
                        null,
                        null,
                        false
                )
        );

        assertEquals("Quiz name cannot be blank.", ex.getMessage());
    }
}