package br.com.novalearn.platform.domain.entities.lesson.content;

import br.com.novalearn.platform.core.exception.business.ValidationException;
import br.com.novalearn.platform.domain.entities.lesson.Lesson;
import br.com.novalearn.platform.domain.entities.lesson.LessonContent;
import br.com.novalearn.platform.domain.entities.module.Module;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static br.com.novalearn.platform.factories.entities.course.CreateCourseFactory.createInitializedCourse;
import static br.com.novalearn.platform.factories.entities.module.CreateModuleFactory.createInitializedModule;
import static org.junit.jupiter.api.Assertions.*;

public class LessonContentAttachTest {
    private Module module;
    private LessonContent content;

    @BeforeEach
    void setUp() {
        module = createInitializedModule(createInitializedCourse());
        content = createEmptyContent();
    }

    private Lesson createValidLesson(String name) {
        return Lesson.create(
                module,
                name,
                "Description",
                1,
                300,
                false,
                true,
                null,
                null,
                null
        );
    }

    private LessonContent createEmptyContent() {
        return new LessonContent();
    }

    @Test
    void should_attach_to_lesson() {
        Lesson lesson = createValidLesson("user1");

        content.attachToLesson(lesson);

        assertEquals(lesson, content.getLesson());
    }

    @Test
    void should_throw_when_lesson_is_null() {
        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> content.attachToLesson(null)
        );

        assertEquals("Lesson cannot be null.", exception.getMessage());
    }

    @Test
    void should_throw_when_already_attached() {
        Lesson lesson1 = createValidLesson("user1");
        Lesson lesson2 = createValidLesson("user2");

        content.attachToLesson(lesson1);

        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> content.attachToLesson(lesson2)
        );

        assertEquals("LessonContent is already attached to a Lesson.", exception.getMessage());
    }
}