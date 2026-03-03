package br.com.novalearn.platform.domain.entities.base;

import br.com.novalearn.platform.core.exception.business.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class BaseEntityAuditTest {
    private TestEntity entity;
    private LocalDateTime now;

    @BeforeEach
    void setUp() {
        entity = new TestEntity();
        now = LocalDateTime.now();
    }

    @Test
    void should_audit_create_correctly() {
        Long actor = 5L;

        entity.auditCreate(actor, now);

        assertEquals(actor, entity.getCreatedBy());
        assertEquals(now, entity.getCreatedAt());
        assertTrue(entity.isActive());
        assertFalse(entity.isDeleted());
    }

    @Test
    void should_fail_when_audit_create_without_actor() {
        assertThrows(
                ValidationException.class,
                () -> entity.auditCreate(null, now)
        );
    }

    @Test
    void should_fail_when_audit_create_without_date() {
        assertThrows(
                ValidationException.class,
                () -> entity.auditCreate(5L, null)
        );
    }

    @Test
    void should_audit_update_correctly() {
        Long actor = 5L;

        entity.auditUpdate(actor, now);

        assertEquals(actor, entity.getUpdatedBy());
        assertEquals(now, entity.getUpdatedAt());
    }

    @Test
    void should_fail_when_audit_update_without_actor() {
        assertThrows(
                ValidationException.class,
                () -> entity.auditUpdate(null, now)
        );
    }

    @Test
    void should_fail_when_audit_update_without_date() {
        assertThrows(
                ValidationException.class,
                () -> entity.auditUpdate(5L, null)
        );
    }
}