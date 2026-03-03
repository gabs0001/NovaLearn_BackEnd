package br.com.novalearn.platform.domain.repositories.user;

import br.com.novalearn.platform.domain.entities.user.UserQuizAnswer;
import br.com.novalearn.platform.domain.enums.EnrollmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserQuizAnswerRepository extends JpaRepository<UserQuizAnswer, Long> {
    List<UserQuizAnswer> findAllByDeletedFalse();
    List<UserQuizAnswer> findAllByUserIdAndQuizQuestionIdInAndDeletedFalse(Long userId, List<Long> quizQuestionIds);
    List<UserQuizAnswer> findAllByUserIdAndQuizQuestionQuizIdAndDeletedFalse(Long userId, Long quizId);
    Optional<UserQuizAnswer> findByUserIdAndQuizQuestionIdAndDeletedFalse(Long userId, Long quizQuestionId);
    Optional<UserQuizAnswer> findByIdAndUserIdAndDeletedFalse(Long id, Long userId);
}