package br.com.novalearn.platform.domain.repositories.module;

import br.com.novalearn.platform.domain.entities.module.Module;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ModuleRepository extends JpaRepository<Module, Long> {
    List<Module> findAllByDeletedFalse();
    List<Module> findAllByCourse_IdAndDeletedFalseOrderBySequenceAsc(Long courseId);
    boolean existsByCourseIdAndNameIgnoreCaseAndDeletedFalse(Long courseId, String name);
    boolean existsByCourseIdAndSequenceAndDeletedFalse(Long courseId, Integer sequence);
    boolean existsByIdAndDeletedFalse(Long id);
}