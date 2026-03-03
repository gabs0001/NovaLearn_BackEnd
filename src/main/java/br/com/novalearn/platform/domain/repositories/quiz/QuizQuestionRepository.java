package br.com.novalearn.platform.domain.repositories.quiz;

import br.com.novalearn.platform.domain.entities.quiz.QuizQuestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuizQuestionRepository extends JpaRepository<QuizQuestion, Long> {
    List<QuizQuestion> findAllByDeletedFalse();
    List<QuizQuestion> findAllByQuizIdAndDeletedFalseOrderBySequenceAsc(Long quizId);
    List<QuizQuestion> findAllByQuizIdAndDeletedFalseAndActiveTrueOrderBySequenceAsc(Long quizId);
    boolean existsByQuizIdAndSequenceAndDeletedFalse(Long quizId, Integer sequence);
    boolean existsByQuizIdAndIdNotAndSequenceAndDeletedFalse(Long quizId, Long id, Integer sequence);
    long countByQuizIdAndDeletedFalse(Long quizId);
}