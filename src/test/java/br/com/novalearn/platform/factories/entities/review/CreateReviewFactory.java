package br.com.novalearn.platform.factories.entities.review;

import br.com.novalearn.platform.domain.entities.course.Course;
import br.com.novalearn.platform.domain.entities.review.Review;
import br.com.novalearn.platform.domain.entities.user.User;

import java.time.LocalDateTime;

import static br.com.novalearn.platform.factories.entities.course.CreateCourseFactory.createInitializedCourse;
import static br.com.novalearn.platform.factories.entities.user.CreateUserFactory.createInitializedUser;

public final class CreateReviewFactory {
    public static Review createInitializedReview(User user, Course course, LocalDateTime now) {
        Review review = Review.create(
                user,
                course,
                5,
                "Excellent course",
                false,
                "Initial review",
                now.minusDays(2)
        );

        review.activate();
        review.markAsNotDeleted();

        return review;
    }

    public static Review createInvalidReview(User user, Course course, LocalDateTime now) {
        return Review.create(
                user,
                course,
                4,
                "Good",
                true,
                null,
                now
        );
    }
}