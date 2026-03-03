package br.com.novalearn.platform.api.dtos.review;

import br.com.novalearn.platform.domain.enums.ReviewStatus;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ReviewListResponseDTO {
    private Long id;
    private Long courseId;
    private Long userId;
    private Integer rating;
    private Boolean anonymous;
    private ReviewStatus status;
    private Boolean active;
    private Boolean deleted;

    public ReviewListResponseDTO() {}

    public ReviewListResponseDTO(
            Long id,
            Long courseId,
            Long userId,
            Integer rating,
            Boolean anonymous,
            ReviewStatus status,
            Boolean active,
            Boolean deleted
    ) {
        this.id = id;
        this.courseId = courseId;
        this.userId = userId;
        this.rating = rating;
        this.anonymous = anonymous;
        this.status = status;
        this.active = active;
        this.deleted = deleted;
    }

    public Long getId() { return id; }
    public Long getCourseId() { return courseId; }
    public Long getUserId() { return userId; }
    public Integer getRating() { return rating; }
    public Boolean getAnonymous() { return anonymous; }
    public ReviewStatus getStatus() { return status; }
    public Boolean getActive() { return active; }
    public Boolean getDeleted() { return deleted; }

    public void setId(Long id) { this.id = id; }
    public void setCourseId(Long courseId) { this.courseId = courseId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public void setRating(Integer rating) { this.rating = rating; }
    public void setAnonymous(Boolean anonymous) { this.anonymous = anonymous; }
    public void setStatus(ReviewStatus status) { this.status = status; }
    public void setActive(Boolean active) { this.active = active; }
    public void setDeleted(Boolean deleted) { this.deleted = deleted; }
}