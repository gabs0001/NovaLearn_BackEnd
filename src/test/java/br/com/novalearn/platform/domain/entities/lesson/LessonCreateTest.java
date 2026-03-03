package br.com.novalearn.platform.domain.entities.lesson;

import br.com.novalearn.platform.core.exception.business.ValidationException;
import br.com.novalearn.platform.domain.entities.module.Module;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static br.com.novalearn.platform.factories.entities.course.CreateCourseFactory.createInitializedCourse;
import static br.com.novalearn.platform.factories.entities.module.CreateModuleFactory.createInitializedModule;
import static org.junit.jupiter.api.Assertions.*;

public class LessonCreateTest {
    private Module module;

    @BeforeEach
    void setUp() {
        module = createInitializedModule(createInitializedCourse());
    }

    @Test
    void should_create_lesson_with_valid_data() {
        //when
        Lesson lesson = Lesson.create(
                module,
                "Lesson 1",
                "Introduction ",
                1,
                300,
                true,
                true,
                "http://preview.url",
                "Notes",
                "Observations"
        );

        //then
        assertNotNull(lesson);
        assertEquals(module, lesson.getModule());
        assertEquals("Lesson 1", lesson.getName());
        assertEquals(1, lesson.getSequence());
        assertEquals(300, lesson.getDurationSeconds());
        assertTrue(lesson.isRequireCompletion());
        assertTrue(lesson.isVisible());
        assertTrue(lesson.isActive());
        assertFalse(lesson.isDeleted());
    }

    @Test
    void should_throw_validation_exception_when_module_is_null() {
        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> Lesson.create(
                        null,
                        "Lesson",
                        null,
                        1,
                        300,
                        false,
                        true,
                        null,
                        null,
                        null
                )
        );

        assertEquals("Module cannot be null.", exception.getMessage());
    }

    @Test
    void should_throw_validation_exception_when_name_is_null() {
        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> Lesson.create(
                        module,
                        null,
                        null,
                        1,
                        300,
                        false,
                        true,
                        null,
                        null,
                        null
                )
        );

        assertEquals("Lesson name is required.", exception.getMessage());
    }

    @Test
    void should_throw_validation_exception_when_sequence_is_invalid() {
        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> Lesson.create(
                        module,
                        "Lesson",
                        null,
                        0,
                        300,
                        false,
                        true,
                        null,
                        null,
                        null
                )
        );

        assertEquals("Lesson sequence must be greater than zero.", exception.getMessage());
    }

    @Test
    void should_throw_validation_exception_when_duration_is_less_than_minimum() {
        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> Lesson.create(
                        module,
                        "Lesson",
                        null,
                        1,
                        10,
                        false,
                        true,
                        null,
                        null,
                        null
                )
        );

        assertEquals("Lesson duration must be at least 30 seconds.", exception.getMessage());
    }

    @Test
    void should_throw_validation_exception_when_invisible_lesson_requires_completion() {
        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> Lesson.create(
                        module,
                        "Lesson",
                        null,
                        1,
                        300,
                        true,
                        false,
                        null,
                        null,
                        null
                )
        );

        assertEquals("Invisible lessons cannot require completion.", exception.getMessage());
    }
}