package br.com.novalearn.platform.domain.repositories.quiz;

import br.com.novalearn.platform.domain.entities.quiz.Quiz;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuizRepository extends JpaRepository<Quiz, Long> {
    List<Quiz> findAllByDeletedFalse();
    List<Quiz> findAllByModuleIdAndDeletedFalse(Long moduleId);
    boolean existsByModuleIdAndNameIgnoreCaseAndDeletedFalse(Long moduleId, String name);
    boolean existsByModuleIdAndNameIgnoreCaseAndIdNotAndDeletedFalse(Long moduleId, String name, Long id);
}