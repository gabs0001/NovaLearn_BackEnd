package br.com.novalearn.platform.domain.services.course;

import br.com.novalearn.platform.api.dtos.course.CourseCreateRequestDTO;
import br.com.novalearn.platform.api.dtos.course.CourseListResponseDTO;
import br.com.novalearn.platform.api.dtos.course.CourseResponseDTO;
import br.com.novalearn.platform.api.dtos.course.CourseUpdateRequestDTO;
import br.com.novalearn.platform.api.dtos.course.progress.CourseProgressResponseDTO;
import br.com.novalearn.platform.api.dtos.module.ModuleListResponseDTO;
import br.com.novalearn.platform.api.mappers.course.CourseMapper;
import br.com.novalearn.platform.api.mappers.module.ModuleMapper;
import br.com.novalearn.platform.domain.entities.category.Category;
import br.com.novalearn.platform.domain.entities.course.Course;
import br.com.novalearn.platform.domain.entities.user.User;
import br.com.novalearn.platform.domain.entities.user.UserCourse;
import br.com.novalearn.platform.domain.repositories.category.CategoryRepository;
import br.com.novalearn.platform.domain.repositories.course.CourseRepository;
import br.com.novalearn.platform.domain.repositories.lesson.LessonRepository;
import br.com.novalearn.platform.domain.repositories.module.ModuleRepository;
import br.com.novalearn.platform.domain.repositories.user.UserCourseRepository;
import br.com.novalearn.platform.domain.repositories.user.UserLessonProgressRepository;
import br.com.novalearn.platform.domain.services.BaseCrudService;
import br.com.novalearn.platform.domain.services.auth.AuthService;
import br.com.novalearn.platform.core.exception.business.*;
import br.com.novalearn.platform.infra.time.provider.TimeProvider;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CourseService extends BaseCrudService<Course> {
    private final AuthService authService;
    private final CourseRepository courseRepository;
    private final CategoryRepository categoryRepository;
    private final UserCourseRepository userCourseRepository;
    private final LessonRepository lessonRepository;
    private final UserLessonProgressRepository userLessonProgressRepository;
    private final ModuleRepository moduleRepository;
    private final CourseMapper courseMapper;
    private final ModuleMapper moduleMapper;

    public CourseService(
            AuthService authService,
            CourseRepository courseRepository,
            CategoryRepository categoryRepository,
            UserCourseRepository userCourseRepository,
            LessonRepository lessonRepository,
            UserLessonProgressRepository userLessonProgressRepository,
            ModuleRepository moduleRepository,
            CourseMapper courseMapper,
            ModuleMapper moduleMapper,
            TimeProvider timeProvider
    ) {
        super(courseRepository, "Course", timeProvider);
        this.authService = authService;
        this.courseRepository = courseRepository;
        this.categoryRepository = categoryRepository;
        this.userCourseRepository = userCourseRepository;
        this.lessonRepository = lessonRepository;
        this.userLessonProgressRepository = userLessonProgressRepository;
        this.moduleRepository = moduleRepository;
        this.courseMapper = courseMapper;
        this.moduleMapper = moduleMapper;
    }

    @Transactional
    public CourseResponseDTO create(Long userId, CourseCreateRequestDTO dto) {
        Category category = findCategoryOrThrow(dto.getCategoryId());
        User instructor = authService.getAuthenticatedUserEntity();

        Course course = Course.create(dto.getName(), category, instructor);
        course.auditCreate(userId, timeProvider.now());

        if(courseRepository.existsByNameIgnoreCaseAndDeletedFalse(course.getName())) {
            throw new ConflictException("Course already exists.");
        }

        return courseMapper.toResponseDTO(courseRepository.save(course));
    }

    @Transactional
    public CourseResponseDTO update(Long id, CourseUpdateRequestDTO dto, Long userId) {
        Course course = findEntityOrThrow(id);
        course.ensureNotDeleted();

        if (dto.getName() != null && courseRepository.existsByNameIgnoreCaseAndDeletedFalse(dto.getName()) &&
                !dto.getName().equalsIgnoreCase(course.getName())
        ) {
            throw new ConflictException("Course name already exists.");
        }

        course.updateInfo(dto.getName(), dto.getShortDescription(), dto.getLongDescription(), dto.getObservations());

        if(dto.getCategoryId() != null) {
            Category category = findCategoryOrThrow(dto.getCategoryId());
            course.changeCategory(category);
        }

        if(dto.getActive() != null) {
            if(dto.getActive()) course.activate();
            else course.deactivate();
        }

        if(Boolean.TRUE.equals(dto.getDeleted())) { course.delete(); }

        course.auditUpdate(userId, timeProvider.now());
        return courseMapper.toResponseDTO(courseRepository.save(course));
    }

    @Transactional
    public CourseResponseDTO findById(Long id) {
        Course entity = findEntityOrThrow(id);
        entity.ensureNotDeleted();

        return courseMapper.toResponseDTO(entity);
    }

    @Transactional
    public CourseResponseDTO findBySlug(String slug) {
        Course course = courseRepository.findBySlugAndDeletedFalse(slug)
                .orElseThrow(
                        () -> new ResourceNotFoundException("Course not found for slug: " + slug)
                );

        if(!course.isActive()) throw new ForbiddenOperationException("Course is not active.");

        return courseMapper.toResponseDTO(course);
    }

    @Transactional
    public List<CourseListResponseDTO> listAllActive() {
        return courseRepository.findAllByDeletedFalse()
                .stream()
                .map(courseMapper::toListResponseDTO)
                .toList();
    }

    @Transactional
    public List<CourseListResponseDTO> listByCategory(Long categoryId) {
        if(!categoryRepository.existsByIdAndDeletedFalse(categoryId)) {
            throw new ResourceNotFoundException("Category not found.");
        }

        return courseRepository.findAllByCategory_IdAndDeletedFalse(categoryId)
                .stream()
                .map(courseMapper::toListResponseDTO)
                .toList();
    }

    @Transactional
    public CourseProgressResponseDTO getCourseProgress(Long courseId) {
        User user = authService.getAuthenticatedUserEntity();

        UserCourse enrollment = userCourseRepository
                .findByUserIdAndCourseIdAndDeletedFalse(user.getId(), courseId)
                .orElseThrow(() ->
                        new ForbiddenOperationException("User is not enrolled in this course.")
                );

        long totalLessons = lessonRepository.countByModule_Course_IdAndDeletedFalse(courseId);

        long completedLessons = userLessonProgressRepository.countByUserIdAndLesson_Module_Course_IdAndCompletedTrueAndDeletedFalse(
                user.getId(), courseId);

        int progressPercent = totalLessons == 0 ? 0 : (int) ((completedLessons * 100) / totalLessons);

        CourseProgressResponseDTO dto = new CourseProgressResponseDTO();
        dto.setId(courseId);
        dto.setName(enrollment.getCourse().getName());
        dto.setTotalLessons((int) totalLessons);
        dto.setCompletedLessons((int) completedLessons);
        dto.setProgressPercent(progressPercent);
        dto.setEnrollmentStatus(enrollment.getEnrollmentStatus());
        dto.setEnrolledAt(enrollment.getEnrolledAt());
        dto.setCompletedAt(enrollment.getCompletedAt());

        return dto;
    }

    @Transactional
    public List<ModuleListResponseDTO> listByCourseSlug(String slug) {
        CourseResponseDTO course = findBySlug(slug);

        return moduleRepository.findAllByCourse_IdAndDeletedFalseOrderBySequenceAsc(course.getId())
                .stream()
                .map(moduleMapper::toListResponseDTO)
                .toList();
    }

    @Transactional
    public List<CourseListResponseDTO> listAll() {
        return courseRepository.findAll()
                .stream()
                .map(courseMapper::toListResponseDTO)
                .toList();
    }

    @Transactional
    public boolean existsByName(String name) {
        return courseRepository.existsByNameIgnoreCaseAndDeletedFalse(name.trim());
    }

    @Transactional
    public List<CourseListResponseDTO> listForSelect() {
        return courseRepository.findAllByActiveTrueAndDeletedFalse()
                .stream()
                .map(courseMapper::toListResponseDTO)
                .toList();
    }

    @Transactional
    public List<CourseListResponseDTO> listFeatured() {
        return courseRepository.findTop5ByActiveTrueAndDeletedFalseOrderByCreatedAtDesc()
                .stream()
                .map(courseMapper::toListResponseDTO)
                .toList();
    }

    private Category findCategoryOrThrow(Long id) {
        return categoryRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));
    }
}