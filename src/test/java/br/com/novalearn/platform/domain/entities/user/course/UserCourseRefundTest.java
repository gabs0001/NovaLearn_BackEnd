package br.com.novalearn.platform.domain.entities.user.course;

import br.com.novalearn.platform.core.exception.business.ForbiddenOperationException;
import br.com.novalearn.platform.domain.entities.user.UserCourse;
import br.com.novalearn.platform.domain.enums.EnrollmentStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static br.com.novalearn.platform.factories.entities.enrollment.CreateEnrollmentFactory.createInitializedEnrollment;
import static org.junit.jupiter.api.Assertions.*;

public class UserCourseRefundTest {
    private LocalDateTime now;
    private UserCourse enrollment;

    @BeforeEach
    void setUp() {
        now = LocalDateTime.now();
        enrollment = createInitializedEnrollment();
    }

    @Test
    void should_refund_enrollment_from_enrolled_status() {
        //given
        String reason = "Payment chargeback";

        //when
        enrollment.refund(reason);

        //then
        assertEquals(EnrollmentStatus.REFUNDED, enrollment.getEnrollmentStatus());
        assertEquals(reason, enrollment.getObservations());
    }

    @Test
    void should_refund_enrollment_from_in_progress_status() {
        //given
        enrollment.updateProgress(40, now);
        String reason = "User dissatisfied";

        //when
        enrollment.refund(reason);

        //then
        assertEquals(EnrollmentStatus.REFUNDED, enrollment.getEnrollmentStatus());
        assertEquals(reason, enrollment.getObservations());
    }

    @Test
    void should_refund_enrollment_from_cancelled_status() {
        //given
        enrollment.cancel("User cancelled");
        String refundReason = "Refund after cancellation";

        //when
        enrollment.refund(refundReason);

        //then
        assertEquals(EnrollmentStatus.REFUNDED, enrollment.getEnrollmentStatus());
        assertEquals(refundReason, enrollment.getObservations());
    }

    @Test
    void should_not_allow_refund_when_enrollment_is_completed() {
        //given
        enrollment.complete(now);

        //when / then
        ForbiddenOperationException exception = assertThrows(
                ForbiddenOperationException.class,
                () -> enrollment.refund("Refund completed enrollment")
        );

        assertEquals("Completed enrollment cannot be refunded.", exception.getMessage());
    }
}