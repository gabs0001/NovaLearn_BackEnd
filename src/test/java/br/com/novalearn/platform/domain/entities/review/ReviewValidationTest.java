package br.com.novalearn.platform.domain.entities.review;

import br.com.novalearn.platform.core.exception.business.ForbiddenOperationException;
import br.com.novalearn.platform.domain.entities.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;

import static br.com.novalearn.platform.factories.entities.course.CreateCourseFactory.createInitializedCourse;
import static br.com.novalearn.platform.factories.entities.review.CreateReviewFactory.createInitializedReview;
import static br.com.novalearn.platform.factories.entities.user.CreateUserFactory.createInitializedUser;
import static org.junit.jupiter.api.Assertions.*;

public class ReviewValidationTest {
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
    void should_fail_when_editing_deleted_review() {
        review.markAsDeleted();

        assertThrows(
                ForbiddenOperationException.class,
                () -> review.edit(4, "Novo", false)
        );
    }

    @Test
    void should_fail_when_approving_deleted_review() {
        review.markAsDeleted();

        assertThrows(
                ForbiddenOperationException.class,
                () -> review.approveAndPublish(now)
        );
    }

    @Test
    void should_fail_when_rejecting_deleted_review() {
        review.markAsDeleted();

        assertThrows(
                ForbiddenOperationException.class,
                () -> review.reject("Motivo")
        );
    }
}