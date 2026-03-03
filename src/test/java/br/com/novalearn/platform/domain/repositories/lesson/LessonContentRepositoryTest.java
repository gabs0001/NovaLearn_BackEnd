package br.com.novalearn.platform.domain.repositories.lesson;

import br.com.novalearn.platform.domain.entities.category.Category;
import br.com.novalearn.platform.domain.entities.course.Course;
import br.com.novalearn.platform.domain.entities.lesson.Lesson;
import br.com.novalearn.platform.domain.entities.lesson.LessonContent;
import br.com.novalearn.platform.domain.entities.module.Module;
import br.com.novalearn.platform.domain.entities.user.User;
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
public class LessonContentRepositoryTest {
    @Autowired
    private LessonContentRepository lessonContentRepository;

    @Autowired
    private TestEntityManager entityManager;

    private LocalDateTime now;

    private User user;
    private Category category;
    private Course course;
    private Module module;

    private Lesson lesson;

    @BeforeEach
    void setUp() {
        now = LocalDateTime.now();

        user = createUser("a@test.com", "11111111111");

        category = createCategory("Backend");
        category.setCreatedAt(now);

        course = createCourse("Java", category, user);
        course.setCreatedAt(now);

        module = createModule("Basics", course);
        module.setCreatedAt(now);

        lesson = createLesson("Introduction", module, 1);
        lesson.setCreatedAt(now);

        persistAll();
    }

    private void persistAll() {
        entityManager.persist(user);
        entityManager.persist(category);
        entityManager.persist(course);
        entityManager.persist(module);
        entityManager.persist(lesson);
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

    private LessonContent createContent(boolean main, boolean active) {
        LessonContent content = LessonContent.create(
                lesson,
                "http://url.com",
                null,
                null,
                "some content",
                false,
                main,
                "observations",
                1L,
                now
        );

        if(!active) {
            content.deactivate();
        }

        return content;
    }

    private LessonContent createDeletedContent(boolean main) {
        LessonContent content =
                LessonContent.create(
                        lesson,
                        "http://url.com",
                        null,
                        null,
                        "some content",
                        false,
                        main,
                        "observations",
                        1L,
                        now
                );

        content.delete();

        return content;
    }

    @Test
    void should_find_all_not_deleted() {
        LessonContent deleted = createDeletedContent(false);

        entityManager.persist(deleted);

        LessonContent active = createContent(false, true);

        entityManager.persist(active);

        List<LessonContent> result = lessonContentRepository.findAllByDeletedFalse();

        assertThat(result).hasSize(1);
    }

    @Test
    void should_find_by_lesson_and_not_deleted() {
        LessonContent content = createContent(false, true);

        entityManager.persist(content);

        Optional<LessonContent> result = lessonContentRepository
                .findByLessonIdAndDeletedFalse(lesson.getId());

        assertThat(result).isPresent();
    }

    @Test
    void should_check_exists_main_content() {
        LessonContent main = createContent(true, true);

        entityManager.persist(main);

        boolean exists = lessonContentRepository
                .existsByLessonIdAndMainContentTrue(lesson.getId());

        assertThat(exists).isTrue();
    }

    @Test
    void should_count_active_and_not_deleted() {
        LessonContent active = createContent(false, true);
        LessonContent inactive = createContent(false, false);

        entityManager.persist(active);
        entityManager.persist(inactive);

        long count = lessonContentRepository
                .countByLessonIdAndActiveTrueAndDeletedFalse(lesson.getId());

        assertThat(count).isEqualTo(1);
    }

    @Test
    void should_find_main_content_and_not_deleted() {
        LessonContent main = createContent(true, true);

        entityManager.persist(main);

        Optional<LessonContent> result = lessonContentRepository
                .findByLessonIdAndMainContentTrueAndDeletedFalse(lesson.getId());

        assertThat(result).isPresent();
        assertThat(result.get().isMainContent()).isTrue();
    }
}