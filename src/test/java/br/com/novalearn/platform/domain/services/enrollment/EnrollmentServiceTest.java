package br.com.novalearn.platform.domain.services.enrollment;

import br.com.novalearn.platform.api.dtos.enrollment.EnrollmentRequestDTO;
import br.com.novalearn.platform.api.dtos.enrollment.EnrollmentResponseDTO;
import br.com.novalearn.platform.api.dtos.enrollment.UpdateProgressRequestDTO;
import br.com.novalearn.platform.api.dtos.enrollment.history.EnrollmentHistoryResponseDTO;
import br.com.novalearn.platform.api.mappers.enrollment.EnrollmentMapper;
import br.com.novalearn.platform.api.mappers.enrollment.history.EnrollmentHistoryMapper;
import br.com.novalearn.platform.core.exception.business.ConflictException;
import br.com.novalearn.platform.core.exception.business.InvalidStateException;
import br.com.novalearn.platform.core.exception.business.ResourceNotFoundException;
import br.com.novalearn.platform.domain.entities.course.Course;
import br.com.novalearn.platform.domain.entities.user.User;
import br.com.novalearn.platform.domain.entities.user.UserCourse;
import br.com.novalearn.platform.domain.enums.EnrollmentStatus;
import br.com.novalearn.platform.domain.enums.QuizAttemptStatus;
import br.com.novalearn.platform.domain.policies.CertificateIssuancePolicy;
import br.com.novalearn.platform.domain.repositories.course.CourseRepository;
import br.com.novalearn.platform.domain.repositories.user.UserCourseRepository;
import br.com.novalearn.platform.domain.repositories.user.UserQuizAttemptRepository;
import br.com.novalearn.platform.domain.services.auth.AuthService;
import br.com.novalearn.platform.domain.services.enrollment.history.EnrollmentHistoryService;
import br.com.novalearn.platform.infra.time.provider.TimeProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EnrollmentServiceTest {
    @Mock
    private AuthService authService;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @Mock
    private CourseRepository courseRepository;

    @Mock
    private UserCourseRepository userCourseRepository;

    @Mock
    private UserQuizAttemptRepository userQuizAttemptRepository;

    @Mock
    private EnrollmentMapper enrollmentMapper;

    @Mock
    private EnrollmentHistoryMapper enrollmentHistoryMapper;

    @Mock
    private TimeProvider timeProvider;

    @Mock
    private CertificateIssuancePolicy policy;

    @InjectMocks
    private EnrollmentService enrollmentService;

    @InjectMocks
    private EnrollmentHistoryService enrollmentHistoryService;

    private User user;
    private Course course;
    private UserCourse enrollment;
    private EnrollmentRequestDTO request;
    private EnrollmentResponseDTO dto;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.clearContext();
        user = mock(User.class);
        course = mock(Course.class);
        enrollment = mock(UserCourse.class);
        request = new EnrollmentRequestDTO();
        request.setCourseId(1L);
        dto = mock(EnrollmentResponseDTO.class);

        when(authService.getAuthenticatedUserEntity()).thenReturn(user);
        lenient().when(user.getId()).thenReturn(5L);
        lenient().when(course.getId()).thenReturn(1L);
        lenient().when(timeProvider.now()).thenReturn(LocalDateTime.now());
    }

    @Test
    void should_enroll_user() {
        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));
        when(userCourseRepository.existsByUserIdAndCourseIdAndDeletedFalse(5L, 1L)).thenReturn(false);

        try (MockedStatic<UserCourse> mocked = mockStatic(UserCourse.class)) {
            mocked.when(() -> UserCourse.enroll(user, course, timeProvider.now())).thenReturn(enrollment);

            when(userCourseRepository.save(enrollment)).thenReturn(enrollment);
            when(enrollmentMapper.toResponseDTO(enrollment)).thenReturn(dto);

            EnrollmentResponseDTO result = enrollmentService.enroll(request);

            assertThat(result).isEqualTo(dto);

            verify(enrollment).auditCreate(eq(5L), any());
        }
    }

    @Test
    void should_throw_when_course_not_found() {
        request.setCourseId(2L);
        when(courseRepository.findById(2L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> enrollmentService.enroll(request))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void should_throw_when_already_enrolled() {
        when(courseRepository.findById(1L))
                .thenReturn(Optional.of(course));

        when(userCourseRepository
                .existsByUserIdAndCourseIdAndDeletedFalse(5L, 1L))
                .thenReturn(true);

        assertThatThrownBy(() -> enrollmentService.enroll(request))
                .isInstanceOf(ConflictException.class);
    }

    @Test
    void should_cancel_enrollment() {
        when(userCourseRepository
                .findByUserIdAndCourseIdAndDeletedFalse(5L, 1L))
                .thenReturn(Optional.of(enrollment));

        when(userCourseRepository.save(enrollment)).thenReturn(enrollment);

        when(enrollmentMapper.toResponseDTO(enrollment)).thenReturn(dto);

        EnrollmentResponseDTO result = enrollmentService.cancelEnrollment(1L);

        assertThat(result).isEqualTo(dto);

        verify(enrollment).cancel("Enrollment canceled by user.");

        verify(enrollment).auditUpdate(eq(5L), any());
    }

    @Test
    void should_update_progress() {
        UpdateProgressRequestDTO request = new UpdateProgressRequestDTO();
        request.setProgressPercent(80);

        when(userCourseRepository
                .findByUserIdAndCourseIdAndDeletedFalse(5L, 1L))
                .thenReturn(Optional.of(enrollment));

        when(userCourseRepository.save(enrollment)).thenReturn(enrollment);
        when(enrollmentMapper.toResponseDTO(enrollment)).thenReturn(dto);

        EnrollmentResponseDTO result = enrollmentService.updateProgress(1L, request);

        assertThat(result).isEqualTo(dto);

        verify(enrollment).updateProgress(eq(80), any());
        verify(enrollment).auditUpdate(eq(5L), any());
    }

    @Test
    void should_issue_certificate() {
        when(userCourseRepository
                .findByUserIdAndCourseIdAndDeletedFalse(5L, 1L))
                .thenReturn(Optional.of(enrollment));

        Course c = mock(Course.class);
        when(enrollment.getCourse()).thenReturn(c);
        when(c.getId()).thenReturn(1L);

        when(userQuizAttemptRepository
                .existsByUserIdAndCourseIdAndStatusIn(
                        5L,
                        1L,
                        List.of(QuizAttemptStatus.STARTED, QuizAttemptStatus.CANCELLED)
                ))
                .thenReturn(false);

        when(userCourseRepository.save(enrollment)).thenReturn(enrollment);
        when(enrollmentMapper.toResponseDTO(enrollment)).thenReturn(dto);

        EnrollmentResponseDTO result = enrollmentService.issueCertificate(1L);

        assertThat(result).isEqualTo(dto);

        verify(policy).validate(enrollment, false);
        verify(enrollment).issueCertificate();
    }

    @Test
    void should_throw_when_policy_blocks_certificate() {
        when(userCourseRepository
                .findByUserIdAndCourseIdAndDeletedFalse(5L, 1L))
                .thenReturn(Optional.of(enrollment));

        when(enrollment.getCourse()).thenReturn(course);

        when(userQuizAttemptRepository
                .existsByUserIdAndCourseIdAndStatusIn(
                        any(),
                        any(),
                        any()
                ))
                .thenReturn(true);

        doThrow(new InvalidStateException("Blocked"))
                .when(policy).validate(any(), anyBoolean());

        assertThatThrownBy(() -> enrollmentService.issueCertificate(1L))
                .isInstanceOf(InvalidStateException.class);
    }

    @Test
    void should_list_my_enrollments() {
        List<UserCourse> list = List.of(enrollment);

        when(userCourseRepository
                .findAllByUserIdAndDeletedFalseOrderByEnrolledAtDesc(5L))
                .thenReturn(list);

        when(enrollmentMapper.toResponseDTO(enrollment)).thenReturn(dto);

        List<EnrollmentResponseDTO> result = enrollmentService.listMyEnrollments();

        assertThat(result).hasSize(1);
        assertThat(result.getFirst()).isEqualTo(dto);
    }

    @Test
    void should_get_enrollment_details() {
        when(userCourseRepository
                .findByUserIdAndCourseIdAndDeletedFalse(5L, 1L))
                .thenReturn(Optional.of(enrollment));

        when(enrollmentMapper.toResponseDTO(enrollment)).thenReturn(dto);

        EnrollmentResponseDTO result = enrollmentService.getEnrollmentDetails(1L);

        assertThat(result).isEqualTo(dto);
    }

    //enrollment history
    @Test
    void should_get_enrollment_history() {
        List<UserCourse> list = List.of(enrollment);

        when(userCourseRepository
                .findAllByUserIdAndDeletedFalseOrderByEnrolledAtDesc(5L))
                .thenReturn(list);

        EnrollmentHistoryResponseDTO dto = mock(EnrollmentHistoryResponseDTO.class);

        when(enrollmentHistoryMapper.toResponseDTO(enrollment)).thenReturn(dto);

        List<EnrollmentHistoryResponseDTO> result = enrollmentHistoryService.getEnrollmentHistory();

        assertThat(result).hasSize(1);
    }

    @Test
    void should_get_completed_history() {
        List<UserCourse> list = List.of(enrollment);

        when(userCourseRepository
                .findAllByUserIdAndEnrollmentStatusAndDeletedFalseOrderByCompletedAtDesc(
                        5L, EnrollmentStatus.COMPLETED))
                .thenReturn(list);

        when(enrollmentHistoryMapper.toResponseDTO(enrollment))
                .thenReturn(mock(EnrollmentHistoryResponseDTO.class));

        List<EnrollmentHistoryResponseDTO> result = enrollmentHistoryService.getCompletedHistory();

        assertThat(result).hasSize(1);
    }

    @Test
    void should_get_canceled_history() {
        List<UserCourse> list = List.of(enrollment);

        when(userCourseRepository
                .findAllByUserIdAndEnrollmentStatusAndDeletedFalseOrderByEnrolledAtDesc(
                        5L, EnrollmentStatus.CANCELLED))
                .thenReturn(list);

        when(enrollmentHistoryMapper.toResponseDTO(enrollment))
                .thenReturn(mock(EnrollmentHistoryResponseDTO.class));

        List<EnrollmentHistoryResponseDTO> result = enrollmentHistoryService.getCanceledHistory();

        assertThat(result).hasSize(1);
    }
}