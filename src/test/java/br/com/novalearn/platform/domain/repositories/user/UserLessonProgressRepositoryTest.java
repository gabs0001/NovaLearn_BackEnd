package br.com.novalearn.platform.domain.repositories.user;

import br.com.novalearn.platform.domain.entities.category.Category;
import br.com.novalearn.platform.domain.entities.course.Course;
import br.com.novalearn.platform.domain.entities.lesson.Lesson;
import br.com.novalearn.platform.domain.entities.module.Module;
import br.com.novalearn.platform.domain.entities.user.User;
import br.com.novalearn.platform.domain.entities.user.UserLessonProgress;
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
public class UserLessonProgressRepositoryTest {
    @Autowired
    private UserLessonProgressRepository userLessonProgressRepository;

    @Autowired
    private TestEntityManager entityManager;

    private LocalDateTime now;

    private User user;
    private Category category;
    private Course course;
    private Module module1;
    private Module module2;

    private Lesson lesson1;
    private Lesson lesson2;
    private Lesson lesson3;

    @BeforeEach
    void setUp() {
        now = LocalDateTime.now();

        user = createUser("a@test.com", "11111111111");

        category = createCategory("Backend");
        category.setCreatedAt(now);

        course = createCourse("Java", category, user);
        course.setCreatedAt(now);

        module1 = createModule("Basics", course);
        module1.setCreatedAt(now);

        module2 = createModule("Advanced", course);
        module2.setCreatedAt(now);

        lesson1 = createLesson("Intro", module1, 1);
        lesson1.setCreatedAt(now);

        lesson2 = createLesson("Variables", module1, 2);
        lesson2.setCreatedAt(now);

        lesson3 = createLesson("Streams", module2, 1);
        lesson3.setCreatedAt(now);

        persistAll();
    }

    private void persistAll() {
        entityManager.persist(user);
        entityManager.persist(category);
        entityManager.persist(course);

        entityManager.persist(module1);
        entityManager.persist(module2);

        entityManager.persist(lesson1);
        entityManager.persist(lesson2);
        entityManager.persist(lesson3);
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

    private Module createModule(String name, Course course) {
        return Module.create(
                course,
                name,
                "description",
                1,
                "observations"
        );
    }

    private Lesson createLesson(
            String name,
            Module module,
            int sequence
    ) {
        return Lesson.create(
                module,
                name,
                "description",
                sequence,
                300,
                false,
                true,
                null,
                null,
                null
        );
    }

    private UserLessonProgress createProgress(
            User user,
            Lesson lesson,
            boolean completed
    ) {
        UserLessonProgress progress = UserLessonProgress.start(user, lesson, now);

        if(completed) progress.complete(now.plusMinutes(30));

        return entityManager.persist(progress);
    }

    private UserLessonProgress createDeletedProgress(
            User user,
            Lesson lesson
    ) {

        UserLessonProgress progress = UserLessonProgress.start(user, lesson, now);

        progress.delete();

        return entityManager.persist(progress);
    }

    @Test
    void should_find_all_not_deleted() {
        createProgress(user, lesson1, true);
        createDeletedProgress(user, lesson2);

        List<UserLessonProgress> result = userLessonProgressRepository.findAllByDeletedFalse();

        assertThat(result).hasSize(1);
    }

    @Test
    void should_find_by_user_and_not_deleted() {
        createProgress(user, lesson1, false);
        createProgress(user, lesson3, true);

        User userB = createUser("b@test.com", "22222222222");

        entityManager.persist(userB);
        entityManager.flush();

        createProgress(userB, lesson1, true);

        entityManager.clear();

        List<UserLessonProgress> result = userLessonProgressRepository
                .findAllByUserIdAndDeletedFalse(user.getId());

        assertThat(result).hasSize(2);
        assertThat(result).allMatch(p -> p.getUser().getId().equals(user.getId()));
    }

    @Test
    void should_find_by_user_and_lesson_and_not_deleted() {
        createProgress(user, lesson1, true);

        Optional<UserLessonProgress> result = userLessonProgressRepository
                .findByUserIdAndLessonIdAndDeletedFalse(user.getId(), lesson1.getId());

        assertThat(result).isPresent();
    }

    @Test
    void should_check_exists_by_user_and_lesson_and_not_deleted() {
        createProgress(user, lesson2, false);

        boolean exists = userLessonProgressRepository
                .existsByUserIdAndLessonIdAndDeletedFalse(user.getId(), lesson2.getId());

        assertThat(exists).isTrue();
    }

    @Test
    void should_not_consider_deleted_when_checking_exists() {
        createDeletedProgress(user, lesson2);

        boolean exists = userLessonProgressRepository
                .existsByUserIdAndLessonIdAndDeletedFalse(user.getId(), lesson2.getId());

        assertThat(exists).isFalse();
    }

    @Test
    void should_count_completed_by_module() {
        createProgress(user, lesson1, true);
        createProgress(user, lesson2, false);

        long count = userLessonProgressRepository
                .countByUserIdAndLesson_Module_IdAndCompletedTrueAndDeletedFalse(user.getId(), module1.getId());

        assertThat(count).isEqualTo(1);
    }

    @Test
    void should_count_total_by_module() {
        createProgress(user, lesson1, true);
        createProgress(user, lesson2, false);

        createDeletedProgress(user, lesson3);

        long count = userLessonProgressRepository
                .countByUserIdAndLesson_Module_IdAndDeletedFalse(user.getId(), module1.getId());

        assertThat(count).isEqualTo(2);
    }

    @Test
    void should_count_completed_by_course() {
        createProgress(user, lesson1, true);
        createProgress(user, lesson2, true);
        createProgress(user, lesson3, false);

        long count = userLessonProgressRepository
                .countByUserIdAndLesson_Module_Course_IdAndCompletedTrueAndDeletedFalse(user.getId(), course.getId());

        assertThat(count).isEqualTo(2);
    }
}