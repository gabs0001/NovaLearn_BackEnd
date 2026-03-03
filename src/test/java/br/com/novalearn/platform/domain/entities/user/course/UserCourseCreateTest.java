package br.com.novalearn.platform.domain.entities.user.course;

import br.com.novalearn.platform.core.exception.business.ValidationException;
import br.com.novalearn.platform.domain.entities.course.Course;
import br.com.novalearn.platform.domain.entities.user.User;
import br.com.novalearn.platform.domain.entities.user.UserCourse;
import br.com.novalearn.platform.domain.enums.EnrollmentStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import static br.com.novalearn.platform.factories.entities.course.CreateCourseFactory.createInitializedCourse;
import static br.com.novalearn.platform.factories.entities.user.CreateUserFactory.createInitializedUser;
import static org.junit.jupiter.api.Assertions.*;

public class UserCourseCreateTest {
    private LocalDateTime now;
    private User user;
    private Course course;

    @BeforeEach
    void setUp() {
        now = LocalDateTime.now();
        user = createInitializedUser();
        course = createInitializedCourse();
    }

    @Test
    void should_enroll_user_in_course_with_initial_state() {
        //when
        UserCourse enrollment = UserCourse.enroll(user, course, now);

        //then
        assertNotNull(enrollment);
        assertEquals(user, enrollment.getUser());
        assertEquals(course, enrollment.getCourse());
        assertEquals(now, enrollment.getEnrolledAt());

        assertEquals(EnrollmentStatus.ENROLLED, enrollment.getEnrollmentStatus());
        assertEquals(0, enrollment.getProgressPercent());
        assertFalse(enrollment.isCertificateIssued());

        ReflectionTestUtils.setField(enrollment, "completedAt", null);
        assertNull(enrollment.getCompletedAt());
    }

    @Test
    void should_throw_validation_exception_when_user_is_null() {
        //when / then
        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> UserCourse.enroll(null, course, now)
        );

        assertEquals("User cannot be null for enrollment.", exception.getMessage());
    }

    @Test
    void should_throw_validation_exception_when_course_is_null() {
        //when / then
        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> UserCourse.enroll(user, null, now)
        );

        assertEquals("Course cannot be null for enrollment.", exception.getMessage());
    }

    @Test
    void should_throw_validation_exception_when_enrollment_date_is_null() {
        //when / then
        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> UserCourse.enroll(user, course, null)
        );

        assertEquals("Enrollment date cannot be null.", exception.getMessage());
    }
}