package br.com.novalearn.platform.domain.services.dashboard;

import br.com.novalearn.platform.api.dtos.dashboard.DashboardOverviewResponseDTO;
import br.com.novalearn.platform.api.dtos.dashboard.summary.CertificatesSummaryResponseDTO;
import br.com.novalearn.platform.api.dtos.dashboard.summary.CoursesSummaryResponseDTO;
import br.com.novalearn.platform.api.dtos.dashboard.summary.ProgressSummaryResponseDTO;
import br.com.novalearn.platform.api.mappers.dashboard.DashboardMapper;
import br.com.novalearn.platform.domain.entities.user.User;
import br.com.novalearn.platform.domain.entities.user.UserCourse;
import br.com.novalearn.platform.domain.enums.EnrollmentStatus;
import br.com.novalearn.platform.domain.repositories.user.UserCourseRepository;
import br.com.novalearn.platform.domain.services.auth.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DashboardServiceTest {
    @Mock
    private AuthService authService;

    @Mock
    private UserCourseRepository userCourseRepository;

    @Mock
    private DashboardMapper mapper;

    @InjectMocks
    private DashboardService service;

    private User user;

    @BeforeEach
    void setUp() {
        user = mock(User.class);
    }

    private UserCourse mockEnrollment(
            EnrollmentStatus status,
            Integer progress,
            boolean certificate
    ) {
        UserCourse uc = mock(UserCourse.class);

        lenient().when(uc.getEnrollmentStatus()).thenReturn(status);
        lenient().when(uc.getProgressPercent()).thenReturn(progress);
        lenient().when(uc.isCertificateIssued()).thenReturn(certificate);

        return uc;
    }

    @Test
    void getDashboard_should_return_overview() {
        when(user.getId()).thenReturn(5L);

        when(authService.getAuthenticatedUserEntity()).thenReturn(user);

        List<UserCourse> enrollments = List.of(
                mockEnrollment(EnrollmentStatus.ENROLLED, 20, false),
                mockEnrollment(EnrollmentStatus.IN_PROGRESS, 50, false),
                mockEnrollment(EnrollmentStatus.COMPLETED, 100, true),
                mockEnrollment(EnrollmentStatus.CANCELLED, null, false)
        );

        when(userCourseRepository.findAllByUserIdAndDeletedFalse(5L)).thenReturn(enrollments);

        DashboardOverviewResponseDTO expected = new DashboardOverviewResponseDTO(
                4L,
                1L,
                1L,
                1L,
                1L,
                new BigDecimal("56.67"),
                1L
        );

        when(mapper.toOverview(
                4,
                1,
                1,
                1,
                1,
                new BigDecimal("56.67"),
                1
        )).thenReturn(expected);

        DashboardOverviewResponseDTO result = service.getDashboard();

        assertEquals(expected, result);
    }

    @Test
    void getCoursesSummary_should_return_counts() {
        when(user.getId()).thenReturn(5L);

        when(authService.getAuthenticatedUserEntity()).thenReturn(user);

        List<UserCourse> enrollments = List.of(
                mockEnrollment(EnrollmentStatus.ENROLLED, 10, false),
                mockEnrollment(EnrollmentStatus.IN_PROGRESS, 40, false),
                mockEnrollment(EnrollmentStatus.IN_PROGRESS, 60, false),
                mockEnrollment(EnrollmentStatus.COMPLETED, 100, true)
        );

        when(userCourseRepository.findAllByUserIdAndDeletedFalse(5L)).thenReturn(enrollments);

        CoursesSummaryResponseDTO expected = new CoursesSummaryResponseDTO(1, 2, 1);

        when(mapper.toCoursesSummary(1, 2, 1)).thenReturn(expected);

        CoursesSummaryResponseDTO result = service.getCoursesSummary();

        assertEquals(expected, result);
    }

    @Test
    void getProgressSummary_should_return_average() {
        when(user.getId()).thenReturn(5L);

        when(authService.getAuthenticatedUserEntity()).thenReturn(user);

        List<UserCourse> enrollments = List.of(
                mockEnrollment(EnrollmentStatus.ENROLLED, 25, false),
                mockEnrollment(EnrollmentStatus.IN_PROGRESS, 75, false)
        );

        when(userCourseRepository.findAllByUserIdAndDeletedFalse(5L)).thenReturn(enrollments);

        ProgressSummaryResponseDTO expected = new ProgressSummaryResponseDTO(new BigDecimal("50.00"));

        when(mapper.toProgressSummary(new BigDecimal("50.00"))).thenReturn(expected);

        ProgressSummaryResponseDTO result = service.getProgressSummary();

        assertEquals(expected, result);
    }

    @Test
    void getCertificatesSummary_should_count() {
        when(user.getId()).thenReturn(5L);

        when(authService.getAuthenticatedUserEntity()).thenReturn(user);

        List<UserCourse> enrollments = List.of(
                mockEnrollment(EnrollmentStatus.COMPLETED, 100, true),
                mockEnrollment(EnrollmentStatus.COMPLETED, 100, true),
                mockEnrollment(EnrollmentStatus.IN_PROGRESS, 40, false)
        );

        when(userCourseRepository.findAllByUserIdAndDeletedFalse(5L)).thenReturn(enrollments);

        CertificatesSummaryResponseDTO expected = new CertificatesSummaryResponseDTO(2);

        when(mapper.toCertificatesSummary(2)).thenReturn(expected);

        CertificatesSummaryResponseDTO result = service.getCertificatesSummary();

        assertEquals(expected, result);
    }

    @Test
    void getProgressSummary_when_no_progress_should_return_zero() {
        when(user.getId()).thenReturn(5L);

        when(authService.getAuthenticatedUserEntity()).thenReturn(user);

        List<UserCourse> enrollments = List.of(
                mockEnrollment(EnrollmentStatus.ENROLLED, null, false),
                mockEnrollment(EnrollmentStatus.IN_PROGRESS, null, false)
        );

        when(userCourseRepository.findAllByUserIdAndDeletedFalse(5L)).thenReturn(enrollments);

        ProgressSummaryResponseDTO expected = new ProgressSummaryResponseDTO(BigDecimal.ZERO);

        when(mapper.toProgressSummary(BigDecimal.ZERO)).thenReturn(expected);

        ProgressSummaryResponseDTO result = service.getProgressSummary();

        assertEquals(expected, result);
    }

    @Test
    void getDashboard_should_call_dependencies() {
        when(user.getId()).thenReturn(5L);

        when(authService.getAuthenticatedUserEntity()).thenReturn(user);

        when(userCourseRepository.findAllByUserIdAndDeletedFalse(5L)).thenReturn(List.of());

        when(mapper.toOverview(0,0,0,0,0,BigDecimal.ZERO,0))
                .thenReturn(new DashboardOverviewResponseDTO());

        service.getDashboard();

        verify(authService).getAuthenticatedUserEntity();
        verify(userCourseRepository).findAllByUserIdAndDeletedFalse(5L);
        verify(mapper).toOverview(
                0,0,0,0,0,BigDecimal.ZERO,0
        );
    }
}