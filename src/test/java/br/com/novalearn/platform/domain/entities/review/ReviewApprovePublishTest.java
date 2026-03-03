package br.com.novalearn.platform.domain.entities.review;

import br.com.novalearn.platform.core.exception.business.InvalidStateException;
import br.com.novalearn.platform.core.exception.business.ValidationException;
import br.com.novalearn.platform.domain.entities.user.User;
import br.com.novalearn.platform.domain.enums.ReviewStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;

import static br.com.novalearn.platform.factories.entities.course.CreateCourseFactory.createInitializedCourse;
import static br.com.novalearn.platform.factories.entities.review.CreateReviewFactory.createInitializedReview;
import static br.com.novalearn.platform.factories.entities.user.CreateUserFactory.createInitializedUser;
import static org.junit.jupiter.api.Assertions.*;

public class ReviewApprovePublishTest {
    private LocalDateTime now;
    private Review review;

    @BeforeEach
    void setUp() {
        now = LocalDateTime.now();
        User user = createInitializedUser();
        ReflectionTestUtils.setField(user, "id", 5L);
        review = createInitializedReview(user, createInitializedCourse(), now);
    }

    @Test
    void should_approve_and_publish_review() {
        review.approveAndPublish(now);

        assertEquals(ReviewStatus.APPROVED, review.getStatus());
        assertEquals(now, review.getPublishedAt());
    }

    @Test
    void should_fail_when_already_approved() {
        review.approveAndPublish(now);

        assertThrows(
                InvalidStateException.class,
                () -> review.approveAndPublish(now)
        );
    }

    @Test
    void should_fail_when_publish_date_is_null() {
        assertThrows(
                ValidationException.class,
                () -> review.approveAndPublish(null)
        );
    }
}