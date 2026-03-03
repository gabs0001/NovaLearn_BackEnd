package br.com.novalearn.platform.domain.entities.module;

import br.com.novalearn.platform.core.exception.business.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static br.com.novalearn.platform.factories.entities.course.CreateCourseFactory.createInitializedCourse;
import static br.com.novalearn.platform.factories.entities.module.CreateModuleFactory.createInitializedModule;
import static org.junit.jupiter.api.Assertions.*;

public class ModuleDefineObservationsTest {
    private Module module;

    @BeforeEach
    void setUp() {
        module = createInitializedModule(createInitializedCourse());
    }

    @Test
    void should_define_observations() {
        module.defineObservations("Obs");
        assertEquals("Obs", module.getObservations());
    }

    @Test
    void should_allow_null_observations() {
        module.defineObservations(null);
        assertNull(module.getObservations());
    }

    @Test
    void should_fail_when_observations_is_too_long() {
        String obs = "a".repeat(501);

        assertThrows(
                ValidationException.class,
                () -> module.defineObservations(obs)
        );
    }
}