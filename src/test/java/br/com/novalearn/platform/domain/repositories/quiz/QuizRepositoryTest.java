package br.com.novalearn.platform.domain.repositories.quiz;

import br.com.novalearn.platform.domain.entities.category.Category;
import br.com.novalearn.platform.domain.entities.course.Course;
import br.com.novalearn.platform.domain.entities.module.Module;
import br.com.novalearn.platform.domain.entities.quiz.Quiz;
import br.com.novalearn.platform.domain.entities.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
public class QuizRepositoryTest {
    @Autowired
    private QuizRepository quizRepository;

    @Autowired
    private TestEntityManager entityManager;

    private LocalDateTime now;

    private User user;
    private Category category;
    private Course course;
    private Module module;

    private Quiz quiz1;
    private Quiz quiz2;

    @BeforeEach
    void setUp() {
        now = LocalDateTime.now();

        user = createUser("a@test.com", "11111111111");

        category = createCategory("Backend");
        category.setCreatedAt(now);

        course = createCourse("Java", category, user);
        course.setCreatedAt(now);

        module = createModule("Fundamentals", course, 1);
        module.setCreatedAt(now);

        quiz1 = createQuiz("Quiz 1", module);
        quiz1.setCreatedAt(now);

        quiz2 = createQuiz("Quiz 2", module);
        quiz2.setCreatedAt(now);

        persistAll();
    }

    private void persistAll() {
        entityManager.persist(user);
        entityManager.persist(category);
        entityManager.persist(course);
        entityManager.persist(module);

        entityManager.persist(quiz1);
        entityManager.persist(quiz2);
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

    private Quiz createDeletedQuiz(String name, Module module) {
        Quiz quiz = Quiz.create(
                module,
                name,
                10,
                new BigDecimal("70"),
                3,
                true
        );

        quiz.setCreatedAt(now);
        quiz.delete();

        return quiz;
    }

    @Test
    void should_find_all_not_deleted() {
        Quiz deleted = createDeletedQuiz("Quiz extra", module);
        deleted.setCreatedAt(now);

        entityManager.persist(deleted);

        List<Quiz> result = quizRepository.findAllByDeletedFalse();

        assertThat(result).hasSize(2);
    }

    @Test
    void should_find_all_by_module_and_not_deleted() {
        List<Quiz> result = quizRepository
                .findAllByModuleIdAndDeletedFalse(module.getId());

        assertThat(result).hasSize(2);
    }

    @Test
    void should_check_exists_by_module_and_name_ignore_case() {
        boolean exists = quizRepository
                .existsByModuleIdAndNameIgnoreCaseAndDeletedFalse(module.getId(), "qUiZ 1");

        assertThat(exists).isTrue();
    }

    @Test
    void should_not_consider_deleted_when_checking_exists() {
        Quiz deleted = createDeletedQuiz("Quiz extra", module);
        deleted.setCreatedAt(now);

        entityManager.persist(deleted);

        boolean exists = quizRepository.existsByModuleIdAndNameIgnoreCaseAndDeletedFalse(module.getId(), "Quiz extra");

        assertThat(exists).isFalse();
    }

    @Test
    void should_ignore_current_id_when_checking_duplicate_name() {
        boolean exists = quizRepository
                .existsByModuleIdAndNameIgnoreCaseAndIdNotAndDeletedFalse(module.getId(), "quiz 1", quiz1.getId());

        assertThat(exists).isFalse();
    }

    @Test
    void should_detect_duplicate_name_when_updating_other_quiz() {
        boolean exists = quizRepository
                .existsByModuleIdAndNameIgnoreCaseAndIdNotAndDeletedFalse(module.getId(), "quiz 1", quiz2.getId());

        assertThat(exists).isTrue();
    }
}