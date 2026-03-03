package br.com.novalearn.platform.domain.entities.user.course;

import br.com.novalearn.platform.core.exception.business.InvalidStateException;
import br.com.novalearn.platform.core.exception.business.ValidationException;
import br.com.novalearn.platform.domain.entities.user.UserCourse;
import br.com.novalearn.platform.domain.enums.EnrollmentStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static br.com.novalearn.platform.factories.entities.enrollment.CreateEnrollmentFactory.createInitializedEnrollment;
import static org.junit.jupiter.api.Assertions.*;

public class UserCourseCancelTest {
    private LocalDateTime now;
    private UserCourse enrollment;

    @BeforeEach
    void setUp() {
        now = LocalDateTime.now();
        enrollment = createInitializedEnrollment();
    }

    @Test
    void should_cancel_enrollment_from_enrolled_status() {
        //given
        String reason = "User request cancellation";

        //when
        enrollment.cancel(reason);

        //then
        assertEquals(EnrollmentStatus.CANCELLED, enrollment.getEnrollmentStatus());
    }

    @Test
    void should_cancel_enrollment_from_in_progress_status() {
        //given
        enrollment.updateProgress(30, now);
        String reason = "User no longer interested";

        //when
        enrollment.cancel(reason);

        //then
        assertEquals(EnrollmentStatus.CANCELLED, enrollment.getEnrollmentStatus());
    }

    @Test
    void should_not_allow_cancel_when_enrollment_is_completed() {
        //given
        enrollment.complete(now);

        //when / then
        InvalidStateException exception = assertThrows(
                InvalidStateException.class,
                () -> enrollment.cancel("Invalid cancel")
        );

        assertEquals("Enrollment cannot be cancelled from status: COMPLETED", exception.getMessage());
    }

    @Test
    void should_not_allow_cancel_when_enrollment_is_already_cancelled() {
        //given
        enrollment.cancel("Already cancelled");

        //when / then
        InvalidStateException exception = assertThrows(
                InvalidStateException.class,
                () -> enrollment.cancel("Cancel again")
        );

        assertEquals("Enrollment cannot be cancelled from status: CANCELLED", exception.getMessage());
    }

    @Test
    void should_not_allow_cancel_when_enrollment_is_refunded() {
        //given
        enrollment.refund("Refunded");

        //when / then
        InvalidStateException exception = assertThrows(
                InvalidStateException.class,
                () -> enrollment.cancel("Cancel refunded")
        );

        assertEquals("Enrollment cannot be cancelled from status: REFUNDED", exception.getMessage());
    }

    @Test
    void should_throw_validation_exception_when_cancellation_reason_is_null() {
        //when / then
        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> enrollment.cancel(null)
        );

        assertEquals("Cancellation reason cannot be empty.", exception.getMessage());
    }

    @Test
    void should_throw_validation_exception_when_cancellation_reason_is_blank() {
        //when / then
        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> enrollment.cancel("   ")
        );

        assertEquals("Cancellation reason cannot be empty.", exception.getMessage());
    }
}