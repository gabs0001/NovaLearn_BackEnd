package br.com.novalearn.platform.domain.services.module;

import br.com.novalearn.platform.api.dtos.lesson.LessonListResponseDTO;
import br.com.novalearn.platform.api.dtos.module.ModuleCreateRequestDTO;
import br.com.novalearn.platform.api.dtos.module.ModuleListResponseDTO;
import br.com.novalearn.platform.api.dtos.module.ModuleResponseDTO;
import br.com.novalearn.platform.api.dtos.module.ModuleUpdateRequestDTO;
import br.com.novalearn.platform.api.dtos.module.progress.ModuleProgressResponseDTO;
import br.com.novalearn.platform.api.mappers.lesson.LessonMapper;
import br.com.novalearn.platform.api.mappers.module.ModuleMapper;
import br.com.novalearn.platform.core.exception.business.InvalidStateException;
import br.com.novalearn.platform.core.exception.business.ResourceNotFoundException;
import br.com.novalearn.platform.core.exception.business.ValidationException;
import br.com.novalearn.platform.domain.entities.course.Course;
import br.com.novalearn.platform.domain.entities.module.Module;
import br.com.novalearn.platform.domain.entities.user.User;
import br.com.novalearn.platform.domain.repositories.course.CourseRepository;
import br.com.novalearn.platform.domain.repositories.lesson.LessonRepository;
import br.com.novalearn.platform.domain.repositories.module.ModuleRepository;
import br.com.novalearn.platform.domain.repositories.user.UserLessonProgressRepository;
import br.com.novalearn.platform.domain.services.BaseCrudService;
import br.com.novalearn.platform.domain.services.auth.AuthService;
import br.com.novalearn.platform.infra.time.provider.TimeProvider;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ModuleService extends BaseCrudService<Module> {
    private final AuthService authService;
    private final ModuleRepository moduleRepository;
    private final CourseRepository courseRepository;
    private final LessonRepository lessonRepository;
    private final UserLessonProgressRepository userLessonProgressRepository;
    private final ModuleMapper moduleMapper;
    private final LessonMapper lessonMapper;

    public ModuleService(
            AuthService authService,
            ModuleRepository moduleRepository,
            CourseRepository courseRepository,
            LessonRepository lessonRepository,
            UserLessonProgressRepository userLessonProgressRepository,
            ModuleMapper moduleMapper,
            LessonMapper lessonMapper,
            TimeProvider timeProvider
    ) {
        super(moduleRepository, "Module", timeProvider);
        this.authService = authService;
        this.moduleRepository = moduleRepository;
        this.courseRepository = courseRepository;
        this.lessonRepository = lessonRepository;
        this.userLessonProgressRepository = userLessonProgressRepository;
        this.moduleMapper = moduleMapper;
        this.lessonMapper = lessonMapper;
    }

    @Transactional
    public ModuleResponseDTO create(Long userId, ModuleCreateRequestDTO dto) {
        Course course = courseRepository.findById(dto.getCourseId())
                .orElseThrow(() -> new ResourceNotFoundException("Course not found."));

        Module module = moduleMapper.toEntity(dto, course);
        applyAuditCreate(module, userId);

        if(moduleRepository.existsByCourseIdAndNameIgnoreCaseAndDeletedFalse(
                course.getId(), module.getName())) {
            throw new ValidationException("A module with this name already exists in this course.");
        }

        if(module.getSequence() != null && moduleRepository.existsByCourseIdAndSequenceAndDeletedFalse(course.getId(), module.getSequence())) {
            throw new ValidationException("This course already contains a module with this sequence.");
        }

        return moduleMapper.toResponseDTO(moduleRepository.save(module));
    }

    @Transactional
    public ModuleResponseDTO update(Long id, ModuleUpdateRequestDTO dto, Long userId) {
        Module module = findEntityOrThrow(id);
        module.ensureNotDeleted();

        if(!module.getCourse().isActive() || module.getCourse().isDeleted()) {
            throw new InvalidStateException("Module cannot be updated because its course is inactive or deleted.");
        }

        if(dto.getName() != null) {
            if(moduleRepository.existsByCourseIdAndNameIgnoreCaseAndDeletedFalse(
                    module.getCourse().getId(), dto.getName().trim())
                    && !dto.getName().equalsIgnoreCase(module.getName())
            ) {
                throw new ValidationException("Another module with this name already exists in this course.");
            }
            module.defineName(dto.getName());
        }

        if(dto.getSequence() != null) {
            if(moduleRepository.existsByCourseIdAndSequenceAndDeletedFalse(
                    module.getCourse().getId(), dto.getSequence())
                    && !dto.getSequence().equals(module.getSequence())) {
                throw new ValidationException("This course already contains a module with this sequence.");
            }
            module.defineSequence(dto.getSequence());
        }

        if(dto.getDescription() != null) module.defineDescription(dto.getDescription());

        if(dto.getObservations() != null) module.defineObservations(dto.getObservations());

        applyAuditUpdate(module, userId);

        return moduleMapper.toResponseDTO(moduleRepository.save(module));
    }

    @Transactional
    public ModuleResponseDTO findById(Long id) {
        Module entity = findEntityOrThrow(id);

        if(entity.isDeleted()) throw new ResourceNotFoundException("Module not found.");

        return moduleMapper.toResponseDTO(entity);
    }

    @Transactional
    public List<ModuleListResponseDTO> listAllActive() {
        return moduleRepository.findAllByDeletedFalse()
                .stream()
                .map(moduleMapper::toListResponseDTO)
                .toList();
    }

    @Transactional
    public ModuleProgressResponseDTO getModuleProgress(Long moduleId) {
        User user = authService.getAuthenticatedUserEntity();

        Module module = moduleRepository.findById(moduleId)
                .orElseThrow(() -> new ResourceNotFoundException("Module not found."));

        long totalLessons = lessonRepository.countByModule_IdAndDeletedFalse(moduleId);
        long completedLessons =
                userLessonProgressRepository.countByUserIdAndLesson_Module_IdAndCompletedTrueAndDeletedFalse(
                        user.getId(),
                        moduleId
                );

        int progress = totalLessons == 0 ? 0 : (int) Math.round((completedLessons * 100.0) / totalLessons);

        ModuleProgressResponseDTO dto = new ModuleProgressResponseDTO();
        dto.setModuleId(module.getId());
        dto.setCourseId(module.getCourse().getId());
        dto.setName(module.getName());
        dto.setTotalLessons((int) totalLessons);
        dto.setCompletedLessons((int) completedLessons);
        dto.setProgressPercent(progress);

        return dto;
    }

    @Transactional
    public List<LessonListResponseDTO> listByModule(Long moduleId) {
        findEntityOrThrow(moduleId);

        return lessonRepository.findAllByModule_IdAndDeletedFalseOrderBySequenceAsc(moduleId)
                .stream()
                .map(lessonMapper::toListResponseDTO)
                .toList();
    }
}