package br.com.novalearn.platform.domain.services.lesson;

import br.com.novalearn.platform.api.dtos.lesson.LessonCreateRequestDTO;
import br.com.novalearn.platform.api.dtos.lesson.LessonListResponseDTO;
import br.com.novalearn.platform.api.dtos.lesson.LessonResponseDTO;
import br.com.novalearn.platform.api.dtos.lesson.LessonUpdateRequestDTO;
import br.com.novalearn.platform.api.mappers.lesson.LessonMapper;
import br.com.novalearn.platform.core.exception.business.ResourceNotFoundException;
import br.com.novalearn.platform.core.exception.business.ValidationException;
import br.com.novalearn.platform.domain.entities.lesson.Lesson;
import br.com.novalearn.platform.domain.entities.module.Module;
import br.com.novalearn.platform.domain.repositories.lesson.LessonRepository;
import br.com.novalearn.platform.domain.repositories.module.ModuleRepository;
import br.com.novalearn.platform.domain.services.BaseCrudService;
import br.com.novalearn.platform.infra.time.provider.TimeProvider;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LessonService extends BaseCrudService<Lesson> {
    private final LessonRepository lessonRepository;
    private final ModuleRepository moduleRepository;
    private final LessonMapper lessonMapper;

    public LessonService(
            LessonRepository lessonRepository,
            ModuleRepository moduleRepository,
            LessonMapper lessonMapper,
            TimeProvider timeProvider
    ) {
        super(lessonRepository, "Lesson", timeProvider);
        this.lessonRepository = lessonRepository;
        this.moduleRepository = moduleRepository;
        this.lessonMapper = lessonMapper;
    }

    @Transactional
    public LessonResponseDTO create(Long userId, LessonCreateRequestDTO dto) {
        Module module = findModuleOrThrow(dto.getModuleId());

        if(lessonRepository.existsByModuleIdAndSequence(dto.getModuleId(), dto.getSequence())) {
            throw new ValidationException("This module already contains a lesson with this sequence.");
        }

        if(lessonRepository.existsByModuleIdAndNameIgnoreCase(dto.getModuleId(), dto.getName().trim())) {
            throw new ValidationException("A lesson with this name already exists in the module.");
        }

        Lesson lesson = lessonMapper.toEntity(dto, module);
        applyAuditCreate(lesson, userId);

        return lessonMapper.toResponseDTO(lessonRepository.save(lesson));
    }

    @Transactional
    public LessonResponseDTO update(Long id, LessonUpdateRequestDTO dto, Long userId) {
        Lesson lesson = findEntityOrThrow(id);

        lesson.ensureNotDeleted();

        if(dto.getName() != null) {
            boolean changed = !dto.getName().equalsIgnoreCase(lesson.getName()) &&
                    lessonRepository.existsByModuleIdAndNameIgnoreCase(
                            lesson.getModule().getId(),
                            dto.getName()
                    );
            if(changed) throw new ValidationException("Another lesson with this name already exists in this module.");

            lesson.changeName(dto.getName());
        }

        if(dto.getSequence() != null) {
            boolean changed = !dto.getSequence().equals(lesson.getSequence()) &&
                    lessonRepository.existsByModuleIdAndSequence(
                            lesson.getModule().getId(),
                            dto.getSequence()
                    );
            if(changed) throw new ValidationException("This module already contains a lesson with this sequence.");

            lesson.changeSequence(dto.getSequence());
        }

        if(dto.getDurationSeconds() != null) lesson.changeDuration(dto.getDurationSeconds());
        if(dto.getRequireCompletion() != null) lesson.changeRequireCompletion(dto.getRequireCompletion());
        if(dto.getVisible() != null) lesson.changeVisibility(dto.getVisible());
        if(dto.getDescription() != null) lesson.changeDescription(dto.getDescription());
        if(dto.getPreviewUrl() != null) lesson.changePreviewUrl(dto.getPreviewUrl());
        if(dto.getNotes() != null) lesson.changeNotes(dto.getNotes());
        if(dto.getObservations() != null) lesson.changeObservations(dto.getObservations());

        applyAuditUpdate(lesson, userId);

        return lessonMapper.toResponseDTO(lesson);
    }

    @Transactional
    public LessonResponseDTO findById(Long id) {
        Lesson entity = findEntityOrThrow(id);

        entity.ensureNotDeleted();

        return lessonMapper.toResponseDTO(entity);
    }

    @Transactional
    public List<LessonListResponseDTO> listAllActive() {
        return lessonRepository.findAllByDeletedFalse()
                .stream()
                .map(lessonMapper::toListResponseDTO)
                .toList();
    }

    @Transactional
    public List<LessonListResponseDTO> listByModule(Long moduleId) {
        if(!moduleRepository.existsById(moduleId)) {
            throw new ResourceNotFoundException("Module not found.");
        }

        return lessonRepository.findAllByModule_IdAndDeletedFalseOrderBySequenceAsc(moduleId)
                .stream()
                .map(lessonMapper::toListResponseDTO)
                .toList();
    }

    @Transactional
    public List<LessonListResponseDTO> listAll() {
        return lessonRepository.findAll()
                .stream()
                .map(lessonMapper::toListResponseDTO)
                .toList();
    }

    @Transactional
    public boolean existsSequence(Long moduleId, Integer sequence) { return lessonRepository.existsByModuleIdAndSequence(moduleId, sequence); }

    @Transactional
    public void reorder(Long moduleId, List<Long> lessonIds, Long userId) {
        int sequence = 1;

        for(Long lessonId : lessonIds) {
            Lesson lesson = lessonRepository.findById(lessonId)
                    .orElseThrow(() -> new ResourceNotFoundException("Lesson not found."));

            if(!lesson.getModule().getId().equals(moduleId)) {
                throw new ValidationException("Lesson does not belong to module.");
            }

            lesson.changeSequence(sequence++);
            applyAuditUpdate(lesson, userId);
        }
    }

    private Module findModuleOrThrow(Long id) {
        return moduleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Module not found."));
    }
}