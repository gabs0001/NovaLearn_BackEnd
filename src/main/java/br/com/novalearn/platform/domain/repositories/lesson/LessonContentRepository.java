package br.com.novalearn.platform.domain.repositories.lesson;

import br.com.novalearn.platform.domain.entities.lesson.LessonContent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LessonContentRepository extends JpaRepository<LessonContent, Long> {
    List<LessonContent> findAllByDeletedFalse();
    Optional<LessonContent> findByLessonIdAndDeletedFalse(Long lessonId);
    boolean existsByLessonIdAndMainContentTrue(Long lessonId);
    long countByLessonIdAndActiveTrueAndDeletedFalse(Long lessonId);
    Optional<LessonContent> findByLessonIdAndMainContentTrueAndDeletedFalse(Long lessonId);
}