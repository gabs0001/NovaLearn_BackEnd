package br.com.novalearn.platform.domain.services.review;

import br.com.novalearn.platform.api.dtos.review.ReviewCreateRequestDTO;
import br.com.novalearn.platform.api.dtos.review.ReviewListResponseDTO;
import br.com.novalearn.platform.api.dtos.review.ReviewResponseDTO;
import br.com.novalearn.platform.api.dtos.review.ReviewUpdateRequestDTO;
import br.com.novalearn.platform.api.mappers.review.ReviewMapper;
import br.com.novalearn.platform.core.exception.business.ConflictException;
import br.com.novalearn.platform.core.exception.business.ForbiddenOperationException;
import br.com.novalearn.platform.core.exception.business.ResourceNotFoundException;
import br.com.novalearn.platform.core.exception.business.ValidationException;
import br.com.novalearn.platform.domain.entities.course.Course;
import br.com.novalearn.platform.domain.entities.review.Review;
import br.com.novalearn.platform.domain.entities.user.User;
import br.com.novalearn.platform.domain.enums.ReviewStatus;
import br.com.novalearn.platform.domain.repositories.course.CourseRepository;
import br.com.novalearn.platform.domain.repositories.review.ReviewRepository;
import br.com.novalearn.platform.domain.repositories.user.UserCourseRepository;
import br.com.novalearn.platform.domain.services.BaseCrudService;
import br.com.novalearn.platform.domain.services.auth.AuthService;
import br.com.novalearn.platform.infra.time.provider.TimeProvider;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReviewService extends BaseCrudService<Review> {
    private final AuthService authService;
    private final ReviewRepository reviewRepository;
    private final CourseRepository courseRepository;
    private final UserCourseRepository userCourseRepository;
    private final ReviewMapper reviewMapper;
    private final TimeProvider timeProvider;

    public ReviewService(
            AuthService authService,
            ReviewRepository reviewRepository,
            CourseRepository courseRepository,
            UserCourseRepository userCourseRepository,
            ReviewMapper reviewMapper,
            TimeProvider timeProvider
    ) {
        super(reviewRepository, "Review", timeProvider);
        this.authService = authService;
        this.reviewRepository = reviewRepository;
        this.courseRepository = courseRepository;
        this.userCourseRepository = userCourseRepository;
        this.reviewMapper = reviewMapper;
        this.timeProvider = timeProvider;
    }

    @Transactional
    public ReviewResponseDTO create(Long userId, ReviewCreateRequestDTO dto) {
        User user = authService.getAuthenticatedUserEntity();

        Course course = findCourseOrThrow(dto.getCourseId());

        validateCreate(dto, course.getId(), userId);

        validateUserCanReviewCourse(userId, course.getId());

        Review entity = Review.create(
                user,
                course,
                dto.getRating(),
                dto.getComment(),
                Boolean.TRUE.equals(dto.getAnonymous()),
                dto.getObservations(),
                timeProvider.now()
        );

        applyAuditCreate(entity, userId);

        Review saved = reviewRepository.save(entity);

        return reviewMapper.toResponseDTO(saved);
    }

    private void validateCreate(ReviewCreateRequestDTO dto, Long courseId, Long userId) {
        if(dto.getRating() == null || dto.getRating() < 1 || dto.getRating() > 5) {
            throw new ValidationException("Rating must be between 1 and 5.");
        }

        if(reviewRepository.existsByCourseIdAndUserIdAndDeletedFalse(courseId, userId)) {
            throw new ConflictException("User has already reviewed this course.");
        }

        if(dto.getComment() != null && dto.getComment().length() < 3) {
            throw new ValidationException("Comment must contain at least 3 characters.");
        }
    }

    @Transactional
    public ReviewResponseDTO update(Long id, ReviewUpdateRequestDTO dto, Long userId) {
        Review entity = findEntityOrThrow(id);

        entity.ensureNotDeleted();

        entity.edit(dto.getRating(), dto.getComment(), dto.getAnonymous());

        //revisar aqui
        /*if(dto.getApproved() != null) {
            if(dto.getApproved()) {
                entity.approveAndPublish(timeProvider.now());
            } else {
                entity.reject(dto.getObservations());
            }
        }*/

        applyAuditUpdate(entity, userId);

        Review updated = reviewRepository.save(entity);

        return reviewMapper.toResponseDTO(updated);
    }

    @Transactional
    public ReviewResponseDTO findById(Long id) {
        Review entity = findEntityOrThrow(id);

        entity.ensureNotDeleted();

        return reviewMapper.toResponseDTO(entity);
    }

    @Transactional
    public List<ReviewListResponseDTO> listAllActive() {
        return reviewRepository.findAllByDeletedFalse()
                .stream()
                .map(reviewMapper::toListResponseDTO)
                .toList();
    }

    @Transactional
    public List<ReviewResponseDTO> listByCourse(Long courseId) {
        courseRepository.findById(courseId).orElseThrow(
                () -> new ResourceNotFoundException("Course not found.")
        );

        return reviewRepository.findAllByCourseIdAndStatusAndAndDeletedFalse(courseId, ReviewStatus.APPROVED)
                .stream()
                .map(reviewMapper::toResponseDTO)
                .toList();
    }

    @Transactional
    public void approve(Long id, Long adminId) {
        Review entity = findEntityOrThrow(id);

        entity.ensureNotDeleted();

        entity.approveAndPublish(timeProvider.now());
        applyAuditUpdate(entity, adminId);

        reviewRepository.save(entity);
    }

    @Transactional
    public void reject(Long id, String observation, Long adminId) {
        Review entity = findEntityOrThrow(id);

        entity.ensureNotDeleted();

        entity.reject(observation);
        applyAuditUpdate(entity, adminId);

        reviewRepository.save(entity);
    }

    @Transactional
    public List<ReviewListResponseDTO> listPendingApproval() {
        return reviewRepository.findAllByStatusAndDeletedFalse(ReviewStatus.PENDING)
                .stream()
                .map(reviewMapper::toListResponseDTO)
                .toList();
    }

    private void validateUserCanReviewCourse(Long userId, Long courseId) {
        boolean enrolled = userCourseRepository
                .existsByUserIdAndCourseIdAndDeletedFalse(userId, courseId);

        if(!enrolled)
            throw new ForbiddenOperationException("User must be enrolled to this course to review it.");
    }

    private Course findCourseOrThrow(Long id) {
        return courseRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Course not found.")
        );
    }
}