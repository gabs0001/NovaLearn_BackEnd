package br.com.novalearn.platform.domain.repositories.user;

import br.com.novalearn.platform.domain.entities.category.Category;
import br.com.novalearn.platform.domain.entities.course.Course;
import br.com.novalearn.platform.domain.entities.user.User;
import br.com.novalearn.platform.domain.entities.user.UserCourse;
import br.com.novalearn.platform.domain.enums.EnrollmentStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
public class UserCourseRepositoryTest {
    @Autowired
    private UserCourseRepository userCourseRepository;

    @Autowired
    private TestEntityManager entityManager;

    private LocalDateTime now;
    private Category category;
    private User user;
    private User otherUser;
    private Course course;
    private Course otherCourse;

    @BeforeEach
    void setUp() {
        now = LocalDateTime.now();

        category = createCategory();
        category.setCreatedAt(now);

        user = createUser("a@test.com", "11111111111");
        otherUser = createUser("b@test.com", "22222222222");

        course = createCourse("Java fundamentals", user);
        course.setCreatedAt(now);

        otherCourse = createCourse("Spring Boot", user);
        otherCourse.setCreatedAt(now);

        persistAll();
    }

    private void persistAll() {
        entityManager.persist(category);
        entityManager.persist(user);
        entityManager.persist(otherUser);
        entityManager.persist(course);
        entityManager.persist(otherCourse);
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

        user.initializeNewUser("encoded-password", now);
        return user;
    }

    private Category createCategory() {
        return Category.create(
                "Backend",
                "BCK",
                "Some Description",
                "Observations"
        );
    }

    private Course createCourse(String name, User owner) {
        return Course.create(name, category, owner);
    }

    private UserCourse enroll(User user, Course course, LocalDateTime enrolledAt) {
        UserCourse enrollment = UserCourse.enroll(user, course, enrolledAt);
        enrollment.setCreatedAt(now);
        UserCourse persisted = entityManager.persist(enrollment);
        entityManager.flush();
        return persisted;
    }

    private void enrollAndComplete(User user, Course course, LocalDateTime enrolledAt, LocalDateTime completedAt) {
        UserCourse enrollment = UserCourse.enroll(user, course, enrolledAt);
        enrollment.setCreatedAt(now);
        enrollment.complete(completedAt);
        entityManager.persist(enrollment);
        entityManager.flush();
    }

    private void enrollAndDelete(User user, Course course, LocalDateTime enrolledAt) {
        UserCourse enrollment = UserCourse.enroll(user, course, enrolledAt);
        enrollment.setCreatedAt(now);
        enrollment.delete();
        entityManager.persist(enrollment);
        entityManager.flush();
    }

    @Test
    void should_find_all_active_user_courses() {
        enroll(user, course, now.minusDays(5));
        enrollAndDelete(user, otherCourse, now.minusDays(3));

        List<UserCourse> result = userCourseRepository.findAllByDeletedFalse();

        assertThat(result).hasSize(1);
    }

    @Test
    void should_find_by_user_id_and_deleted_false() {
        enroll(user, course, now.minusDays(5));
        enroll(otherUser, otherCourse, now.minusDays(5));

        List<UserCourse> result = userCourseRepository.findAllByUserIdAndDeletedFalse(user.getId());

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getUser().getId()).isEqualTo(user.getId());
    }

    @Test
    void should_order_by_enrolled_at_desc() {
        UserCourse older = enroll(user, course, now.minusDays(10));
        UserCourse newer = enroll(user, otherCourse, now.minusDays(2));

        older.setCreatedAt(now);
        newer.setCreatedAt(now);

        List<UserCourse> result = userCourseRepository.
                findAllByUserIdAndDeletedFalseOrderByEnrolledAtDesc(user.getId());

        assertThat(result.get(0).getId()).isEqualTo(newer.getId());

        assertThat(result.get(1).getId()).isEqualTo(older.getId());
    }

    @Test
    void should_find_by_user_and_status_and_deleted_false() {
        enroll(user, course, now.minusDays(5));

        enrollAndComplete(user, otherCourse, now.minusDays(10), now.minusDays(1));

        List<UserCourse> result = userCourseRepository
                .findAllByUserIdAndEnrollmentStatusAndDeletedFalse(user.getId(), EnrollmentStatus.COMPLETED);

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getEnrollmentStatus()).isEqualTo(EnrollmentStatus.COMPLETED);
    }

    @Test
    void should_find_completed_courses() {
        enrollAndComplete(user, course, now.minusDays(5), now.minusDays(1));
        enroll(user, otherCourse, now.minusDays(3));

        entityManager.clear();

        List<UserCourse> result = userCourseRepository
                .findAllByUserIdAndCompletedAtIsNotNullAndDeletedFalse(user.getId());

        assertThat(result).hasSize(1);
    }

    @Test
    void should_find_in_progress_courses() {
        enroll(user, course, now.minusDays(5));
        enrollAndComplete(user, otherCourse, now.minusDays(8), now.minusDays(1));

        entityManager.clear();

        List<UserCourse> result = userCourseRepository
                .findAllByUserIdAndCompletedAtIsNullAndDeletedFalse(user.getId());

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getCompletedAt()).isNull();
    }

    @Test
    void should_find_by_user_and_course_and_deleted_false() {
        enroll(user, course, now.minusDays(5));
        enroll(user, otherCourse, now.minusDays(5));

        Optional<UserCourse> result = userCourseRepository
                .findByUserIdAndCourseIdAndDeletedFalse(user.getId(), course.getId());

        assertThat(result).isPresent();
        assertThat(result.get().getCourse().getId()).isEqualTo(course.getId());
    }

    @Test
    void should_check_exists_by_user_and_course_and_deleted_false() {
        enroll(user, course, now.minusDays(5));

        boolean exists = userCourseRepository
                .existsByUserIdAndCourseIdAndDeletedFalse(user.getId(), course.getId());

        assertThat(exists).isTrue();
    }

    @Test
    void should_not_consider_deleted_when_checking_exists() {
        enrollAndDelete(user, course, now.minusDays(5));

        boolean exists = userCourseRepository
                .existsByUserIdAndCourseIdAndDeletedFalse(user.getId(), course.getId());

        assertThat(exists).isFalse();
    }

    @Test
    void should_count_active_by_user() {
        enroll(user, course, now.minusDays(5));
        enroll(user, otherCourse, now.minusDays(3));

        Course docker = createCourse("Docker", user);
        docker.auditCreate(user.getId(), now);
        entityManager.persist(docker);
        entityManager.flush();

        enrollAndDelete(user, docker, now.minusDays(1));

        entityManager.clear();

        long count = userCourseRepository.countByUserIdAndDeletedFalse(user.getId());
        assertThat(count).isEqualTo(2);
    }

    @Test
    void should_count_completed_by_user() {
        enrollAndComplete(user, course, now.minusDays(10), now.minusDays(2));
        enroll(user, otherCourse, now.minusDays(3));

        entityManager.clear();

        long count = userCourseRepository.countCompletedByUserId(user.getId());

        assertThat(count).isEqualTo(1L);
    }
}