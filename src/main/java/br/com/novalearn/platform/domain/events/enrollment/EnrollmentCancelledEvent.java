package br.com.novalearn.platform.domain.events.enrollment;

import br.com.novalearn.platform.domain.events.DomainEvent;

import java.time.LocalDateTime;

public class EnrollmentCancelledEvent implements DomainEvent {
    private final Long userId;
    private final Long courseId;
    private final LocalDateTime occurredAt;

    public EnrollmentCancelledEvent(
            Long userId,
            Long courseId,
            LocalDateTime occurredAt
    ) {
        this.userId = userId;
        this.courseId = courseId;
        this.occurredAt = occurredAt;
    }

    public Long getUserId() { return userId; }
    public Long getCourseId() { return courseId; }

    @Override
    public LocalDateTime occurredAt() { return occurredAt; }
}