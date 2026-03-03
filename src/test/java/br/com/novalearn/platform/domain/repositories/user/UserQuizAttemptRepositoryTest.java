package br.com.novalearn.platform.domain.repositories.user;

import br.com.novalearn.platform.domain.entities.category.Category;
import br.com.novalearn.platform.domain.entities.course.Course;
import br.com.novalearn.platform.domain.entities.module.Module;
import br.com.novalearn.platform.domain.entities.quiz.Quiz;
import br.com.novalearn.platform.domain.entities.user.User;
import br.com.novalearn.platform.domain.entities.user.UserQuizAttempt;
import br.com.novalearn.platform.domain.enums.QuizAttemptStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
public class UserQuizAttemptRepositoryTest {
    @Autowired
    private UserQuizAttemptRepository userQuizAttemptRepository;

    @Autowired
    private TestEntityManager entityManager;

    private LocalDateTime now;

    private User user;
    private User otherUser;

    private Category category;
    private Course course;
    private Module module;
    private Quiz quiz;

    private UserQuizAttempt attempt1;
    private UserQuizAttempt attempt2;
    private UserQuizAttempt failedAttempt;
    private UserQuizAttempt otherUserAttempt;

    @BeforeEach
    void setUp() {
        now = LocalDateTime.now();
        Long actorId = 1L;

        user = createUser("a@test.com", "11111111111");
        otherUser = createUser("b@test.com", "22222222222");

        user = entityManager.persistAndFlush(user);
        otherUser = entityManager.persistAndFlush(otherUser);

        category = createCategory("Backend");
        category.auditCreate(user.getId(), now);
        category = entityManager.persistAndFlush(category);

        course = createCourse("Java", category, user);
        course.auditCreate(user.getId(), now);
        course = entityManager.persistAndFlush(course);

        module = createModule("Fundamentals", course, 1);
        module.auditCreate(user.getId(), now);
        module = entityManager.persistAndFlush(module);

        quiz = createQuiz("Quiz Java", module);
        quiz.auditCreate(user.getId(), now);
        quiz = entityManager.persistAndFlush(quiz);

        attempt1 = startAttempt(user, quiz, QuizAttemptStatus.FINISHED);
        attempt1.auditCreate(user.getId(), now);

        attempt2 = startAttempt(user, quiz, QuizAttemptStatus.STARTED);
        attempt2.auditCreate(user.getId(), now);

        failedAttempt = startAttempt(user, quiz, QuizAttemptStatus.CANCELLED);
        failedAttempt.auditCreate(user.getId(), now);

        otherUserAttempt = startAttempt(otherUser, quiz, QuizAttemptStatus.FINISHED);
        otherUserAttempt.auditCreate(otherUser.getId(), now);

        persistAttempts();
    }

