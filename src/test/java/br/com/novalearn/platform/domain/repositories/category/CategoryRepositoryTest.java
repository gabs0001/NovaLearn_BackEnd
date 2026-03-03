package br.com.novalearn.platform.domain.repositories.category;

import br.com.novalearn.platform.domain.entities.category.Category;
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
public class CategoryRepositoryTest {
    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private TestEntityManager entityManager;

    private Category activeCategory;
    private Category inactiveCategory;
    private Category deletedCategory;

    @BeforeEach
    void setUp() {
        activeCategory = createCategory("Backend", true);
        inactiveCategory = createCategory("Frontend", false);
        deletedCategory = createDeletedCategory("DevOps");

        activeCategory.setCreatedAt(LocalDateTime.now());
        inactiveCategory.setCreatedAt(LocalDateTime.now());
        deletedCategory.setCreatedAt(LocalDateTime.now());

        persistAll();
    }

    private void persistAll() {
        entityManager.persist(activeCategory);
        entityManager.persist(inactiveCategory);
        entityManager.persist(deletedCategory);
    }

    private Category createCategory(String name, boolean active) {
        Category category = Category.create(
                name,
                "BCK",
                "Some Description",
                "Observations"
        );

        if(!active) category.deactivate();

        return category;
    }

    private Category createDeletedCategory(String name) {
        Category category = Category.create(
                name,
                "BCK",
                "Some Description",
                "Observations"
        );

        category.delete();

        return category;
    }

    @Test
    void should_find_all_not_deleted() {
        List<Category> result = categoryRepository.findAllByDeletedFalse();
        assertThat(result).hasSize(2);
    }

    @Test
    void should_find_all_not_deleted_with_pagination() {
        Page<Category> page = categoryRepository.findAllByDeletedFalse(
                PageRequest.of(0, 10));

        assertThat(page.getTotalElements()).isEqualTo(2);
    }

    @Test
    void should_find_all_active_and_not_deleted() {
        List<Category> result = categoryRepository.findAllByActiveTrueAndDeletedFalse();

        assertThat(result).hasSize(1);

        assertThat(result.getFirst().isActive()).isTrue();
    }

    @Test
    void should_find_by_id_and_not_deleted() {
        Optional<Category> result = categoryRepository.findByIdAndDeletedFalse(activeCategory.getId());
        assertThat(result).isPresent();
    }

    @Test
    void should_not_find_deleted_by_id() {
        Optional<Category> result = categoryRepository.findByIdAndDeletedFalse(deletedCategory.getId());
        assertThat(result).isEmpty();
    }

    @Test
    void should_find_by_name_ignore_case() {
        Optional<Category> result = categoryRepository.findByNameIgnoreCaseAndDeletedFalse("bAcKeNd");

        assertThat(result).isPresent();

        assertThat(result.get().getName()).isEqualTo("Backend");
    }

    @Test
    void should_check_exists_by_name_ignore_case() {
        boolean exists = categoryRepository.existsByNameIgnoreCaseAndDeletedFalse("backend");
        assertThat(exists).isTrue();
    }

    @Test
    void should_not_consider_deleted_when_checking_exists() {
        boolean exists = categoryRepository.existsByNameIgnoreCaseAndDeletedFalse("devops");
        assertThat(exists).isFalse();
    }

    @Test
    void should_check_exists_by_id_and_not_deleted() {
        boolean exists = categoryRepository.existsByIdAndDeletedFalse(activeCategory.getId());
        assertThat(exists).isTrue();
    }

    @Test
    void should_not_consider_deleted_when_checking_exists_by_id() {
        boolean exists = categoryRepository.existsByIdAndDeletedFalse(deletedCategory.getId());
        assertThat(exists).isFalse();
    }
}