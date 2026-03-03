package br.com.novalearn.platform.domain.services.course;

import java.util.List;
import java.util.Optional;

import br.com.novalearn.platform.api.dtos.course.my.MyCourseResponseDTO;
import br.com.novalearn.platform.api.mappers.course.MyCourseMapper;
import br.com.novalearn.platform.core.exception.auth.UnauthorizedException;
import br.com.novalearn.platform.core.exception.business.ForbiddenOperationException;
import br.com.novalearn.platform.domain.entities.user.User;
import br.com.novalearn.platform.domain.entities.user.UserCourse;
import br.com.novalearn.platform.domain.repositories.user.UserCourseRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.*;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class MyCourseServiceTest {
    @Mock
    private UserCourseRepository userCourseRepository;

    @Mock
    private MyCourseMapper myCourseMapper;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private MyCourseService myCourseService;

    private User user;

    private final Long USER_ID = 5L;

    @BeforeEach
    void setup() {
        user = mock(User.class);

        lenient().when(user.getId()).thenReturn(USER_ID);

        SecurityContextHolder.setContext(securityContext);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        lenient().when(authentication.getPrincipal()).thenReturn(user);
    }

    @AfterEach
    void cleanup() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void should_list_my_courses() {
        List<UserCourse> enrollments = List.of(mock(UserCourse.class));

        when(userCourseRepository.findAllByUserIdAndDeletedFalse(USER_ID)).thenReturn(enrollments);

        List<MyCourseResponseDTO> response = List.of(mock(MyCourseResponseDTO.class));

        when(myCourseMapper.toListResponseDTO(enrollments)).thenReturn(response);

        //act
        List<MyCourseResponseDTO> result = myCourseService.listMyCourses();

        //assert
        assertThat(result).isEqualTo(response);

        verify(userCourseRepository).findAllByUserIdAndDeletedFalse(USER_ID);
    }

    @Test
    void should_throw_when_not_authenticated() {
        when(securityContext.getAuthentication()).thenReturn(null);

        assertThatThrownBy(() -> myCourseService.listMyCourses())
                .isInstanceOf(UnauthorizedException.class);
    }

    @Test
    void should_get_my_course_by_id() {
        UserCourse enrollment = mock(UserCourse.class);

        when(userCourseRepository
                .findByUserIdAndCourseIdAndDeletedFalse(USER_ID, 1L))
                .thenReturn(Optional.of(enrollment));

        MyCourseResponseDTO dto = mock(MyCourseResponseDTO.class);

        when(myCourseMapper.toResponseDTO(enrollment)).thenReturn(dto);

        //act
        MyCourseResponseDTO result = myCourseService.getMyCourseById(1L);

        //assert
        assertThat(result).isEqualTo(dto);
    }

    @Test
    void should_throw_when_not_enrolled() {
        when(userCourseRepository
                .findByUserIdAndCourseIdAndDeletedFalse(USER_ID, 1L))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> myCourseService.getMyCourseById(1L))
                .isInstanceOf(ForbiddenOperationException.class);
    }

    @Test
    void should_throw_when_principal_is_invalid() {
        when(authentication.getPrincipal()).thenReturn("anonymous");

        assertThatThrownBy(() -> myCourseService.listMyCourses())
                .isInstanceOf(UnauthorizedException.class);
    }

    @Test
    void should_list_completed_courses() {
        List<UserCourse> enrollments = List.of(mock(UserCourse.class));

        when(userCourseRepository
                .findAllByUserIdAndCompletedAtIsNotNullAndDeletedFalse(USER_ID))
                .thenReturn(enrollments);

        List<MyCourseResponseDTO> response = List.of(mock(MyCourseResponseDTO.class));

        when(myCourseMapper.toListResponseDTO(enrollments)).thenReturn(response);

        List<MyCourseResponseDTO> result = myCourseService.listCompletedCourses();

        assertThat(result).isEqualTo(response);
    }

    @Test
    void should_list_courses_in_progress() {
        List<UserCourse> enrollments = List.of(mock(UserCourse.class));

        when(userCourseRepository
                .findAllByUserIdAndCompletedAtIsNullAndDeletedFalse(USER_ID))
                .thenReturn(enrollments);

        List<MyCourseResponseDTO> response = List.of(mock(MyCourseResponseDTO.class));

        when(myCourseMapper.toListResponseDTO(enrollments)).thenReturn(response);

        List<MyCourseResponseDTO> result = myCourseService.listCoursesInProgress();

        assertThat(result).isEqualTo(response);
    }
}