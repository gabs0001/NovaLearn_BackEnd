package br.com.novalearn.platform.api.dtos.review;

import br.com.novalearn.platform.api.dtos.BaseDTO;
import br.com.novalearn.platform.domain.enums.ReviewStatus;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ReviewResponseDTO extends BaseDTO {
    private Long courseId;
    private Long userId;
    private Integer rating;
    private String comment;
    private LocalDateTime reviewAt;
    private LocalDateTime publishedAt;
    private Boolean anonymous;
    private ReviewStatus status;
    private Long createdBy;
    private Long updatedBy;

    public ReviewResponseDTO() {}

    public ReviewResponseDTO(
            Long id,
            Boolean active,
            Boolean deleted,
            LocalDateTime createdAt,
            LocalDateTime updatedAt,
            String observations,
            Long courseId,
            Long userId,
            Integer rating,
            String comment,
            LocalDateTime reviewAt,
            LocalDateTime publishedAt,
            Boolean anonymous,
            ReviewStatus status,
            Long createdBy,
            Long updatedBy
    ) {
        super(id, active, deleted, createdAt, updatedAt, observations);
        this.courseId = courseId;
        this.userId = userId;
        this.rating = rating;
        this.comment = comment;
        this.reviewAt = reviewAt;
        this.publishedAt = publishedAt;
        this.anonymous = anonymous;
        this.status = status;
        this.createdBy = createdBy;
        this.updatedBy = updatedBy;
    }

    public Long getCourseId() { return courseId; }
    public Long getUserId() { return userId; }
    public Integer getRating() { return rating; }
    public String getComment() { return comment; }
    public LocalDateTime getReviewAt() { return reviewAt; }
    public LocalDateTime getPublishedAt() { return publishedAt; }
    public Boolean getAnonymous() { return anonymous; }
    public ReviewStatus getStatus() { return status; }
    public Long getCreatedBy() { return createdBy; }
    public Long getUpdatedBy() { return updatedBy; }

    public void setCourseId(Long courseId) { this.courseId = courseId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public void setRating(Integer rating) { this.rating = rating; }
    public void setComment(String comment) { this.comment = comment; }
    public void setReviewAt(LocalDateTime reviewAt) { this.reviewAt = reviewAt; }
    public void setPublishedAt(LocalDateTime publishedAt) { this.publishedAt = publishedAt; }
    public void setAnonymous(Boolean anonymous) { this.anonymous = anonymous; }
    public void setStatus(ReviewStatus status) { this.status = status; }
    public void setCreatedBy(Long createdBy) { this.createdBy = createdBy; }
    public void setUpdatedBy(Long updatedBy) { this.updatedBy = updatedBy; }
}