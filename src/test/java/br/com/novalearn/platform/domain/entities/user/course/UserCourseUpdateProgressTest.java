package br.com.novalearn.platform.domain.entities.user.course;

import br.com.novalearn.platform.core.exception.business.InvalidStateException;
import br.com.novalearn.platform.core.exception.business.ValidationException;
import br.com.novalearn.platform.domain.entities.user.UserCourse;
import br.com.novalearn.platform.domain.enums.EnrollmentStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import static br.com.novalearn.platform.factories.entities.enrollment.CreateEnrollmentFactory.createInitializedEnrollment;
import static org.junit.jupiter.api.Assertions.*;

public class UserCourseUpdateProgressTest {
    private LocalDateTime now;
    private UserCourse enrollment;

    @BeforeEach
    void setUp() {
        now = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
        enrollment = createInitializedEnrollment();
    }

    @Test
    void should_update_progress_and_keep_status_enrolled_when_progress_is_zero() {
        //when
        enrollment.updateProgress(0, now);

        //then
        assertEquals(0, enrollment.getProgressPercent());
        assertEquals(EnrollmentStatus.ENROLLED, enrollment.getEnrollmentStatus());

        ReflectionTestUtils.setField(enrollment, "completedAt", null);
        assertNull(enrollment.getCompletedAt());
    }

    @Test
    void should_update_progress_and_change_status_to_in_progress_when_progress_is_greater_than_zero() {
        //when
        enrollment.updateProgress(10, now);

        //then
        assertEquals(10, enrollment.getProgressPercent());
        assertEquals(EnrollmentStatus.IN_PROGRESS, enrollment.getEnrollmentStatus());

        ReflectionTestUtils.setField(enrollment, "completedAt", null);
        assertNull(enrollment.getCompletedAt());
    }

    @Test
    void should_complete_enrollment_when_progress_reaches_100() {
        //when
        enrollment.updateProgress(100, now);

        //then
        assertEquals(100, enrollment.getProgressPercent());
        assertEquals(EnrollmentStatus.COMPLETED, enrollment.getEnrollmentStatus());
        assertNotNull(enrollment.getCompletedAt());
        assertEquals(now, enrollment.getCompletedAt());
    }

    @Test
    void should_throw_validation_exception_when_progress_is_negative() {
        //when / then
        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> enrollment.updateProgress(-1, now)
        );

        assertEquals("Progress must be between 0 and 100.", exception.getMessage());
    }

    @Test
    void should_throw_validation_exception_when_progress_is_greater_than_100() {
        //when / then
        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> enrollment.updateProgress(101, now)
        );

        assertEquals("Progress must be between 0 and 100.", exception.getMessage());
    }

    @Test
    void should_not_allow_progress_update_when_enrollment_is_completed() {
        //given
        enrollment.updateProgress(100, now);

        //when / then
        InvalidStateException exception = assertThrows(
                InvalidStateException.class,
                () -> enrollment.updateProgress(50, now)
        );

        assertEquals("Progress cannot be updated when enrollment is COMPLETED.", exception.getMessage());
    }

    @Test
    void should_not_allow_progress_update_when_enrollment_is_cancelled() {
        //given
        enrollment.cancel("User requested cancellation");

        //when / then
        InvalidStateException exception = assertThrows(
                InvalidStateException.class,
                () -> enrollment.updateProgress(10, now)
        );

        assertEquals("Progress cannot be updated when enrollment is CANCELLED.", exception.getMessage());
    }

    @Test
    void should_not_allow_progress_update_when_enrollment_is_refunded() {
        //given
        enrollment.refund("Payment refused");

        //when / then
        InvalidStateException exception = assertThrows(
                InvalidStateException.class,
                () -> enrollment.updateProgress(10, now)
        );

        assertEquals("Progress cannot be updated when enrollment is REFUNDED.", exception.getMessage());
    }
}