package br.com.novalearn.platform.domain.entities.user.course;

import br.com.novalearn.platform.core.exception.business.InvalidStateException;
import br.com.novalearn.platform.domain.entities.user.UserCourse;
import br.com.novalearn.platform.domain.enums.EnrollmentStatus;
import br.com.novalearn.platform.domain.events.DomainEvent;
import br.com.novalearn.platform.domain.events.course.CourseCompletedEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import static br.com.novalearn.platform.factories.entities.enrollment.CreateEnrollmentFactory.createInitializedEnrollment;
import static org.junit.jupiter.api.Assertions.*;

public class UserCourseCompleteTest {
    private LocalDateTime now;
    private UserCourse enrollment;

    @BeforeEach
    void setUp() {
        now = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
        enrollment = createInitializedEnrollment();
    }

    @Test
    void should_complete_enrollment_from_enrolled_status() {
        //when
        enrollment.complete(now);

        //then
        assertEquals(EnrollmentStatus.COMPLETED, enrollment.getEnrollmentStatus());
        assertEquals(100, enrollment.getProgressPercent());
        assertEquals(now, enrollment.getCompletedAt());
    }

    @Test
    void should_complete_enrollment_from_in_progress_status() {
        //given
        enrollment.updateProgress(50, now);

        //when
        enrollment.complete(now);

        //then
        assertEquals(EnrollmentStatus.COMPLETED, enrollment.getEnrollmentStatus());
        assertEquals(100, enrollment.getProgressPercent());
        assertEquals(now, enrollment.getCompletedAt());
    }

    @Test
    void should_not_allow_complete_when_enrollment_is_already_completed() {
        //given
        enrollment.complete(now);

        //when / then
        InvalidStateException exception = assertThrows(
                InvalidStateException.class,
                () -> enrollment.complete(now)
        );

        assertEquals("Enrollment cannot be completed from status: COMPLETED", exception.getMessage());
    }

    @Test
    void should_not_allow_complete_when_enrollment_is_cancelled() {
        //given
        enrollment.cancel("Cancelled by user");

        //when / then
        InvalidStateException exception = assertThrows(
                InvalidStateException.class,
                () -> enrollment.complete(now)
        );

        assertEquals("Enrollment cannot be completed from status: CANCELLED", exception.getMessage());
    }

    @Test
    void should_not_allow_complete_when_enrollment_is_refunded() {
        //given
        enrollment.refund("Refunded");

        //when / then
        InvalidStateException exception = assertThrows(
                InvalidStateException.class,
                () -> enrollment.complete(now)
        );

        assertEquals("Enrollment cannot be completed from status: REFUNDED", exception.getMessage());
    }

    @Test
    void should_throw_null_pointer_exception_when_completion_date_is_null() {
        //when / then
        NullPointerException exception = assertThrows(
                NullPointerException.class,
                () -> enrollment.complete(null)
        );

        assertEquals("Completion date cannot be null", exception.getMessage());
    }

    @Test
    void should_register_course_completed_event_when_enrollment_is_completed() {
        //when
        enrollment.complete(now);

        //then
        assertEquals(1, enrollment.getDomainEvents().size());

        DomainEvent event = enrollment.getDomainEvents().getFirst();
        assertInstanceOf(CourseCompletedEvent.class, event);

        CourseCompletedEvent completedEvent = (CourseCompletedEvent) event;

        assertEquals(enrollment.getUser(), completedEvent.getUser());
        assertEquals(enrollment.getCourse(), completedEvent.getCourse());
        assertEquals(now, completedEvent.occurredAt());
    }
}