package br.com.novalearn.platform.domain.repositories.module;

import br.com.novalearn.platform.domain.entities.category.Category;
import br.com.novalearn.platform.domain.entities.course.Course;
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
public class ModuleRepositoryTest {
    @Autowired
    private ModuleRepository moduleRepository;

    @Autowired
    private TestEntityManager entityManager;

    private LocalDateTime now;

    private User user;
    private Category category;
    private Course course;

    private Module module1;
    private Module module2;

    @BeforeEach
    void setUp() {
        now = LocalDateTime.now();

        user = createUser("a@test.com", "11111111111");

        category = createCategory("Backend");
        category.setCreatedAt(now);

        course = createCourse("Java", category, user);
        course.setCreatedAt(now);

        module1 = createModule("Introduction", course, 1);
        module1.setCreatedAt(now);

        module2 = createModule("Basics", course, 2);
        module2.setCreatedAt(now);

        persistAll();
    }

    private void persistAll() {
        entityManager.persist(user);
        entityManager.persist(category);
        entityManager.persist(course);

        entityManager.persist(module1);
        entityManager.persist(module2);
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

    private Module createDeletedModule(
            String name,
            Course course,
            int sequence
    ) {

        Module module = Module.create(
                course,
                name,
                "description",
                sequence,
                "observations"
        );

        module.delete();

        return module;
    }

    @Test
    void should_find_all_not_deleted() {
        Module deleted = createDeletedModule("Advanced", course, 3);
        deleted.setCreatedAt(now);

        entityManager.persist(deleted);

        List<Module> result = moduleRepository.findAllByDeletedFalse();

        assertThat(result).hasSize(2);
    }

    @Test
    void should_find_by_course_ordered_by_sequence() {
        List<Module> result = moduleRepository
                .findAllByCourse_IdAndDeletedFalseOrderBySequenceAsc(course.getId());

        assertThat(result).hasSize(2);

        assertThat(result.get(0).getSequence()).isEqualTo(1);
        assertThat(result.get(1).getSequence()).isEqualTo(2);
    }

    @Test
    void should_check_exists_by_course_and_name_ignore_case() {
        boolean exists = moduleRepository
                .existsByCourseIdAndNameIgnoreCaseAndDeletedFalse(course.getId(), "iNtrOdUcTiOn");

        assertThat(exists).isTrue();
    }

    @Test
    void should_check_exists_by_course_and_sequence_and_not_deleted() {
        boolean exists = moduleRepository
                .existsByCourseIdAndSequenceAndDeletedFalse(course.getId(), 2);

        assertThat(exists).isTrue();
    }

    @Test
    void should_not_consider_deleted_when_checking_exists_by_name() {
        Module deleted = createDeletedModule("Extras", course, 3);
        deleted.setCreatedAt(now);

        entityManager.persist(deleted);

        boolean exists = moduleRepository
                .existsByCourseIdAndNameIgnoreCaseAndDeletedFalse(course.getId(), "extras");

        assertThat(exists).isFalse();
    }

    @Test
    void should_not_consider_deleted_when_checking_exists_by_sequence() {
        Module deleted = createDeletedModule("Extras", course, 3);
        deleted.setCreatedAt(now);

        entityManager.persist(deleted);

        boolean exists = moduleRepository
                .existsByCourseIdAndSequenceAndDeletedFalse(course.getId(), 3);

        assertThat(exists).isFalse();
    }

    @Test
    void should_check_exists_by_id_and_not_deleted() {
        boolean exists = moduleRepository.existsByIdAndDeletedFalse(module1.getId());
        assertThat(exists).isTrue();
    }

    @Test
    void should_not_find_deleted_by_id() {
        Module deleted = createDeletedModule("Final", course, 4);
        deleted.setCreatedAt(now);

        entityManager.persist(deleted);

        boolean exists = moduleRepository.existsByIdAndDeletedFalse(deleted.getId());

        assertThat(exists).isFalse();
    }
}