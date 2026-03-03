package br.com.novalearn.platform.domain.events.user;

import br.com.novalearn.platform.domain.events.DomainEvent;

import java.time.LocalDateTime;

public class UserRegisteredEvent implements DomainEvent {
    private final String email;
    private final LocalDateTime occurredAt;

    public UserRegisteredEvent(
            String email,
            LocalDateTime occurredAt
    ) {
        this.email = email;
        this.occurredAt = occurredAt;
    }

    public String getEmail() { return email; }
    public LocalDateTime getOccurredAt() { return occurredAt; }

    @Override
    public LocalDateTime occurredAt() { return occurredAt; }
}