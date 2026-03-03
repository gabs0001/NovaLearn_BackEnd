package br.com.novalearn.platform.domain.entities.review;

import br.com.novalearn.platform.core.exception.business.InvalidStateException;
import br.com.novalearn.platform.domain.entities.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;

import static br.com.novalearn.platform.factories.entities.course.CreateCourseFactory.createInitializedCourse;
import static br.com.novalearn.platform.factories.entities.review.CreateReviewFactory.createInitializedReview;
import static br.com.novalearn.platform.factories.entities.user.CreateUserFactory.createInitializedUser;
import static org.junit.jupiter.api.Assertions.*;

public class ReviewRejectTest {
    private LocalDateTime now;
    private Review review;

    @BeforeEach
    void setUp() {
        now = LocalDateTime.now();
        User user = createInitializedUser();
        ReflectionTestUtils.setField(user, "id", 5L);
        review = createInitializedReview(user, createInitializedCourse(), LocalDateTime.now());
    }

    @Test
    void should_reject_review() {
        review.reject("Invalid content");
        assertEquals("Invalid content", review.getObservations());
    }

    @Test
    void should_fail_when_rejecting_approved_review() {
        review.approveAndPublish(now);

        assertThrows(
                InvalidStateException.class,
                () -> review.reject("Reason")
        );
    }
}