    private void persistAttempts() {
        entityManager.persist(attempt1);
        entityManager.persist(attempt2);
        entityManager.persist(failedAttempt);
        entityManager.persist(otherUserAttempt);
        entityManager.flush();
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

    private Module createModule(String name, Course course, int sequence) {
        return Module.create(
                course,
                name,
                "description",
                sequence,
                "observations"
        );
    }

    private Quiz createQuiz(String name, Module module) {
        return Quiz.create(
                module,
                name,
                10,
                new BigDecimal("70"),
                3,
                true
        );
    }

    private UserQuizAttempt startAttempt(
            User user,
            Quiz quiz,
            QuizAttemptStatus status
    ) {

        UserQuizAttempt attempt = UserQuizAttempt.start(
                user,
                quiz,
                1,
                1L,
                now
        );

        attempt.setCreatedAt(now);

        switch(status) {
            case FINISHED -> attempt.finish(
                    new BigDecimal("7.0"),
                    new BigDecimal("10.0"),
                    now,
                    1L,
                    now
            );

            case CANCELLED -> attempt.cancel(user.getId(), now);

            case STARTED -> {
                // nothing
            }
        }

        return attempt;
    }

    private UserQuizAttempt createDeletedAttempt(User user, Quiz quiz, QuizAttemptStatus status) {
        UserQuizAttempt attempt = startAttempt(
                user,
                quiz,
                status
        );

        attempt.auditCreate(user.getId(), now);
        attempt.delete();

        return attempt;
    }

    @Test
    void should_find_all_not_deleted() {
        UserQuizAttempt deleted = createDeletedAttempt(
                user,
                quiz,
                QuizAttemptStatus.FINISHED
        );

        deleted.setCreatedAt(now);

        entityManager.persist(deleted);

        List<UserQuizAttempt> result = userQuizAttemptRepository.findAllByDeletedFalse();

        assertThat(result).hasSize(4);
    }

    @Test
    void should_find_by_user_ordered_by_started_at_desc() {
        List<UserQuizAttempt> result = userQuizAttemptRepository
                .findAllByUserIdAndDeletedFalseOrderByStartedAtDesc(user.getId());

        assertThat(result).hasSize(3);

        assertThat(result.get(0).getStartedAt()).isAfterOrEqualTo(result.get(1).getStartedAt());
    }

    @Test
    void should_find_by_user_and_quiz() {
        List<UserQuizAttempt> result = userQuizAttemptRepository
                .findAllByUserIdAndQuizIdAndDeletedFalse(user.getId(), quiz.getId());

        assertThat(result).hasSize(3);
    }

    @Test
    void should_find_by_quiz_and_user_ordered_by_created_at() {
        List<UserQuizAttempt> result = userQuizAttemptRepository
                .findAllByQuizIdAndUserIdAndDeletedFalseOrderByCreatedAtAsc(quiz.getId(), user.getId());

        assertThat(result).hasSize(3);

        assertThat(result.get(0).getCreatedAt()).isBeforeOrEqualTo(result.get(1).getCreatedAt());
    }

    @Test
    void should_find_by_id_and_user_not_deleted() {
        Optional<UserQuizAttempt> result = userQuizAttemptRepository
                .findByIdAndUserIdAndDeletedFalse(attempt1.getId(), user.getId());

        assertThat(result).isPresent();
    }

    @Test
    void should_not_find_deleted_attempt() {
        UserQuizAttempt deleted = createDeletedAttempt(
                user,
                quiz,
                QuizAttemptStatus.FINISHED
        );

        deleted.setCreatedAt(now);

        entityManager.persist(deleted);

        Optional<UserQuizAttempt> result = userQuizAttemptRepository
                .findByIdAndUserIdAndDeletedFalse(deleted.getId(), user.getId());

        assertThat(result).isEmpty();
    }

    @Test
    void should_check_exists_by_status() {
        boolean exists = userQuizAttemptRepository
                .existsByUserIdAndQuizIdAndStatusAndDeletedFalse(
                        user.getId(), quiz.getId(), QuizAttemptStatus.STARTED);

        assertThat(exists).isTrue();
    }

    @Test
    void should_detect_pending_or_failed_attempts() {
        boolean exists = userQuizAttemptRepository
                .existsByUserIdAndCourseIdAndStatusIn(
                        user.getId(),
                        course.getId(),
                        List.of(QuizAttemptStatus.STARTED, QuizAttemptStatus.CANCELLED)
                );

        assertThat(exists).isTrue();
    }

    @Test
    void should_count_by_user_and_quiz() {
        long count = userQuizAttemptRepository
                .countByUserIdAndQuizIdAndDeletedFalse(user.getId(), quiz.getId());

        assertThat(count).isEqualTo(3);
    }

    @Test
    void should_count_by_user() {
        long count = userQuizAttemptRepository.countByUserIdAndDeletedFalse(user.getId());
        assertThat(count).isEqualTo(3);
    }

    @Test
    void should_count_passed_by_user() {
        long count = userQuizAttemptRepository.countByUserIdAndPassedTrueAndDeletedFalse(user.getId());
        assertThat(count).isEqualTo(1);
    }

    @Test
    void should_count_answers_by_attempt() {
        long count = userQuizAttemptRepository.countAnswersByUserAndQuiz(user.getId(), quiz.getId());
        assertThat(count).isZero();
    }

    @Test
    void should_count_correct_answers_by_attempt() {
        long count = userQuizAttemptRepository.countCorrectAnswersByUserAndQuiz(user.getId(), quiz.getId());
        assertThat(count).isZero();
    }

    @Test
    void should_find_last_activity_at_by_user() {
        Optional<LocalDateTime> result = userQuizAttemptRepository.findLastActivityAtByUserId(user.getId());
        assertThat(result).isPresent();
    }
}