package br.com.novalearn.platform.domain.repositories.user;

import br.com.novalearn.platform.domain.entities.user.UserQuizAttempt;
import br.com.novalearn.platform.domain.enums.QuizAttemptStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserQuizAttemptRepository extends JpaRepository<UserQuizAttempt, Long> {
    List<UserQuizAttempt> findAllByDeletedFalse();
    List<UserQuizAttempt> findAllByUserIdAndDeletedFalseOrderByStartedAtDesc(Long userId);
    List<UserQuizAttempt> findAllByUserIdAndQuizIdAndDeletedFalse(Long userId, Long quizId);
    List<UserQuizAttempt> findAllByQuizIdAndUserIdAndDeletedFalseOrderByCreatedAtAsc(Long quizId, Long userId);
    Optional<UserQuizAttempt> findByIdAndUserId(Long id, Long userId);
    Optional<UserQuizAttempt> findByIdAndUserIdAndDeletedFalse(Long id, Long userId);
    boolean existsByUserIdAndQuizIdAndStatusAndDeletedFalse(Long userId, Long quizId, QuizAttemptStatus status);

    @Query("SELECT COUNT(a) > 0 FROM UserQuizAttempt a " +
            "WHERE a.user.id = :userId " +
            "AND a.quiz.module.course.id = :courseId " +
            "AND a.status IN :statuses " +
            "AND a.deleted = false")
    boolean existsByUserIdAndCourseIdAndStatusIn(
            @Param("userId") Long userId,
            @Param("courseId") Long courseId,
            @Param("statuses") List<QuizAttemptStatus> statuses
    );

    long countByUserIdAndQuizIdAndDeletedFalse(Long userId, Long quizId);
    long countByUserIdAndDeletedFalse(Long userId);
    long countByUserIdAndPassedTrueAndDeletedFalse(Long userId);

    @Query("SELECT COUNT(a) FROM UserQuizAnswer a " +
            "WHERE a.user.id = :userId " +
            "AND a.quizQuestion.quiz.id = :quizId " +
            "AND a.deleted = false")
    long countAnswersByUserAndQuiz(@Param("userId") Long userId, @Param("quizId") Long quizId);

    @Query("SELECT COUNT(a) FROM UserQuizAnswer a " +
            "WHERE a.user.id = :userId " +
            "AND a.quizQuestion.quiz.id = :quizId " +
            "AND a.correct = true " +
            "AND a.deleted = false")
    long countCorrectAnswersByUserAndQuiz(@Param("userId") Long userId, @Param("quizId") Long quizId);

    @Query("SELECT MAX(a.startedAt) FROM UserQuizAttempt a WHERE a.user.id = :userId AND a.deleted = false")
    Optional<LocalDateTime> findLastActivityAtByUserId(@Param("userId") Long userId);
}