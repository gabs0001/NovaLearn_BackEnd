package br.com.novalearn.platform.domain.repositories.lesson;

import br.com.novalearn.platform.domain.entities.lesson.Lesson;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LessonRepository extends JpaRepository<Lesson, Long> {
    long countByModule_IdAndDeletedFalse(Long moduleId);
    long countByModule_Course_IdAndDeletedFalse(Long courseId);
    List<Lesson> findAllByDeletedFalse();
    List<Lesson> findAllByModule_IdAndDeletedFalseOrderBySequenceAsc(Long moduleId);
    boolean existsByModuleIdAndSequence(Long moduleId, Integer sequence);
    boolean existsByModuleIdAndNameIgnoreCase(Long moduleId, String name);
}