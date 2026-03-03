package br.com.novalearn.platform.domain.entities.lesson;

import br.com.novalearn.platform.core.exception.business.ValidationException;
import br.com.novalearn.platform.domain.entities.category.Category;
import br.com.novalearn.platform.domain.entities.course.Course;
import br.com.novalearn.platform.domain.entities.module.Module;
import br.com.novalearn.platform.domain.entities.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static br.com.novalearn.platform.factories.entities.course.CreateCourseFactory.createInitializedCourse;
import static br.com.novalearn.platform.factories.entities.module.CreateModuleFactory.createInitializedModule;
import static org.junit.jupiter.api.Assertions.*;

public class LessonChangeRequireCompletionTest {
    private Module module;

    @BeforeEach
    void setUp() {
        module = createInitializedModule(createInitializedCourse());
    }

    private Lesson createInitializedLesson(boolean visible, boolean requireCompletion) {
        return Lesson.create(
                module,
                "Aula Inicial",
                "Descrição",
                1,
                300,
                requireCompletion,
                visible,
                null,
                null,
                null
        );
    }

    @Test
    void should_allow_require_completion_when_lesson_is_visible() {
        // given
        Lesson lesson = createInitializedLesson(true, false);

        // when
        lesson.changeRequireCompletion(true);

        // then
        assertTrue(lesson.isRequireCompletion());
    }

    @Test
    void should_allow_disable_require_completion() {
        // given
        Lesson lesson = createInitializedLesson(true, true);

        // when
        lesson.changeRequireCompletion(false);

        // then
        assertFalse(lesson.isRequireCompletion());
    }

    @Test
    void should_throw_exception_when_require_completion_on_invisible_lesson() {
        // given
        Lesson lesson = createInitializedLesson(false, false);

        // when / then
        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> lesson.changeRequireCompletion(true)
        );

        assertEquals("Invisible lessons cannot require completion.", exception.getMessage());
    }

    @Test
    void should_throw_exception_when_visibility_is_changed_and_breaks_rule() {
        // given
        Lesson lesson = createInitializedLesson(true, true);

        // when
        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> lesson.changeVisibility(false)
        );

        // then
        assertEquals("Invisible lessons cannot require completion.", exception.getMessage());
    }
}