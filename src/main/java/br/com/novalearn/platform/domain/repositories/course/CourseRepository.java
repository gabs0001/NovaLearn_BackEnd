package br.com.novalearn.platform.domain.repositories.course;

import br.com.novalearn.platform.domain.entities.course.Course;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {
    boolean existsByNameIgnoreCaseAndDeletedFalse(String name);
    Optional<Course> findByIdAndDeletedFalse(Long courseId);
    List<Course> findAllByDeletedFalse();
    Page<Course> findAllByDeletedFalse(Pageable pageable);
    List<Course> findAllByActiveTrueAndDeletedFalse();
    List<Course> findTop5ByActiveTrueAndDeletedFalseOrderByCreatedAtDesc();
    List<Course> findAllByCategory_IdAndDeletedFalse(Long categoryId);
    Page<Course> findAllByCategory_IdAndDeletedFalse(Long categoryId, Pageable pageable);
    Optional<Course> findBySlugAndDeletedFalse(String slug);
    Optional<Course> findByNameIgnoreCaseAndDeletedFalse(String name);
}