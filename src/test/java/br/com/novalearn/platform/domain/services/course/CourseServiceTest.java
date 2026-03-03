package br.com.novalearn.platform.domain.services.course;

import br.com.novalearn.platform.api.dtos.course.CourseCreateRequestDTO;
import br.com.novalearn.platform.api.dtos.course.CourseResponseDTO;
import br.com.novalearn.platform.api.dtos.course.CourseUpdateRequestDTO;
import br.com.novalearn.platform.api.dtos.course.progress.CourseProgressResponseDTO;
import br.com.novalearn.platform.api.mappers.course.CourseMapper;
import br.com.novalearn.platform.api.mappers.module.ModuleMapper;
import br.com.novalearn.platform.core.exception.business.ConflictException;
import br.com.novalearn.platform.core.exception.business.ForbiddenOperationException;
import br.com.novalearn.platform.core.exception.business.ResourceNotFoundException;
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
import br.com.novalearn.platform.domain.services.auth.AuthService;
import br.com.novalearn.platform.infra.time.provider.TimeProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class CourseServiceTest {
    @Mock
    private AuthService authService;

    @Mock
    private CourseRepository courseRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private UserCourseRepository userCourseRepository;

    @Mock
    private LessonRepository lessonRepository;

    @Mock
    private UserLessonProgressRepository userLessonProgressRepository;

    @Mock
    private ModuleRepository moduleRepository;

    @Mock
    private CourseMapper courseMapper;

    @Mock
    private ModuleMapper moduleMapper;

    @Mock
    private TimeProvider timeProvider;

    @InjectMocks
    private CourseService courseService;

    private CourseCreateRequestDTO createRequestDTO;
    private Course course;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.clearContext();
        createRequestDTO = mock(CourseCreateRequestDTO.class);
        course = mock(Course.class);
    }

    @Test
    void should_create_course() {
        when(createRequestDTO.getCategoryId()).thenReturn(10L);
        when(createRequestDTO.getName()).thenReturn("Java");

        Category category = mock(Category.class);

        when(categoryRepository.findByIdAndDeletedFalse(10L)).thenReturn(Optional.of(category));

        User instructor = mock(User.class);

        when(authService.getAuthenticatedUserEntity()).thenReturn(instructor);

        try (MockedStatic<Course> mocked = mockStatic(Course.class)) {
            mocked.when(() -> Course.create("Java", category, instructor)).thenReturn(course);

            when(course.getName()).thenReturn("Java");
            when(courseRepository.existsByNameIgnoreCaseAndDeletedFalse("Java")).thenReturn(false);
            when(timeProvider.now()).thenReturn(LocalDateTime.now());
            when(courseRepository.save(course)).thenReturn(course);

            CourseResponseDTO response = mock(CourseResponseDTO.class);

            when(courseMapper.toResponseDTO(course)).thenReturn(response);

            //act
            CourseResponseDTO result = courseService.create(5L, createRequestDTO);

            //assert
            verify(course).auditCreate(eq(5L), any());
            assertThat(result).isEqualTo(response);
        }
    }

    @Test
    void should_throw_when_category_not_found_on_create() {
        when(createRequestDTO.getCategoryId()).thenReturn(10L);
        when(categoryRepository.findByIdAndDeletedFalse(10L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> courseService.create(5L, createRequestDTO))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void should_throw_when_course_name_exists_on_create() {
        when(createRequestDTO.getName()).thenReturn("Java");
        when(createRequestDTO.getCategoryId()).thenReturn(10L);

        when(categoryRepository.findByIdAndDeletedFalse(10L))
                .thenReturn(Optional.of(mock(Category.class)));

        when(authService.getAuthenticatedUserEntity()).thenReturn(mock(User.class));

        try (MockedStatic<Course> mocked = mockStatic(Course.class)) {
            Course courseMock = mock(Course.class);
            when(courseMock.getName()).thenReturn("Java");

            mocked.when(() -> Course.create(any(), any(), any()))
                    .thenReturn(courseMock);

            when(courseRepository
                    .existsByNameIgnoreCaseAndDeletedFalse("Java"))
                    .thenReturn(true);

            assertThatThrownBy(() -> courseService.create(5L, createRequestDTO))
                    .isInstanceOf(ConflictException.class);
        }
    }

    @Test
    void should_update_course() {
        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));

        Long categoryId = 10L;

        CourseUpdateRequestDTO dto = mock(CourseUpdateRequestDTO.class);
        when(dto.getCategoryId()).thenReturn(categoryId);
        when(dto.getName()).thenReturn("New");

        when(categoryRepository.findByIdAndDeletedFalse(categoryId))
                .thenReturn(Optional.of(mock(Category.class)));

        when(courseRepository.existsByNameIgnoreCaseAndDeletedFalse("New")).thenReturn(false);
        when(timeProvider.now()).thenReturn(LocalDateTime.now());
        when(courseRepository.save(course)).thenReturn(course);

        CourseResponseDTO response = mock(CourseResponseDTO.class);

        when(courseMapper.toResponseDTO(course)).thenReturn(response);

        //act
        CourseResponseDTO result = courseService.update(1L, dto, 5L);

        //assert
        verify(course).updateInfo(any(), any(), any(), any());
        verify(course).auditUpdate(eq(5L), any());

        assertThat(result).isEqualTo(response);
    }

    @Test
    void should_throw_when_name_exists_on_update() {
        when(course.getName()).thenReturn("Old");
        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));

        CourseUpdateRequestDTO dto = mock(CourseUpdateRequestDTO.class);

        when(dto.getName()).thenReturn("New");
        when(courseRepository
                .existsByNameIgnoreCaseAndDeletedFalse("New"))
                .thenReturn(true);

        assertThatThrownBy(() -> courseService.update(1L, dto, 5L))
                .isInstanceOf(ConflictException.class);
    }

    @Test
    void should_return_course_progress() {
        User user = mock(User.class);

        when(user.getId()).thenReturn(5L);
        when(authService.getAuthenticatedUserEntity()).thenReturn(user);

        UserCourse enrollment = mock(UserCourse.class);

        when(course.getName()).thenReturn("Java");
        when(enrollment.getCourse()).thenReturn(course);

        when(userCourseRepository
                .findByUserIdAndCourseIdAndDeletedFalse(5L, 1L))
                .thenReturn(Optional.of(enrollment));

        when(lessonRepository
                .countByModule_Course_IdAndDeletedFalse(1L))
                .thenReturn(3L);

        when(userLessonProgressRepository
                .countByUserIdAndLesson_Module_Course_IdAndCompletedTrueAndDeletedFalse(
                        5L, 1L
                ))
                .thenReturn(1L);

        //act
        CourseProgressResponseDTO result = courseService.getCourseProgress(1L);

        //assert
        assertThat(result.getProgressPercent()).isEqualTo(33);
        assertThat(result.getName()).isEqualTo("Java");
    }

    @Test
    void should_throw_when_user_not_enrolled() {
        User user = mock(User.class);

        when(user.getId()).thenReturn(5L);

        when(authService.getAuthenticatedUserEntity()).thenReturn(user);

        when(userCourseRepository
                .findByUserIdAndCourseIdAndDeletedFalse(any(), any()))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> courseService.getCourseProgress(1L))
                .isInstanceOf(ForbiddenOperationException.class);
    }

    @Test
    void should_find_by_slug() {
        when(course.isActive()).thenReturn(true);
        when(courseRepository.findBySlugAndDeletedFalse("java")).thenReturn(Optional.of(course));

        CourseResponseDTO response = mock(CourseResponseDTO.class);

        when(courseMapper.toResponseDTO(course)).thenReturn(response);

        CourseResponseDTO result = courseService.findBySlug("java");

        assertThat(result).isEqualTo(response);
    }
}