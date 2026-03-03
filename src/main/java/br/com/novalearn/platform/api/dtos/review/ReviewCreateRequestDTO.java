package br.com.novalearn.platform.api.dtos.review;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ReviewCreateRequestDTO {
    @NotNull
    private Long courseId;

    @NotNull(message = "Rating is required.")
    @Min(value = 1, message = "Rating must be at least 1.")
    @Max(value = 5, message = "Rating must be at most 5.")
    private Integer rating;

    @Size(max = 1000, message = "Comment must not exceed 1000 characters.")
    private String comment;

    @NotNull(message = "Anonymous flag is required.")
    private Boolean anonymous;

    @Size(max = 500, message = "Observations must not exceed 500 characters.")
    private String observations;

    public ReviewCreateRequestDTO() {}

    public ReviewCreateRequestDTO(
            Long courseId,
            Integer rating,
            String comment,
            Boolean anonymous,
            String observations
    ) {
        this.courseId = courseId;
        this.rating = rating;
        this.comment = comment;
        this.anonymous = anonymous;
        this.observations = observations;
    }

    public Long getCourseId() { return courseId; }
    public Integer getRating() { return rating; }
    public String getComment() { return comment; }
    public Boolean getAnonymous() { return anonymous; }
    public String getObservations() { return observations; }

    public void setCourseId(Long courseId) { this.courseId = courseId; }
    public void setRating(Integer rating) { this.rating = rating; }
    public void setComment(String comment) { this.comment = comment; }
    public void setAnonymous(Boolean anonymous) { this.anonymous = anonymous; }
    public void setObservations(String observations) { this.observations = observations; }
}