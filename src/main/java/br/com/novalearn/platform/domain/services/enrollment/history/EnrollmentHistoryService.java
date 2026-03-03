package br.com.novalearn.platform.domain.services.enrollment.history;

import br.com.novalearn.platform.api.dtos.enrollment.history.EnrollmentHistoryResponseDTO;
import br.com.novalearn.platform.api.mappers.enrollment.history.EnrollmentHistoryMapper;
import br.com.novalearn.platform.domain.entities.user.User;
import br.com.novalearn.platform.domain.entities.user.UserCourse;
import br.com.novalearn.platform.domain.enums.EnrollmentStatus;
import br.com.novalearn.platform.domain.repositories.user.UserCourseRepository;
import br.com.novalearn.platform.domain.services.auth.AuthService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class EnrollmentHistoryService {
    private final AuthService authService;
    private final UserCourseRepository userCourseRepository;
    private final EnrollmentHistoryMapper enrollmentHistoryMapper;

    public EnrollmentHistoryService(AuthService authService, UserCourseRepository userCourseRepository, EnrollmentHistoryMapper enrollmentHistoryMapper) {
        this.authService = authService;
        this.userCourseRepository = userCourseRepository;
        this.enrollmentHistoryMapper = enrollmentHistoryMapper;
    }

    @Transactional
    public List<EnrollmentHistoryResponseDTO> getEnrollmentHistory() {
        User user = authService.getAuthenticatedUserEntity();

        List<UserCourse> enrollments = userCourseRepository.findAllByUserIdAndDeletedFalseOrderByEnrolledAtDesc(user.getId());

        return enrollments.stream()
                .map(enrollmentHistoryMapper::toResponseDTO)
                .toList();
    }

    @Transactional
    public List<EnrollmentHistoryResponseDTO> getCompletedHistory() {
        User user = authService.getAuthenticatedUserEntity();

        return userCourseRepository.findAllByUserIdAndEnrollmentStatusAndDeletedFalseOrderByCompletedAtDesc(user.getId(), EnrollmentStatus.COMPLETED)
                .stream()
                .map(enrollmentHistoryMapper::toResponseDTO)
                .toList();
    }

    @Transactional
    public List<EnrollmentHistoryResponseDTO> getCanceledHistory() {
        User user = authService.getAuthenticatedUserEntity();

        return userCourseRepository.findAllByUserIdAndEnrollmentStatusAndDeletedFalseOrderByEnrolledAtDesc(user.getId(), EnrollmentStatus.CANCELLED)
                .stream()
                .map(enrollmentHistoryMapper::toResponseDTO)
                .toList();
    }
}