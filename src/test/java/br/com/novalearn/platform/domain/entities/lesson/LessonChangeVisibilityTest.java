package br.com.novalearn.platform.domain.entities.lesson;

import br.com.novalearn.platform.domain.entities.module.Module;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static br.com.novalearn.platform.factories.entities.course.CreateCourseFactory.createInitializedCourse;
import static br.com.novalearn.platform.factories.entities.module.CreateModuleFactory.createInitializedModule;
import static org.junit.jupiter.api.Assertions.*;

public class LessonChangeVisibilityTest {
    private Module module;

    @BeforeEach
    void setUp() {
        module = createInitializedModule(createInitializedCourse());
    }

    private Lesson createInitializedLesson(boolean visible, boolean requireCompletion) {
        return Lesson.create(
                module,
                "First Lesson",
                "Description",
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
    void should_allow_hide_lesson_when_completion_is_not_required() {
        // given
        Lesson lesson = createInitializedLesson(true, false);

        // when
        lesson.changeVisibility(false);

        // then
        assertFalse(lesson.isVisible());
    }

    @Test
    void should_allow_show_lesson() {
        // given
        Lesson lesson = createInitializedLesson(false, false);

        // when
        lesson.changeVisibility(true);

        // then
        assertTrue(lesson.isVisible());
    }
}