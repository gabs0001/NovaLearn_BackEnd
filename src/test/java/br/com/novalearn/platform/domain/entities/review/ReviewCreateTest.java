package br.com.novalearn.platform.domain.entities.review;

import br.com.novalearn.platform.core.exception.business.ValidationException;
import br.com.novalearn.platform.domain.entities.course.Course;
import br.com.novalearn.platform.domain.entities.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;

import static br.com.novalearn.platform.factories.entities.course.CreateCourseFactory.createInitializedCourse;
import static br.com.novalearn.platform.factories.entities.user.CreateUserFactory.createInitializedUser;
import static org.junit.jupiter.api.Assertions.*;

public class ReviewCreateTest {
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
    void should_create_review_successfully() {
        ReflectionTestUtils.setField(user, "id", 5L);

        Review review = Review.create(
                user,
                course,
                4,
                "Very good",
                true,
                "Obs",
                now
        );

        assertNotNull(review);
        assertEquals(user, review.getUser());
        assertEquals(course, review.getCourse());
        assertEquals(4, review.getRating());
        assertEquals("Very good", review.getComment());
        assertTrue(review.isAnonymous());
        assertFalse(review.isApproved());
        assertEquals(now, review.getReviewAt());
        assertNull(review.getPublishedAt());
    }

    @Test
    void should_fail_when_user_is_null() {
        assertThrows(
                ValidationException.class,
                () -> Review.create(
                        null,
                        course,
                        5,
                        "Ok",
                        false,
                        null,
                        now
                )
        );
    }

    @Test
    void should_fail_when_course_is_null() {
        assertThrows(
                ValidationException.class,
                () -> Review.create(
                        user,
                        null,
                        5,
                        "Ok",
                        false,
                        null,
                        now
                )
        );
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 6, -1})
    void should_fail_when_rating_is_invalid(int rating) {
        assertThrows(
                ValidationException.class,
                () -> Review.create(
                        user,
                        course,
                        rating,
                        "Ok",
                        false,
                        null,
                        now
                )
        );
    }

    @Test
    void should_fail_when_now_is_null() {
        assertThrows(
                ValidationException.class,
                () -> Review.create(
                        user,
                        course,
                        5,
                        "Ok",
                        false,
                        null,
                        null
                )
        );
    }
}