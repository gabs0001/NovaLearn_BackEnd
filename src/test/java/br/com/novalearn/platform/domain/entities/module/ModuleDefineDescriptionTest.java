package br.com.novalearn.platform.domain.entities.module;

import br.com.novalearn.platform.core.exception.business.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static br.com.novalearn.platform.factories.entities.course.CreateCourseFactory.createInitializedCourse;
import static br.com.novalearn.platform.factories.entities.module.CreateModuleFactory.createInitializedModule;
import static org.junit.jupiter.api.Assertions.*;

public class ModuleDefineDescriptionTest {
    private Module module;

    @BeforeEach
    void setUp() {
        module = createInitializedModule(createInitializedCourse());
    }

    @Test
    void should_define_description() {
        module.defineDescription("  Desc ");
        assertEquals("Desc", module.getDescription());
    }

    @Test
    void should_allow_null_description() {
        module.defineDescription(null);
        assertNull(module.getDescription());
    }

    @Test
    void should_fail_when_description_is_too_long() {
        String desc = "a".repeat(256);

        assertThrows(
                ValidationException.class,
                () -> module.defineDescription(desc)
        );
    }
}