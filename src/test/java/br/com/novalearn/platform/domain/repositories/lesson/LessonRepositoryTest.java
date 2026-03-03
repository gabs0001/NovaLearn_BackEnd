package br.com.novalearn.platform.domain.repositories.lesson;

import br.com.novalearn.platform.domain.entities.category.Category;
import br.com.novalearn.platform.domain.entities.course.Course;
import br.com.novalearn.platform.domain.entities.lesson.Lesson;
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

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
public class LessonRepositoryTest {
    @Autowired
    private LessonRepository lessonRepository;

    @Autowired
    private TestEntityManager entityManager;

    private LocalDateTime now;

    private User user;
    private Category category;
    private Course course;
    private Module module;

    private Lesson lesson1;
    private Lesson lesson2;

    @BeforeEach
    void setUp() {
        now = LocalDateTime.now();

        user = createUser("a@test.com", "11111111111");

        category = createCategory("Backend");
        category.setCreatedAt(now);

        course = createCourse("Java", category, user);
        course.setCreatedAt(now);

        module = createModule("Introdução", course);
        module.setCreatedAt(now);

        lesson1 = createLesson("Variáveis", module, 1);
        lesson1.setCreatedAt(now);

        lesson2 = createLesson("Condicionais", module, 2);
        lesson2.setCreatedAt(now);

        persistAll();
    }

    private void persistAll() {
        entityManager.persist(user);
        entityManager.persist(category);
        entityManager.persist(course);
        entityManager.persist(module);

        entityManager.persist(lesson1);
        entityManager.persist(lesson2);
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

    private Lesson createDeletedLesson(
            String name,
            Module module,
            int sequence
    ) {

        Lesson lesson = Lesson.create(
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

        lesson.delete();

        return lesson;
    }

    @Test
    void should_count_by_module_and_not_deleted() {
        Lesson deleted = createDeletedLesson("Loops", module, 3);
        deleted.setCreatedAt(now);

        entityManager.persist(deleted);

        long count = lessonRepository.countByModule_IdAndDeletedFalse(module.getId());

        assertThat(count).isEqualTo(2);
    }

    @Test
    void should_count_by_course_and_not_deleted() {
        long count = lessonRepository.countByModule_Course_IdAndDeletedFalse(course.getId());
        assertThat(count).isEqualTo(2);
    }

    @Test
    void should_find_all_not_deleted() {
        Lesson deleted = createDeletedLesson("Arrays", module, 3);
        deleted.setCreatedAt(now);

        entityManager.persist(deleted);

        List<Lesson> result = lessonRepository.findAllByDeletedFalse();

        assertThat(result).hasSize(2);
    }

    @Test
    void should_find_by_module_ordered_by_sequence() {
        List<Lesson> result = lessonRepository
                .findAllByModule_IdAndDeletedFalseOrderBySequenceAsc(module.getId());

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getSequence()).isEqualTo(1);
        assertThat(result.get(1).getSequence()).isEqualTo(2);
    }

    @Test
    void should_check_exists_by_module_and_sequence() {
        boolean exists = lessonRepository
                .existsByModuleIdAndSequence(module.getId(), 1);

        assertThat(exists).isTrue();
    }

    @Test
    void should_check_exists_by_module_and_name_ignore_case() {
        boolean exists = lessonRepository
                .existsByModuleIdAndNameIgnoreCase(module.getId(), "vArIáVeIs");

        assertThat(exists).isTrue();
    }
}