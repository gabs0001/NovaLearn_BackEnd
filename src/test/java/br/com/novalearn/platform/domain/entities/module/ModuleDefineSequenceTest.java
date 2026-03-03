package br.com.novalearn.platform.domain.entities.module;

import br.com.novalearn.platform.core.exception.business.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static br.com.novalearn.platform.factories.entities.course.CreateCourseFactory.createInitializedCourse;
import static br.com.novalearn.platform.factories.entities.module.CreateModuleFactory.createInitializedModule;
import static org.junit.jupiter.api.Assertions.*;

public class ModuleDefineSequenceTest {
    private Module module;

    @BeforeEach
    void setUp() {
        module = createInitializedModule(createInitializedCourse());
    }

    @Test
    void should_define_sequence() {
        module.defineSequence(3);
        assertEquals(3, module.getSequence());
    }

    @Test
    void should_allow_null_sequence() {
        module.defineSequence(null);
        assertNull(module.getSequence());
    }

    @Test
    void should_fail_when_sequence_is_invalid() {
        assertThrows(
                ValidationException.class,
                () -> module.defineSequence(0)
        );
    }
}