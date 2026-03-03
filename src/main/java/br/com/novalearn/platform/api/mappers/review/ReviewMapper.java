package br.com.novalearn.platform.api.mappers.review;

import br.com.novalearn.platform.api.dtos.review.ReviewListResponseDTO;
import br.com.novalearn.platform.api.dtos.review.ReviewResponseDTO;
import br.com.novalearn.platform.domain.entities.review.Review;
import org.springframework.stereotype.Component;

@Component
public class ReviewMapper {
    public ReviewResponseDTO toResponseDTO(Review entity) {
        return new ReviewResponseDTO(
                entity.getId(),
                entity.isActive(),
                entity.isDeleted(),
                entity.getCreatedAt(),
                entity.getUpdatedAt(),
                entity.getObservations(),
                entity.getCourse() != null ? entity.getCourse().getId() : null,
                entity.getUser() != null ? entity.getUser().getId() : null,
                entity.getRating(),
                entity.getComment(),
                entity.getReviewAt(),
                entity.getPublishedAt(),
                entity.isAnonymous(),
                entity.getStatus(),
                entity.getCreatedBy(),
                entity.getUpdatedBy()
        );
    }

    public ReviewListResponseDTO toListResponseDTO(Review entity) {
        return new ReviewListResponseDTO(
                entity.getId(),
                entity.getCourse() != null ? entity.getCourse().getId() : null,
                entity.getUser() != null ? entity.getUser().getId() : null,
                entity.getRating(),
                entity.isAnonymous(),
                entity.getStatus(),
                entity.isActive(),
                entity.isDeleted()
        );
    }
}