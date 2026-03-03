package br.com.novalearn.platform.domain.repositories.quiz;

import br.com.novalearn.platform.domain.entities.quiz.QuizAnswerOption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuizAnswerOptionRepository extends JpaRepository<QuizAnswerOption, Long> {
    List<QuizAnswerOption> findAllByDeletedFalse();
    List<QuizAnswerOption> findAllByQuizQuestionIdAndDeletedFalse(Long questionId);
    List<QuizAnswerOption> findAllByQuizQuestionIdAndDeletedFalseOrderBySequenceAsc(Long questionId);
    boolean existsByQuizQuestionIdAndSequenceAndDeletedFalse(Long questionId, Integer sequence);
    boolean existsByQuizQuestionIdAndIdNotAndSequenceAndDeletedFalse(Long questionId, Long id, Integer sequence);
    boolean existsByQuizQuestionIdAndCorrectTrueAndDeletedFalse(Long questionId);
    boolean existsByQuizQuestionIdAndIdNotAndCorrectTrueAndDeletedFalse(Long questionId, Long id);
}