package br.com.novalearn.platform.domain.services.review;

import br.com.novalearn.platform.api.dtos.review.ReviewListResponseDTO;
import br.com.novalearn.platform.api.dtos.review.ReviewResponseDTO;
import br.com.novalearn.platform.api.dtos.review.ReviewUpdateRequestDTO;
import br.com.novalearn.platform.api.mappers.review.ReviewMapper;
import br.com.novalearn.platform.core.exception.business.ResourceNotFoundException;
import br.com.novalearn.platform.domain.entities.review.Review;
import br.com.novalearn.platform.domain.repositories.review.ReviewRepository;
import br.com.novalearn.platform.infra.time.provider.TimeProvider;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MyReviewService {
    private final ReviewRepository reviewRepository;
    private final ReviewMapper reviewMapper;
    private final TimeProvider timeProvider;

    public MyReviewService(
            ReviewRepository reviewRepository,
            ReviewMapper reviewMapper,
            TimeProvider timeProvider
    ) {
        this.reviewRepository = reviewRepository;
        this.reviewMapper = reviewMapper;
        this.timeProvider = timeProvider;
    }

    @Transactional
    public List<ReviewListResponseDTO> listMyReviews(Long userId) {
        return reviewRepository.findAllByUserIdAndDeletedFalse(userId)
                .stream()
                .map(reviewMapper::toListResponseDTO)
                .toList();
    }

    @Transactional
    public ReviewResponseDTO findMyReviewById(Long id, Long userId) {
        Review review = findMyReviewOrThrow(id, userId);
        return reviewMapper.toResponseDTO(review);
    }

    @Transactional
    public ReviewResponseDTO updateMyReview(
            Long id,
            ReviewUpdateRequestDTO dto,
            Long userId
    ) {
        Review review = findMyReviewOrThrow(id, userId);

        review.edit(
                dto.getRating(),
                dto.getComment(),
                dto.getAnonymous()
        );

        review.auditUpdate(userId, timeProvider.now());

        Review saved = reviewRepository.save(review);

        return reviewMapper.toResponseDTO(saved);
    }

    @Transactional
    public void deleteMyReview(Long id, Long userId) {
        Review review = findMyReviewOrThrow(id, userId);

        review.delete();
        review.auditUpdate(userId, timeProvider.now());

        reviewRepository.save(review);
    }

    @Transactional
    public void restoreMyReview(Long id, Long userId) {
        Review review = findMyReviewOrThrowIncludingDeleted(id, userId);

        review.restore();
        review.auditUpdate(userId, timeProvider.now());

        reviewRepository.save(review);
    }

    @Transactional
    public ReviewResponseDTO findMyReviewByCourse(Long courseId, Long userId) {
        Review review = reviewRepository
                .findByCourseIdAndUserIdAndDeletedFalse(courseId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found."));

        return reviewMapper.toResponseDTO(review);
    }

    @Transactional
    public void markAnonymous(Long id, Long userId) {
        Review review = findMyReviewOrThrow(id, userId);

        review.edit(null, null, true);
        review.auditUpdate(userId, timeProvider.now());

        reviewRepository.save(review);
    }

    private Review findMyReviewOrThrow(Long id, Long userId) {
        return reviewRepository
                .findByIdAndUserIdAndDeletedFalse(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found."));
    }

    private Review findMyReviewOrThrowIncludingDeleted(Long id, Long userId) {
        return reviewRepository
                .findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found."));
    }
}