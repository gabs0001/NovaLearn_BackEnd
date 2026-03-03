package br.com.novalearn.platform.domain.entities.lesson;

import br.com.novalearn.platform.core.exception.business.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static br.com.novalearn.platform.factories.entities.lesson.CreateLessonFactory.createInitializedLesson;
import static org.junit.jupiter.api.Assertions.*;

public class LessonChangeSequenceTest {
    private Lesson lesson;

    @BeforeEach
    void setUp() {
        lesson = createInitializedLesson();
    }

    @Test
    void should_change_sequence_when_valid() {
        //when
        lesson.changeSequence(2);

        //then
        assertEquals(2, lesson.getSequence());
    }

    @Test
    void should_throw_validation_exception_when_sequence_is_null() {
        // when / then
        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> lesson.changeSequence(null)
        );

        assertEquals(
                "Lesson sequence must be greater than zero.",
                exception.getMessage()
        );
    }

    @Test
    void should_throw_validation_exception_when_sequence_is_zero() {
        // when / then
        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> lesson.changeSequence(0)
        );

        assertEquals("Lesson sequence must be greater than zero.", exception.getMessage());
    }

    @Test
    void should_throw_validation_exception_when_sequence_is_negative() {
        // when / then
        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> lesson.changeSequence(-5)
        );

        assertEquals("Lesson sequence must be greater than zero.", exception.getMessage());
    }
}