package br.com.novalearn.platform.domain.repositories.course;

import br.com.novalearn.platform.domain.entities.category.Category;
import br.com.novalearn.platform.domain.entities.course.Course;
import br.com.novalearn.platform.domain.entities.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
public class CourseRepositoryTest {
    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private TestEntityManager entityManager;

    private LocalDateTime now;

    private User user;

    private Category backendCategory;
    private Category frontendCategory;

    private Course javaCourse;
    private Course springCourse;
    private Course reactCourse;

    @BeforeEach
    void setUp() {
        now = LocalDateTime.now();

        user = createUser("a@test.com", "11111111111");

        backendCategory = createCategory("Backend");
        frontendCategory = createCategory("Frontend");

        backendCategory.setCreatedAt(now);
        frontendCategory.setCreatedAt(now);

        javaCourse = createCourse("Java Fundamentals", backendCategory);
        springCourse = createCourse("Spring Boot", backendCategory);
        reactCourse = createCourse("React Basics", frontendCategory);

        javaCourse.setCreatedAt(now);
        springCourse.setCreatedAt(now);
        reactCourse.setCreatedAt(now);

        persistAll();
    }

    private void persistAll() {
        entityManager.persist(user);
        entityManager.persist(backendCategory);
        entityManager.persist(frontendCategory);
        entityManager.persist(javaCourse);
        entityManager.persist(springCourse);
        entityManager.persist(reactCourse);

        entityManager.flush();
        entityManager.clear();
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

    private Category createCategory(String name) {
        return Category.create(
                name,
                "BCK",
                "Some Description",
                "Observations"
        );
    }

    private Course createCourse(String name, Category category) {
        return Course.create(name, category, user);
    }

    private Course createInactiveCourse(String name, Category category) {
        Course course = Course.create(name, category, user);

        course.deactivate();

        return course;
    }

    private Course createDeletedCourse(String name, Category category) {
        Course course = Course.create(name, category, user);

        course.delete();

        return course;
    }

    @Test
    void should_check_if_name_exists_ignore_case_and_not_deleted() {
        boolean exists = courseRepository.existsByNameIgnoreCaseAndDeletedFalse("java fundamentals");
        assertThat(exists).isTrue();
    }

    @Test
    void should_not_consider_deleted_when_checking_name_exists() {
        Course deleted = createDeletedCourse("Docker basics", backendCategory);
        deleted.setCreatedAt(now);

        entityManager.persist(deleted);

        boolean exists = courseRepository.existsByNameIgnoreCaseAndDeletedFalse("docker basics");

        assertThat(exists).isFalse();
    }

    @Test
    void should_find_by_id_and_not_deleted() {
        Optional<Course> result = courseRepository.findByIdAndDeletedFalse(javaCourse.getId());
        assertThat(result).isPresent();
    }

    @Test
    void should_not_find_deleted_by_id() {
        Course deleted = createDeletedCourse("Kotlin", backendCategory);
        deleted.setCreatedAt(now);

        entityManager.persist(deleted);

        Optional<Course> result = courseRepository.findByIdAndDeletedFalse(deleted.getId());

        assertThat(result).isEmpty();
    }

    @Test
    void should_find_all_active_courses() {
        Course deleted = createDeletedCourse("NodeJS", backendCategory);
        deleted.setCreatedAt(now);

        entityManager.persist(deleted);

        List<Course> result = courseRepository.findAllByDeletedFalse();

        assertThat(result).hasSize(3);
    }

    @Test
    void should_find_all_active_courses_with_pagination() {
        Page<Course> page = courseRepository.findAllByDeletedFalse(PageRequest.of(0, 2));

        assertThat(page.getContent()).hasSize(2);
        assertThat(page.getTotalElements()).isEqualTo(3);
    }

    @Test
    void should_find_only_active_courses() {
        Course inactive = createInactiveCourse("VueJs", frontendCategory);
        inactive.setCreatedAt(now);

        entityManager.persist(inactive);

        List<Course> result = courseRepository.findAllByActiveTrueAndDeletedFalse();

        assertThat(result).hasSize(3);
    }

    @Test
    void should_find_top_5_active_ordered_by_created_at_desc() {
        for(int i = 1; i <= 5; i++) {
            Course course = createCourse("Extra " + i, backendCategory);
            course.setCreatedAt(now);
            entityManager.persist(course);
        }

        List<Course> result = courseRepository
                .findTop5ByActiveTrueAndDeletedFalseOrderByCreatedAtDesc();

        assertThat(result).hasSize(5);

        assertThat(result.get(0).getCreatedAt())
                .isAfterOrEqualTo(result.get(1).getCreatedAt());
    }

    @Test
    void should_find_by_category_and_not_deleted() {
        List<Course> result = courseRepository
                .findAllByCategory_IdAndDeletedFalse(backendCategory.getId());

        assertThat(result).hasSize(2);
    }

    @Test
    void should_find_by_category_and_not_deleted_with_pagination() {
        Page<Course> page = courseRepository
                .findAllByCategory_IdAndDeletedFalse(backendCategory.getId(), PageRequest.of(0, 1));

        assertThat(page.getTotalElements()).isEqualTo(2);
        assertThat(page.getContent()).hasSize(1);
    }

    @Test
    void should_find_by_slug_and_not_deleted() {
        String slug = javaCourse.getSlug();

        Optional<Course> result = courseRepository.findBySlugAndDeletedFalse(slug);

        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(javaCourse.getId());
    }

    @Test
    void should_not_find_deleted_by_slug() {
        Course deleted = createDeletedCourse("Rust", backendCategory);
        deleted.setCreatedAt(now);

        entityManager.persist(deleted);

        Optional<Course> result = courseRepository
                .findBySlugAndDeletedFalse(deleted.getSlug());

        assertThat(result).isEmpty();
    }

    @Test
    void should_find_by_name_ignore_case_and_not_deleted() {
        Optional<Course> result = courseRepository
                .findByNameIgnoreCaseAndDeletedFalse("jAvA fuNDaMenTaLs");

        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(javaCourse.getId());
    }

    @Test
    void should_not_find_deleted_by_name() {
        Course deleted = createDeletedCourse("Python", backendCategory);
        deleted.setCreatedAt(now);

        entityManager.persist(deleted);

        Optional<Course> result = courseRepository
                .findByNameIgnoreCaseAndDeletedFalse("python");

        assertThat(result).isEmpty();
    }
}