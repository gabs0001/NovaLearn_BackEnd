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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Objects;

@Service
public class DashboardService {
    private final AuthService authService;
    private final UserCourseRepository userCourseRepository;
    private final DashboardMapper mapper;

    public DashboardService(
            AuthService authService,
            UserCourseRepository userCourseRepository,
            DashboardMapper mapper
    ) {
        this.authService = authService;
        this.userCourseRepository = userCourseRepository;
        this.mapper = mapper;
    }

    @Transactional(readOnly = true)
    public DashboardOverviewResponseDTO getDashboard() {
        User user = authService.getAuthenticatedUserEntity();
        List<UserCourse> enrollments = loadEnrollments(user);

        long total = enrollments.size();

        long enrolled = countByStatus(enrollments, EnrollmentStatus.ENROLLED);
        long inProgress = countByStatus(enrollments, EnrollmentStatus.IN_PROGRESS);
        long completed = countByStatus(enrollments, EnrollmentStatus.COMPLETED);
        long cancelled = countByStatus(enrollments, EnrollmentStatus.CANCELLED);

        long certificates = enrollments.stream()
                .filter(UserCourse::isCertificateIssued)
                .count();

        BigDecimal avgProgress = calculateAverageProgress(enrollments);

        return mapper.toOverview(
                total,
                enrolled,
                inProgress,
                completed,
                cancelled,
                avgProgress,
                certificates
        );
    }

    @Transactional(readOnly = true)
    public CoursesSummaryResponseDTO getCoursesSummary() {
        List<UserCourse> enrollments = loadEnrollments(authService.getAuthenticatedUserEntity());

        return mapper.toCoursesSummary(
                countByStatus(enrollments, EnrollmentStatus.ENROLLED),
                countByStatus(enrollments, EnrollmentStatus.IN_PROGRESS),
                countByStatus(enrollments, EnrollmentStatus.COMPLETED)
        );
    }

    @Transactional(readOnly = true)
    public ProgressSummaryResponseDTO getProgressSummary() {
        List<UserCourse> enrollments = loadEnrollments(authService.getAuthenticatedUserEntity());
        return mapper.toProgressSummary(calculateAverageProgress(enrollments));
    }

    @Transactional(readOnly = true)
    public CertificatesSummaryResponseDTO getCertificatesSummary() {
        long count = loadEnrollments(authService.getAuthenticatedUserEntity())
                .stream()
                .filter(UserCourse::isCertificateIssued)
                .count();

        return mapper.toCertificatesSummary(count);
    }

    private List<UserCourse> loadEnrollments(User user) {
        return userCourseRepository.findAllByUserIdAndDeletedFalse(user.getId());
    }

    private long countByStatus(List<UserCourse> list, EnrollmentStatus status) {
        return list.stream()
                .filter(e -> e.getEnrollmentStatus() == status)
                .count();
    }

    private BigDecimal calculateAverageProgress(List<UserCourse> list) {
        List<BigDecimal> values = list.stream()
                .map(UserCourse::getProgressPercent)
                .filter(Objects::nonNull)
                .map(BigDecimal::valueOf)
                .toList();

        if(values.isEmpty()) return BigDecimal.ZERO;

        return values.stream()
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .divide(BigDecimal.valueOf(values.size()), 2, RoundingMode.HALF_UP);
    }
}