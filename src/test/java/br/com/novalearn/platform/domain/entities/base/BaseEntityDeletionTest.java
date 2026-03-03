package br.com.novalearn.platform.domain.entities.base;

import br.com.novalearn.platform.core.exception.business.ForbiddenOperationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class BaseEntityDeletionTest {
    private TestEntity entity;

    @BeforeEach
    void setUp() { entity = new TestEntity(); }

    @Test
    void should_delete_entity() {
        entity.delete();

        assertTrue(entity.isDeleted());
        assertFalse(entity.isActive());
    }

    @Test
    void should_fail_when_deleting_twice() {
        entity.delete();

        assertThrows(
                ForbiddenOperationException.class,
                entity::delete
        );
    }

    @Test
    void should_restore_entity() {
        entity.delete();
        entity.restore();

        assertFalse(entity.isDeleted());
        assertTrue(entity.isActive());
    }

    @Test
    void should_fail_when_restoring_not_deleted_entity() {
        assertThrows(
                ForbiddenOperationException.class,
                entity::restore
        );
    }

    @Test
    void should_mark_as_deleted() {
        entity.markAsDeleted();
        assertTrue(entity.isDeleted());
    }

    @Test
    void should_unmark_deleted() {
        entity.markAsDeleted();
        entity.markAsNotDeleted();

        assertFalse(entity.isDeleted());
    }
}