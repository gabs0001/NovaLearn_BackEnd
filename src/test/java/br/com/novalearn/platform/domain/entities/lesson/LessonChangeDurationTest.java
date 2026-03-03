package br.com.novalearn.platform.domain.entities.lesson;

import br.com.novalearn.platform.core.exception.business.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static br.com.novalearn.platform.factories.entities.lesson.CreateLessonFactory.createInitializedLesson;
import static org.junit.jupiter.api.Assertions.*;

public class LessonChangeDurationTest {
    private Lesson lesson;

    @BeforeEach
    void setUp() {
        lesson = createInitializedLesson();
    }

    @Test
    void should_change_duration_when_valid() {
        // when
        lesson.changeDuration(600);

        // then
        assertEquals(600, lesson.getDurationSeconds());
    }

    @Test
    void should_throw_validation_exception_when_duration_is_null() {
        // when / then
        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> lesson.changeDuration(null)
        );

        assertEquals("Lesson duration must be at least 30 seconds.", exception.getMessage());
    }

    @Test
    void should_throw_validation_exception_when_duration_is_less_than_minimum() {
        // when / then
        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> lesson.changeDuration(10)
        );

        assertEquals("Lesson duration must be at least 30 seconds.", exception.getMessage());
    }

    @Test
    void should_throw_validation_exception_when_duration_is_negative() {
        // when / then
        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> lesson.changeDuration(-100)
        );

        assertEquals("Lesson duration must be at least 30 seconds.", exception.getMessage());
    }
}