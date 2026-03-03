package br.com.novalearn.platform.domain.entities.base;

import br.com.novalearn.platform.core.exception.business.ForbiddenOperationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class BaseEntityLifecycleTest {
    private TestEntity entity;

    @BeforeEach
    void setUp() {
        entity = new TestEntity();
    }

    @Test
    void should_activate_entity() {
        entity.deactivate();
        entity.activate();

        assertTrue(entity.isActive());
    }

    @Test
    void should_deactivate_entity() {
        entity.deactivate();
        assertFalse(entity.isActive());
    }

    @Test
    void should_fail_when_activating_deleted_entity() {
        entity.markAsDeleted();

        assertThrows(
                ForbiddenOperationException.class,
                entity::activate
        );
    }

    @Test
    void should_fail_when_deactivating_deleted_entity() {
        entity.markAsDeleted();

        assertThrows(
                ForbiddenOperationException.class,
                entity::deactivate
        );
    }
}