package br.com.novalearn.platform.domain.events.certificate;

import br.com.novalearn.platform.domain.events.DomainEvent;

import java.time.LocalDateTime;

public class CertificateIssuedEvent implements DomainEvent {
    private final Long userId;
    private final Long courseId;
    private final LocalDateTime occurredAt;

    public CertificateIssuedEvent(Long userId, Long courseId, LocalDateTime occurredAt) {
        this.userId = userId;
        this.courseId = courseId;
        this.occurredAt = occurredAt;
    }

    public Long getUserId() { return userId; }
    public Long getCourseId() { return courseId; }

    @Override
    public LocalDateTime occurredAt() { return occurredAt; }
}