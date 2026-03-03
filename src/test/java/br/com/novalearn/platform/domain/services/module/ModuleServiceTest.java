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
import br.com.novalearn.platform.domain.entities.lesson.Lesson;
import br.com.novalearn.platform.domain.entities.module.Module;
import br.com.novalearn.platform.domain.entities.user.User;
import br.com.novalearn.platform.domain.repositories.course.CourseRepository;
import br.com.novalearn.platform.domain.repositories.lesson.LessonRepository;
import br.com.novalearn.platform.domain.repositories.module.ModuleRepository;
import br.com.novalearn.platform.domain.repositories.user.UserLessonProgressRepository;
import br.com.novalearn.platform.domain.services.auth.AuthService;
import br.com.novalearn.platform.infra.time.provider.TimeProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ModuleServiceTest {
    @Mock
    private AuthService authService;

    @Mock
    private ModuleRepository moduleRepository;

    @Mock
    private CourseRepository courseRepository;

    @Mock
    private LessonRepository lessonRepository;

    @Mock
    private UserLessonProgressRepository userLessonProgressRepository;

    @Mock
    private ModuleMapper moduleMapper;

    @Mock
    private LessonMapper lessonMapper;

    @Mock
    private TimeProvider timeProvider;

    @InjectMocks
    private ModuleService service;

    private Module module;
    private Course course;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.clearContext();
        module = mock(Module.class);
        course = mock(Course.class);
    }

    @Test
    void create_should_create_module() {
        Long userId = 5L;

        ModuleCreateRequestDTO dto = new ModuleCreateRequestDTO();
        dto.setCourseId(1L);

        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));

        when(course.getId()).thenReturn(1L);

        when(moduleMapper.toEntity(dto, course)).thenReturn(module);

        when(module.getName()).thenReturn("Module 1");
        when(module.getSequence()).thenReturn(1);

        when(moduleRepository.existsByCourseIdAndNameIgnoreCaseAndDeletedFalse(1L, "Module 1")).thenReturn(false);
        when(moduleRepository.existsByCourseIdAndSequenceAndDeletedFalse(1L, 1)).thenReturn(false);
        when(moduleRepository.save(module)).thenReturn(module);
        when(moduleMapper.toResponseDTO(module)).thenReturn(new ModuleResponseDTO());

        ModuleResponseDTO result = service.create(userId, dto);

        verify(module).attachToCourse(course);

        assertNotNull(result);
    }

    @Test
    void create_should_throw_when_course_not_found() {
        ModuleCreateRequestDTO dto = new ModuleCreateRequestDTO();
        dto.setCourseId(10L);

        when(courseRepository.findById(10L)).thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> service.create(5L, dto)
        );
    }

    @Test
    void create_should_throw_when_name_exists() {
        ModuleCreateRequestDTO dto = new ModuleCreateRequestDTO();
        dto.setCourseId(10L);

        when(courseRepository.findById(10L)).thenReturn(Optional.of(course));

        when(course.getId()).thenReturn(10L);

        when(moduleMapper.toEntity(dto, course)).thenReturn(module);

        when(module.getName()).thenReturn("Java");

        when(moduleRepository
                .existsByCourseIdAndNameIgnoreCaseAndDeletedFalse(10L, "Java"))
                .thenReturn(true);

        assertThrows(
                ValidationException.class,
                () -> service.create(5L, dto)
        );
    }

    @Test
    void update_should_update_module() {
        Long userId = 5L;

        ModuleUpdateRequestDTO dto = new ModuleUpdateRequestDTO();
        dto.setName("New name");

        when(moduleRepository.findById(6L)).thenReturn(Optional.of(module));

        when(module.getCourse()).thenReturn(course);

        when(course.isActive()).thenReturn(true);
        when(course.isDeleted()).thenReturn(false);
        when(course.getId()).thenReturn(1L);

        when(moduleRepository
                .existsByCourseIdAndNameIgnoreCaseAndDeletedFalse(1L, "New name"))
                .thenReturn(false);

        when(moduleRepository.save(module)).thenReturn(module);

        when(moduleMapper.toResponseDTO(module)).thenReturn(new ModuleResponseDTO());

        ModuleResponseDTO result = service.update(6L, dto, userId);

        verify(module).defineName("New name");

        assertNotNull(result);
    }

    @Test
    void update_should_throw_when_course_inactive() {
        ModuleUpdateRequestDTO dto = new ModuleUpdateRequestDTO();

        when(moduleRepository.findById(6L)).thenReturn(Optional.of(module));

        when(module.getCourse()).thenReturn(course);

        when(course.isActive()).thenReturn(false);

        assertThrows(
                InvalidStateException.class,
                () -> service.update(6L, dto, 5L)
        );
    }

    @Test
    void find_by_id_should_return() {
        when(moduleRepository.findById(6L)).thenReturn(Optional.of(module));

        when(module.isDeleted()).thenReturn(false);

        when(moduleMapper.toResponseDTO(module)).thenReturn(new ModuleResponseDTO());

        ModuleResponseDTO result = service.findById(6L);

        assertNotNull(result);
    }

    @Test
    void list_all_active_should_return_list() {
        when(moduleRepository.findAllByDeletedFalse()).thenReturn(List.of(mock(Module.class)));

        when(moduleMapper.toListResponseDTO(any())).thenReturn(new ModuleListResponseDTO());

        List<ModuleListResponseDTO> result = service.listAllActive();

        assertEquals(1, result.size());
    }

    @Test
    void get_module_progress_should_calculate_correctly() {
        User user = mock(User.class);

        when(authService.getAuthenticatedUserEntity()).thenReturn(user);

        when(user.getId()).thenReturn(5L);

        when(moduleRepository.findById(6L)).thenReturn(Optional.of(module));

        when(module.getId()).thenReturn(6L);
        when(module.getName()).thenReturn("Module");
        when(module.getCourse()).thenReturn(course);

        when(course.getId()).thenReturn(1L);

        when(lessonRepository
                .countByModule_IdAndDeletedFalse(6L))
                .thenReturn(6L);

        when(userLessonProgressRepository
                .countByUserIdAndLesson_Module_IdAndCompletedTrueAndDeletedFalse(5L, 6L))
                .thenReturn(5L);

        ModuleProgressResponseDTO result = service.getModuleProgress(6L);

        assertEquals(83, result.getProgressPercent());
        assertEquals(6, result.getTotalLessons());
        assertEquals(5, result.getCompletedLessons());
    }

    @Test
    void get_module_progress_should_return_zero_when_no_lessons() {
        User user = mock(User.class);

        when(authService.getAuthenticatedUserEntity()).thenReturn(user);

        when(user.getId()).thenReturn(5L);

        when(moduleRepository.findById(6L)).thenReturn(Optional.of(module));

        when(module.getCourse()).thenReturn(course);

        when(lessonRepository.countByModule_IdAndDeletedFalse(6L))
                .thenReturn(0L);

        when(userLessonProgressRepository
                .countByUserIdAndLesson_Module_IdAndCompletedTrueAndDeletedFalse(5L, 6L))
                .thenReturn(0L);

        ModuleProgressResponseDTO result = service.getModuleProgress(6L);

        assertEquals(0, result.getProgressPercent());
    }

    @Test
    void list_by_module_should_return_lessons() {
        when(moduleRepository.findById(6L)).thenReturn(Optional.of(mock(Module.class)));

        when(lessonRepository
                .findAllByModule_IdAndDeletedFalseOrderBySequenceAsc(6L))
                .thenReturn(List.of(mock(Lesson.class)));

        when(lessonMapper.toListResponseDTO(any())).thenReturn(new LessonListResponseDTO());

        List<LessonListResponseDTO> result = service.listByModule(6L);

        assertEquals(1, result.size());
    }
}