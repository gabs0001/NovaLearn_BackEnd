package br.com.novalearn.platform.domain.entities.module;

import br.com.novalearn.platform.core.exception.business.ValidationException;
import br.com.novalearn.platform.domain.entities.course.Course;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static br.com.novalearn.platform.factories.entities.course.CreateCourseFactory.createInitializedCourse;
import static br.com.novalearn.platform.factories.entities.module.CreateModuleFactory.createInitializedModule;
import static org.junit.jupiter.api.Assertions.*;

public class ModuleAttachCourseTest {
    private Course course;
    private Module module;

    @BeforeEach
    void setUp() {
        course = createInitializedCourse();
        module = createInitializedModule(course);
    }

    @Test
    void should_attach_course_successfully() {
        assertEquals(course, module.getCourse());
    }

    @Test
    void should_fail_when_course_is_null() {
        assertThrows(
                ValidationException.class,
                () -> module.attachToCourse(null)
        );
    }

    @Test
    void should_fail_when_already_attached() {
        Course another = createInitializedCourse();

        assertThrows(
                ValidationException.class,
                () -> module.attachToCourse(another)
        );
    }

    @Test
    void should_fail_when_course_is_inactive() {
        assertThrows(
                ValidationException.class,
                () -> module.attachToCourse(course)
        );
    }
}