package br.com.novalearn.platform.domain.repositories.user;

import br.com.novalearn.platform.domain.entities.user.UserLessonProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserLessonProgressRepository extends JpaRepository<UserLessonProgress, Long> {
    List<UserLessonProgress> findAllByDeletedFalse();
    List<UserLessonProgress> findAllByUserIdAndDeletedFalse(Long userId);
    Optional<UserLessonProgress> findByUserIdAndLessonIdAndDeletedFalse(Long userId, Long lessonId);
    boolean existsByUserIdAndLessonIdAndDeletedFalse(Long userId, Long lessonId);
    long countByUserIdAndLesson_Module_IdAndCompletedTrueAndDeletedFalse(Long userId, Long moduleId);
    long countByUserIdAndLesson_Module_IdAndDeletedFalse(Long userId, Long moduleId);
    long countByUserIdAndLesson_Module_Course_IdAndCompletedTrueAndDeletedFalse(Long userId, Long courseId);
}