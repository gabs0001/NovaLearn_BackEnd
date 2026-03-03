package br.com.novalearn.platform.domain.repositories.category;

import br.com.novalearn.platform.domain.entities.category.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    List<Category> findAllByDeletedFalse();
    Page<Category> findAllByDeletedFalse(Pageable pageable);
    List<Category> findAllByActiveTrueAndDeletedFalse();
    Optional<Category> findByIdAndDeletedFalse(Long id);
    Optional<Category> findByNameIgnoreCaseAndDeletedFalse(String name);
    boolean existsByNameIgnoreCaseAndDeletedFalse(String name);
    boolean existsByIdAndDeletedFalse(Long id);
}