package br.com.novalearn.platform.domain.events.course;

import br.com.novalearn.platform.domain.entities.course.Course;
import br.com.novalearn.platform.domain.entities.user.User;
import br.com.novalearn.platform.domain.events.DomainEvent;

import java.time.LocalDateTime;

public class CourseCompletedEvent implements DomainEvent {
    private final User user;
    private final Course course;
    private final LocalDateTime occurredAt;

    public CourseCompletedEvent(User user, Course course, LocalDateTime occurredAt) {
        this.user = user;
        this.course = course;
        this.occurredAt = occurredAt;
    }

    public User getUser() { return user; }
    public Course getCourse() { return course; }

    @Override
    public LocalDateTime occurredAt() { return occurredAt; }
}