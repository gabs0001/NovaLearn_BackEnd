package br.com.novalearn.platform.domain.entities.lesson;

import br.com.novalearn.platform.core.exception.business.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static br.com.novalearn.platform.factories.entities.lesson.CreateLessonFactory.createInitializedLesson;
import static org.junit.jupiter.api.Assertions.*;

public class LessonChangeNameTest {
    private Lesson lesson;

    @BeforeEach
    void setUp() {
        lesson = createInitializedLesson();
    }

    @Test
    void should_change_name_when_valid() {
        //when
        lesson.changeName("Nova aula");

        //then
        assertEquals("Nova aula", lesson.getName());
    }

    @Test
    void should_trim_name_when_changing() {
        // when
        lesson.changeName("   Aula Refatorada   ");

        // then
        assertEquals("Aula Refatorada", lesson.getName());
    }

    @Test
    void should_throw_validation_exception_when_name_is_null() {
        // when / then
        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> lesson.changeName(null)
        );

        assertEquals("Lesson name is required.", exception.getMessage());
    }

    @Test
    void should_throw_validation_exception_when_name_is_blank() {
        // when / then
        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> lesson.changeName("   ")
        );

        assertEquals("Lesson name is required.", exception.getMessage());
    }

    @Test
    void should_throw_validation_exception_when_name_is_too_short() {
        // when / then
        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> lesson.changeName("AB")
        );

        assertEquals(
                "Lesson name must have at least 3 characters.",
                exception.getMessage()
        );
    }
}