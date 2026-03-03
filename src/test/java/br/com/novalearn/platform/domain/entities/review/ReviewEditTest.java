package br.com.novalearn.platform.domain.entities.review;

import br.com.novalearn.platform.core.exception.business.InvalidStateException;
import br.com.novalearn.platform.core.exception.business.ValidationException;
import br.com.novalearn.platform.domain.entities.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;

import static br.com.novalearn.platform.factories.entities.course.CreateCourseFactory.createInitializedCourse;
import static br.com.novalearn.platform.factories.entities.review.CreateReviewFactory.createInitializedReview;
import static br.com.novalearn.platform.factories.entities.user.CreateUserFactory.createInitializedUser;
import static org.junit.jupiter.api.Assertions.*;

public class ReviewEditTest {
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
    void should_edit_rating() {
        review.edit(3, null, null);
        assertEquals(3, review.getRating());
    }

    @Test
    void should_edit_comment() {
        review.edit(null, "Atualizado", null);
        assertEquals("Atualizado", review.getComment());
    }

    @Test
    void should_edit_anonymous() {
        review.edit(null, null, true);
        assertTrue(review.isAnonymous());
    }

    @Test
    void should_fail_when_editing_approved_review() {
        review.approveAndPublish(now);

        assertThrows(
                InvalidStateException.class,
                () -> review.edit(4, "Novo", false)
        );
    }

    @Test
    void should_fail_when_editing_with_invalid_rating() {
        assertThrows(
                ValidationException.class,
                () -> review.edit(6, null, null)
        );
    }

    @Test
    void should_fail_when_comment_is_too_short() {
        assertThrows(
                ValidationException.class,
                () -> review.edit(null, "ab", null)
        );
    }
}