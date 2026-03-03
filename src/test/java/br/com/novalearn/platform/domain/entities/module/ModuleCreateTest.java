package br.com.novalearn.platform.domain.entities.module;

import br.com.novalearn.platform.core.exception.business.ValidationException;
import br.com.novalearn.platform.domain.entities.course.Course;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static br.com.novalearn.platform.factories.entities.course.CreateCourseFactory.createInitializedCourse;
import static br.com.novalearn.platform.factories.entities.module.CreateModuleFactory.createInitializedModule;
import static org.junit.jupiter.api.Assertions.*;

public class ModuleCreateTest {
    private Course course;

    @BeforeEach
    void setUp() {
        course = createInitializedCourse();
    }

    @Test
    void should_create_module_successfully() {
        Module module = createInitializedModule(course);

        assertNotNull(module);
        assertEquals(course, module.getCourse());
        assertEquals("Module", module.getName());
        assertEquals("First Module", module.getDescription());
        assertEquals(1, module.getSequence());
        assertEquals("Module in development", module.getObservations());
        assertTrue(module.isActive());
        assertFalse(module.isDeleted());
    }

    @Test
    void should_fail_when_course_is_null() {
        assertThrows(
                ValidationException.class,
                () -> Module.create(
                        null,
                        "name",
                        null,
                        1,
                        null
                )
        );
    }

    @Test
    void should_fail_when_name_is_invalid() {
        assertThrows(
                ValidationException.class,
                () -> Module.create(
                        course,
                        "   ",
                        null,
                        1,
                        null
                )
        );
    }
}