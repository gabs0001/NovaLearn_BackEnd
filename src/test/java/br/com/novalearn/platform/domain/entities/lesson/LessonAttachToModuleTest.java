package br.com.novalearn.platform.domain.entities.lesson;

import br.com.novalearn.platform.core.exception.business.ValidationException;
import br.com.novalearn.platform.domain.entities.course.Course;
import br.com.novalearn.platform.domain.entities.module.Module;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static br.com.novalearn.platform.factories.entities.course.CreateCourseFactory.createInitializedCourse;
import static org.junit.jupiter.api.Assertions.*;

public class LessonAttachToModuleTest {
    private Course course;
    private Lesson lesson;

    @BeforeEach
    void setUp() {
        course = createInitializedCourse();
        lesson = createLessonWithoutModule();
    }

    private Module createInitializedModule(String name) {
        return Module.create(
                course,
                name + " Module",
                "Primeiro módulo",
                1,
                "Módulo em desenvolvimento"
        );
    }

    private Lesson createLessonWithoutModule() {
        return new Lesson();
    }

    @Test
    void should_attach_lesson_to_module() {
        //given
        Module module = createInitializedModule("Java");

        //when
        lesson.attachToModule(module);

        //then
        assertEquals(module, lesson.getModule());
    }

    @Test
    void should_throw_exception_when_module_is_null() {
        //when / then
        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> lesson.attachToModule(null)
        );

        assertEquals("Module cannot be null.", exception.getMessage());
    }

    @Test
    void should_throw_exception_when_lesson_is_already_attached() {
        // given
        Module module1 = createInitializedModule("Java");
        Module module2 = createInitializedModule("Spring");

        lesson.attachToModule(module1);

        // when / then
        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> lesson.attachToModule(module2)
        );

        assertEquals("Lesson is already attached to a Module.", exception.getMessage());
    }
}