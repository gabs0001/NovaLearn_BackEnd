package br.com.novalearn.platform.domain.services.enrollment;

import br.com.novalearn.platform.api.dtos.enrollment.EnrollmentRequestDTO;
import br.com.novalearn.platform.api.dtos.enrollment.EnrollmentResponseDTO;
import br.com.novalearn.platform.api.dtos.enrollment.UpdateProgressRequestDTO;
import br.com.novalearn.platform.api.mappers.enrollment.EnrollmentMapper;
import br.com.novalearn.platform.core.exception.business.ConflictException;
import br.com.novalearn.platform.core.exception.business.ResourceNotFoundException;
import br.com.novalearn.platform.domain.entities.course.Course;
import br.com.novalearn.platform.domain.entities.user.User;
import br.com.novalearn.platform.domain.entities.user.UserCourse;
import br.com.novalearn.platform.domain.enums.QuizAttemptStatus;
import br.com.novalearn.platform.domain.policies.CertificateIssuancePolicy;
import br.com.novalearn.platform.domain.repositories.course.CourseRepository;
import br.com.novalearn.platform.domain.repositories.user.UserCourseRepository;
import br.com.novalearn.platform.domain.repositories.user.UserQuizAttemptRepository;
import br.com.novalearn.platform.domain.services.auth.AuthService;
import br.com.novalearn.platform.infra.time.provider.TimeProvider;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class EnrollmentService {
    private final AuthService authService;
    private final CourseRepository courseRepository;
    private final UserCourseRepository userCourseRepository;
    private final UserQuizAttemptRepository userQuizAttemptRepository;
    private final EnrollmentMapper enrollmentMapper;
    private final TimeProvider timeProvider;
    private final CertificateIssuancePolicy policy;
    private final ApplicationEventPublisher eventPublisher;

    public EnrollmentService(
            AuthService authService,
            CourseRepository courseRepository,
            UserCourseRepository userCourseRepository,
            UserQuizAttemptRepository userQuizAttemptRepository,
            EnrollmentMapper enrollmentMapper,
            TimeProvider timeProvider,
            CertificateIssuancePolicy policy,
            ApplicationEventPublisher eventPublisher
    ) {
        this.authService = authService;
        this.courseRepository = courseRepository;
        this.userCourseRepository = userCourseRepository;
        this.userQuizAttemptRepository = userQuizAttemptRepository;
        this.enrollmentMapper = enrollmentMapper;
        this.timeProvider = timeProvider;
        this.policy = policy;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public EnrollmentResponseDTO enroll(EnrollmentRequestDTO request) {
        User user = authService.getAuthenticatedUserEntity();

        Course course = courseRepository.findById(request.getCourseId())
                .orElseThrow(() -> new ResourceNotFoundException("Course not found."));

        if(userCourseRepository.existsByUserIdAndCourseIdAndDeletedFalse(user.getId(), course.getId())) {
            throw new ConflictException("User already enrolled in this course.");
        }

        UserCourse enrollment = UserCourse.enroll(user, course, timeProvider.now());
        enrollment.auditCreate(user.getId(), timeProvider.now());

        UserCourse saved = userCourseRepository.save(enrollment);

        return enrollmentMapper.toResponseDTO(saved);
    }

    @Transactional
    public EnrollmentResponseDTO cancelEnrollment(Long courseId) {
        User user = authService.getAuthenticatedUserEntity();

        UserCourse enrollment = findEnrollment(user.getId(), courseId);

        enrollment.cancel("Enrollment canceled by user.");
        enrollment.auditUpdate(user.getId(), timeProvider.now());

        UserCourse saved = userCourseRepository.save(enrollment);

        saved.getDomainEvents().forEach(eventPublisher::publishEvent);
        saved.clearEvents();

        return enrollmentMapper.toResponseDTO(saved);
    }

    @Transactional
    public EnrollmentResponseDTO updateProgress(Long courseId, UpdateProgressRequestDTO request) {
        User user = authService.getAuthenticatedUserEntity();

        UserCourse enrollment = findEnrollment(user.getId(), courseId);

        enrollment.updateProgress(request.getProgressPercent(), timeProvider.now());
        enrollment.auditUpdate(user.getId(), timeProvider.now());

        UserCourse saved = userCourseRepository.save(enrollment);

        saved.getDomainEvents().forEach(eventPublisher::publishEvent);
        saved.clearEvents();

        return enrollmentMapper.toResponseDTO(saved);
    }

    @Transactional
    public EnrollmentResponseDTO issueCertificate(Long courseId) {
        User user = authService.getAuthenticatedUserEntity();
        UserCourse enrollment = findEnrollment(user.getId(), courseId);

        boolean hasPendingAttempts =
                userQuizAttemptRepository.existsByUserIdAndCourseIdAndStatusIn(
                        user.getId(),
                        enrollment.getCourse().getId(),
                        List.of(QuizAttemptStatus.STARTED, QuizAttemptStatus.CANCELLED)
                );
        policy.validate(enrollment, hasPendingAttempts);

        enrollment.issueCertificate();
        enrollment.auditUpdate(user.getId(), timeProvider.now());

        UserCourse saved = userCourseRepository.save(enrollment);

        saved.getDomainEvents().forEach(eventPublisher::publishEvent);
        saved.clearEvents();

        return enrollmentMapper.toResponseDTO(saved);
    }

    @Transactional
    public List<EnrollmentResponseDTO> listMyEnrollments() {
        User user = authService.getAuthenticatedUserEntity();

        return userCourseRepository
                .findAllByUserIdAndDeletedFalseOrderByEnrolledAtDesc(user.getId())
                .stream()
                .map(enrollmentMapper::toResponseDTO)
                .toList();
    }

    @Transactional
    public EnrollmentResponseDTO getEnrollmentDetails(Long courseId) {
        User user = authService.getAuthenticatedUserEntity();

        UserCourse userCourse =
                userCourseRepository.findByUserIdAndCourseIdAndDeletedFalse(user.getId(), courseId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException("Enrollment not found for this course.")
                        );

        return enrollmentMapper.toResponseDTO(userCourse);
    }

    private UserCourse findEnrollment(Long userId, Long courseId) {
        return userCourseRepository.findByUserIdAndCourseIdAndDeletedFalse(userId, courseId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Enrollment not found for this course.")
                );
    }
}