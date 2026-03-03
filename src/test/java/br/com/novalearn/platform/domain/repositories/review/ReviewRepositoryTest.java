package br.com.novalearn.platform.domain.repositories.review;

import br.com.novalearn.platform.domain.entities.category.Category;
import br.com.novalearn.platform.domain.entities.course.Course;
import br.com.novalearn.platform.domain.entities.review.Review;
import br.com.novalearn.platform.domain.entities.user.User;
import br.com.novalearn.platform.domain.enums.ReviewStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
public class ReviewRepositoryTest {
    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private TestEntityManager entityManager;

    private LocalDateTime now;

    private User user;
    private User otherUser;

    private Category category;
    private Course course;

    private Review approvedReview;
    private Review pendingReview;
    private Review otherUserReview;

    @BeforeEach
    void setUp() {
        now = LocalDateTime.now();

        user = createUser("a@test.com", "11111111111");
        otherUser = createUser("b@test.com", "22222222222");

        persistUsersFirst();

        category = createCategory("Backend");
        category.setCreatedAt(now);
        category.auditCreate(user.getId(), now);

        course = createCourse("Java", category, user);
        course.setCreatedAt(now);
        course.auditCreate(user.getId(), now);

        approvedReview = createApprovedReview(user, course, 5, "Excelent");
        approvedReview.setCreatedAt(now);
        approvedReview.auditCreate(user.getId(), now);

        pendingReview = createPendingReview(user, course, 4, "Good course");
        pendingReview.setCreatedAt(now);
        pendingReview.auditCreate(user.getId(), now);

        otherUserReview = createApprovedReview(otherUser, course, 3, "Regular");
        otherUserReview.setCreatedAt(now);
        otherUserReview.auditCreate(otherUser.getId(), now);

        persistRest();
    }

    private void persistUsersFirst() {
        ReflectionTestUtils.setField(user, "createdBy", 1L);
        ReflectionTestUtils.setField(user, "createdAt", now);
        ReflectionTestUtils.setField(user, "active", true);
        ReflectionTestUtils.setField(user, "deleted", false);
        user = entityManager.persistAndFlush(user);

        ReflectionTestUtils.setField(otherUser, "createdBy", user.getId());
        ReflectionTestUtils.setField(otherUser, "createdAt", now);
        ReflectionTestUtils.setField(otherUser, "active", true);
        ReflectionTestUtils.setField(otherUser, "deleted", false);
        otherUser = entityManager.persistAndFlush(otherUser);
    }

    private void persistRest() {
        entityManager.persist(category);
        entityManager.persist(course);

        entityManager.persist(approvedReview);
        entityManager.persist(pendingReview);
        entityManager.persist(otherUserReview);
    }

    private User createUser(String email, String cpf) {
        User user = User.register(
                "João",
                "Silva",
                email,
                cpf,
                "pt-BR",
                null,
                null
        );

        user.initializeNewUser("encoded", now);

        return user;
    }

    private Category createCategory(String name) {
        return Category.create(
                name,
                "BCK",
                "Some Description",
                "Observations"
        );
    }

    private Course createCourse(
            String name,
            Category category,
            User user
    ) {
        return Course.create(name, category, user);
    }

    private Review createApprovedReview(
            User user,
            Course course,
            int rating,
            String comment
    ) {
        Review review = Review.create(
                user,
                course,
                rating,
                comment,
                false,
                "observations",
                now
        );

        review.setCreatedAt(now);
        review.approveAndPublish(now);

        return review;
    }

    private Review createPendingReview(
            User user,
            Course course,
            int rating,
            String comment
    ) {
        return Review.create(
                user,
                course,
                rating,
                comment,
                false,
                "observations",
                now
        );
    }

    private Review createDeletedReview(
            User user,
            Course course,
            int rating,
            String comment
    ) {
        Review review = Review.create(
                user,
                course,
                rating,
                comment,
                false,
                "observations",
                now
        );

        review.setCreatedAt(now);
        review.delete();

        return review;
    }

    @Test
    void should_find_all_not_deleted() {
        Review deleted = createDeletedReview(
                user,
                course,
                2,
                "bad course"
        );

        deleted.setCreatedAt(now);

        entityManager.persist(deleted);

        List<Review> result = reviewRepository.findAllByDeletedFalse();

        assertThat(result).hasSize(3);
    }

    @Test
    void should_find_approved_by_course() {
        List<Review> result = reviewRepository.findAllByCourseIdAndStatusAndAndDeletedFalse(
                course.getId(), ReviewStatus.APPROVED);

        assertThat(result).hasSize(2);

        assertThat(result).allMatch(Review::isApproved);
    }

    @Test
    void should_find_by_user() {
        List<Review> result = reviewRepository
                .findAllByUserIdAndDeletedFalse(user.getId());

        assertThat(result).hasSize(2);
    }

    @Test
    void should_find_pending_reviews() {
        List<Review> result = reviewRepository.findAllByStatusAndDeletedFalse(ReviewStatus.PENDING);

        assertThat(result).hasSize(1);

        assertThat(result.getFirst().getStatus()).isEqualTo(ReviewStatus.PENDING);
    }

    @Test
    void should_find_by_id_and_user() {
        Optional<Review> result = reviewRepository.findByIdAndUserIdAndDeletedFalse(
                approvedReview.getId(),
                user.getId()
        );

        assertThat(result).isPresent();
    }

    @Test
    void should_not_find_deleted_by_id_and_user() {
        Review deleted = createDeletedReview(
                user,
                course,
                1,
                "Horrible course"
        );

        deleted.setCreatedAt(now);
        entityManager.persist(deleted);

        Optional<Review> result = reviewRepository.findByIdAndUserIdAndDeletedFalse(
                deleted.getId(),
                user.getId()
        );

        assertThat(result).isEmpty();
    }

    @Test
    void should_find_by_course_and_user() {
        entityManager.remove(pendingReview);
        entityManager.flush();
        entityManager.clear();

        Optional<Review> result = reviewRepository.findByCourseIdAndUserIdAndDeletedFalse(
               course.getId(),
               user.getId()
        );

        assertThat(result).isPresent();
    }

    @Test
    void should_check_exists_review() {
        boolean exists = reviewRepository
                .existsByCourseIdAndUserIdAndDeletedFalse(course.getId(), user.getId());

        assertThat(exists).isTrue();
    }

    @Test
    void should_ignore_current_id_when_checking_duplicate() {
        entityManager.remove(pendingReview);
        entityManager.flush();

        boolean exists = reviewRepository
                .existsByCourseIdAndUserIdAndIdNotAndDeletedFalse(
                        course.getId(), user.getId(), approvedReview.getId());

        assertThat(exists).isFalse();
    }

    @Test
    void should_detect_duplicate_review_on_update() {
        boolean exists = reviewRepository
                .existsByCourseIdAndUserIdAndIdNotAndDeletedFalse(course.getId(), user.getId(), pendingReview.getId());

        assertThat(exists).isTrue();
    }


    @Test
    void should_count_by_user() {
        long count = reviewRepository.countByUserIdAndDeletedFalse(user.getId());
        assertThat(count).isEqualTo(2);
    }
}