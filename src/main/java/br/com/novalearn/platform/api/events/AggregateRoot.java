package br.com.novalearn.platform.api.events;

import br.com.novalearn.platform.domain.entities.base.BaseEntity;
import br.com.novalearn.platform.domain.events.DomainEvent;
import jakarta.persistence.Transient;
import org.springframework.data.domain.AfterDomainEventPublication;
import org.springframework.data.domain.DomainEvents;

import java.util.ArrayList;
import java.util.List;

public abstract class AggregateRoot extends BaseEntity {
    @Transient
    private final List<DomainEvent> domainEvents = new ArrayList<>();

    protected void registerEvent(DomainEvent event) {
        domainEvents.add(event);
    }

    @DomainEvents
    public List<DomainEvent> getDomainEvents() {
        return List.copyOf(domainEvents);
    }

    @AfterDomainEventPublication
    public void clearEvents() {
        domainEvents.clear();
    }
}