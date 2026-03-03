package br.com.novalearn.platform.domain.entities.base;

import br.com.novalearn.platform.core.exception.business.ValidationException;
import br.com.novalearn.platform.domain.events.DomainEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

public class BaseEntityDomainEventTest {
    private TestEntity entity;
    private LocalDateTime now;

    @BeforeEach
    void setUp() {
        entity = new TestEntity();
        now = LocalDateTime.now();
    }

    @Test
    void should_register_domain_event() {
        DomainEvent event = mock(DomainEvent.class);
        entity.registerEvent(event);

        assertEquals(1, entity.getDomainEvents().size());
    }

    @Test
    void should_fail_when_registering_null_event() {
        assertThrows(
                ValidationException.class,
                () -> entity.registerEvent(null)
        );
    }

    @Test
    void should_clear_domain_events() {
        entity.registerEvent(mock(DomainEvent.class));
        entity.clearDomainEvents();

        assertTrue(entity.getDomainEvents().isEmpty());
    }

    @Test
    void should_pull_and_clear_events() {
        DomainEvent e1 = mock(DomainEvent.class);
        DomainEvent e2 = mock(DomainEvent.class);

        entity.registerEvent(e1);
        entity.registerEvent(e2);

        List<DomainEvent> pulled = entity.pullDomainEvents();

        assertEquals(2, pulled.size());
        assertTrue(entity.getDomainEvents().isEmpty());
    }

    @Test
    void domain_events_should_be_unmodifiable() {
        entity.registerEvent(mock(DomainEvent.class));

        List<DomainEvent> events = entity.getDomainEvents();

        assertThrows(
                UnsupportedOperationException.class,
                () -> events.add(mock(DomainEvent.class))
        );
    }
}