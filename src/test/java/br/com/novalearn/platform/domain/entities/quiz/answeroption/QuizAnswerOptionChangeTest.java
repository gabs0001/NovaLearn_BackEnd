package br.com.novalearn.platform.domain.entities.quiz.answeroption;

import br.com.novalearn.platform.core.exception.business.ValidationException;
import br.com.novalearn.platform.domain.entities.quiz.QuizAnswerOption;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static br.com.novalearn.platform.factories.entities.quiz.CreateOptionFactory.createInitializedOption;
import static br.com.novalearn.platform.factories.entities.quiz.CreateQuestionFactory.createInitializedQuestion;
import static org.junit.jupiter.api.Assertions.*;

public class QuizAnswerOptionChangeTest {
    private QuizAnswerOption option;

    @BeforeEach
    void setUp() {
        option = createInitializedOption(createInitializedQuestion(), LocalDateTime.now());
    }

    @Test
    void should_change_sequence() {
        option.changeSequence(2);
        assertEquals(2, option.getSequence());
    }

    @Test
    void should_throw_when_sequence_invalid() {
        ValidationException ex = assertThrows(
                ValidationException.class,
                () -> option.changeSequence(0)
        );

        assertEquals("Sequence must be >= 1.", ex.getMessage());
    }

    @Test
    void should_change_text() {
        option.changeText("Updated");
        assertEquals("Updated", option.getOptionText());
    }

    @Test
    void should_trim_text_on_change() {
        option.changeText("  New ");
        assertEquals("New", option.getOptionText());
    }

    @Test
    void should_throw_when_text_blank() {
        ValidationException ex = assertThrows(
                ValidationException.class,
                () -> option.changeText("   ")
        );

        assertEquals("Option text cannot be empty.", ex.getMessage());
    }

    @Test
    void should_change_correct_flag() {
        option.changeCorrect(true);
        assertTrue(option.isCorrect());
    }

    @Test
    void should_change_observations() {
        option.changeObservations("obs");
        assertEquals("obs", option.getObservations());
    }
}