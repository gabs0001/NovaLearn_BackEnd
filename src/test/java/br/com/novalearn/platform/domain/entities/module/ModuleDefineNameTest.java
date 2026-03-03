package br.com.novalearn.platform.domain.entities.module;

import br.com.novalearn.platform.core.exception.business.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static br.com.novalearn.platform.factories.entities.course.CreateCourseFactory.createInitializedCourse;
import static br.com.novalearn.platform.factories.entities.module.CreateModuleFactory.createInitializedModule;
import static org.junit.jupiter.api.Assertions.*;

public class ModuleDefineNameTest {
    private Module module;

    @BeforeEach
    void setUp() {
        module = createInitializedModule(createInitializedCourse());
    }

    @Test
    void should_define_name() {
        module.defineName("  Novo Nome  ");
        assertEquals("Novo Nome", module.getName());
    }

    @Test
    void should_fail_when_name_is_null() {
        assertThrows(
                ValidationException.class,
                () -> module.defineName(null)
        );
    }

    @Test
    void should_fail_when_name_is_blank() {
        assertThrows(
                ValidationException.class,
                () -> module.defineName("   ")
        );
    }

    @Test
    void should_fail_when_name_is_too_long() {
        String name = "a".repeat(121);

        assertThrows(
                ValidationException.class,
                () -> module.defineName(name)
        );
    }
}