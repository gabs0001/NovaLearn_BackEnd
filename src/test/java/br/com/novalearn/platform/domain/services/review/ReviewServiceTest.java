package br.com.novalearn.platform.domain.services.review;

import br.com.novalearn.platform.api.dtos.review.ReviewCreateRequestDTO;
import br.com.novalearn.platform.api.dtos.review.ReviewListResponseDTO;
import br.com.novalearn.platform.api.dtos.review.ReviewResponseDTO;
import br.com.novalearn.platform.api.dtos.review.ReviewUpdateRequestDTO;
import br.com.novalearn.platform.api.mappers.review.ReviewMapper;
import br.com.novalearn.platform.core.exception.business.ConflictException;
import br.com.novalearn.platform.core.exception.business.ForbiddenOperationException;
import br.com.novalearn.platform.core.exception.business.ValidationException;
import br.com.novalearn.platform.domain.entities.course.Course;
import br.com.novalearn.platform.domain.entities.review.Review;
import br.com.novalearn.platform.domain.entities.user.User;
import br.com.novalearn.platform.domain.enums.ReviewStatus;
import br.com.novalearn.platform.domain.repositories.course.CourseRepository;
import br.com.novalearn.platform.domain.repositories.review.ReviewRepository;
import br.com.novalearn.platform.domain.repositories.user.UserCourseRepository;
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

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ReviewServiceTest {
    @Mock
    private AuthService authService;

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private CourseRepository courseRepository;

    @Mock
    private UserCourseRepository userCourseRepository;

    @Mock
    private ReviewMapper reviewMapper;

    @Mock
    private TimeProvider timeProvider;

    @InjectMocks
    private ReviewService service;

    @InjectMocks
    private MyReviewService myReviewService;

    private ReviewCreateRequestDTO dto;
    private Course course;
    private Review review;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.clearContext();
        dto = new ReviewCreateRequestDTO();
        course = mock(Course.class);
        lenient().when(course.getId()).thenReturn(1L);
        review = mock(Review.class);
        lenient().when(review.getId()).thenReturn(8L);
    }

    @Test
    void create_should_create_review() {
        Long userId = 5L;

        dto.setCourseId(1L);
        dto.setRating(5);
        dto.setComment("Very good");

        User user = mock(User.class);

        when(authService.getAuthenticatedUserEntity()).thenReturn(user);

        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));

        when(reviewRepository
                .existsByCourseIdAndUserIdAndDeletedFalse(1L, userId))
                .thenReturn(false);

        when(userCourseRepository
                .existsByUserIdAndCourseIdAndDeletedFalse(userId, 1L))
                .thenReturn(true);

        when(timeProvider.now()).thenReturn(LocalDateTime.now());

        try (MockedStatic<Review> mocked = mockStatic(Review.class)) {

            mocked.when(() ->
                    Review.create(
                            eq(user),
                            eq(course),
                            eq(5),
                            eq("Very good"),
                            eq(false),
                            isNull(),
                            any()
                    )
            ).thenReturn(review);

            when(reviewRepository.save(review)).thenReturn(review);

            when(reviewMapper.toResponseDTO(review)).thenReturn(new ReviewResponseDTO());

            ReviewResponseDTO result = service.create(userId, dto);

            assertNotNull(result);
        }
    }

    @Test
    void create_should_throw_when_rating_invalid() {
        dto.setCourseId(1L);
        dto.setRating(10);

        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));

        assertThrows(
                ValidationException.class,
                () -> service.create(5L, dto)
        );
    }

    @Test
    void create_should_throw_when_already_reviewed() {
        dto.setCourseId(1L);
        dto.setRating(5);

        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));

        when(reviewRepository
                .existsByCourseIdAndUserIdAndDeletedFalse(1L, 5L))
                .thenReturn(true);

        assertThrows(
                ConflictException.class,
                () -> service.create(5L, dto)
        );
    }

    @Test
    void create_should_throw_when_not_enrolled() {
        dto.setCourseId(1L);
        dto.setRating(5);

        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));

        when(reviewRepository
                .existsByCourseIdAndUserIdAndDeletedFalse(1L, 5L))
                .thenReturn(false);

        when(userCourseRepository
                .existsByUserIdAndCourseIdAndDeletedFalse(5L, 1L))
                .thenReturn(false);

        assertThrows(
                ForbiddenOperationException.class,
                () -> service.create(5L, dto)
        );
    }

    @Test
    void update_should_edit_review() {
        ReviewUpdateRequestDTO dto = new ReviewUpdateRequestDTO();
        dto.setRating(4);
        dto.setComment("Bom");

        when(reviewRepository.findById(8L)).thenReturn(Optional.of(review));

//        when(review.isDeleted()).thenReturn(false);

        when(reviewRepository.save(review)).thenReturn(review);

        when(reviewMapper.toResponseDTO(review)).thenReturn(new ReviewResponseDTO());

        ReviewResponseDTO result = service.update(8L, dto, 5L);

        verify(review).edit(4, "Bom", null);

        assertNotNull(result);
    }

    @Test
    void approve_should_publish() {
        when(reviewRepository.findById(8L)).thenReturn(Optional.of(review));

        when(timeProvider.now()).thenReturn(LocalDateTime.now());

        service.approve(8L, 5L);

        verify(review).approveAndPublish(any());
        verify(reviewRepository).save(review);
    }

    @Test
    void reject_should_reject() {
        when(reviewRepository.findById(8L)).thenReturn(Optional.of(review));

        service.reject(8L, "Ruim", 5L);

        verify(review).reject("Ruim");
        verify(reviewRepository).save(review);
    }

    @Test
    void list_by_course_should_return_only_approved() {
        when(courseRepository.findById(1L)).thenReturn(Optional.of(mock(Course.class)));

        when(reviewRepository
                .findAllByCourseIdAndStatusAndAndDeletedFalse(1L, ReviewStatus.APPROVED))
                .thenReturn(List.of(mock(Review.class)));

        when(reviewMapper.toResponseDTO(any())).thenReturn(new ReviewResponseDTO());

        List<ReviewResponseDTO> result = service.listByCourse(1L);

        assertEquals(1, result.size());
    }

    @Test
    void list_pending_should_return_list() {
        when(reviewRepository
                .findAllByStatusAndDeletedFalse(ReviewStatus.PENDING))
                .thenReturn(List.of(mock(Review.class)));

        when(reviewMapper.toListResponseDTO(any())).thenReturn(new ReviewListResponseDTO());

        List<ReviewListResponseDTO> result = service.listPendingApproval();

        assertEquals(1, result.size());
    }

    //my review
    @Test
    void list_my_reviews_should_return_list() {
        when(reviewRepository.findAllByUserIdAndDeletedFalse(5L))
                .thenReturn(List.of(review));

        when(reviewMapper.toListResponseDTO(any()))
                .thenReturn(new ReviewListResponseDTO());

        List<ReviewListResponseDTO> result = myReviewService.listMyReviews(5L);

        assertEquals(1, result.size());
    }

    @Test
    void update_my_review_should_edit() {
        ReviewUpdateRequestDTO dto = new ReviewUpdateRequestDTO();
        dto.setRating(3);
        dto.setComment("Ok");

        when(reviewRepository.findByIdAndUserIdAndDeletedFalse(8L, 5L))
                .thenReturn(Optional.of(review));

        when(timeProvider.now()).thenReturn(LocalDateTime.now());

        when(reviewRepository.save(review)).thenReturn(review);

        when(reviewMapper.toResponseDTO(review)).thenReturn(new ReviewResponseDTO());

        ReviewResponseDTO result = myReviewService.updateMyReview(8L, dto, 5L);

        verify(review).edit(3, "Ok", null);

        assertNotNull(result);
    }

    @Test
    void delete_my_review_should_soft_delete() {
        when(reviewRepository.findByIdAndUserIdAndDeletedFalse(8L, 5L))
                .thenReturn(Optional.of(review));

        myReviewService.deleteMyReview(8L, 5L);

        verify(review).delete();
        verify(reviewRepository).save(review);
    }

    @Test
    void restore_my_review_should_restore() {
        when(reviewRepository.findByIdAndUserId(8L, 5L))
                .thenReturn(Optional.of(review));

        myReviewService.restoreMyReview(8L, 5L);

        verify(review).restore();
        verify(reviewRepository).save(review);
    }
}