package br.com.novalearn.platform.domain.repositories.review;

import br.com.novalearn.platform.domain.entities.review.Review;
import br.com.novalearn.platform.domain.enums.ReviewStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findAllByDeletedFalse();
    List<Review> findAllByCourseIdAndStatusAndAndDeletedFalse(Long courseId, ReviewStatus status);
    List<Review> findAllByUserIdAndDeletedFalse(Long userId);
    List<Review> findAllByStatusAndDeletedFalse(ReviewStatus status);
    Optional<Review> findByIdAndUserIdAndDeletedFalse(Long id, Long userId);
    Optional<Review> findByIdAndUserId(Long id, Long userId);
    Optional<Review> findByCourseIdAndUserIdAndDeletedFalse(Long courseId, Long userId);
    boolean existsByCourseIdAndUserIdAndDeletedFalse(Long courseId, Long userId);
    boolean existsByCourseIdAndUserIdAndIdNotAndDeletedFalse(Long courseId, Long userId, Long id);
    long countByUserIdAndDeletedFalse(Long userId);
}