package br.com.novalearn.platform.api.dtos.review;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ReviewUpdateRequestDTO {
    @Min(value = 1, message = "Rating must be at least 1.")
    @Max(value = 5, message = "Rating must be at most 5.")
    private Integer rating;

    @Size(max = 2000, message = "Comment must have at most 2000 characters.")
    private String comment;

    private Boolean anonymous;

    @Size(max = 500, message = "Observations must have at most 500 characters.")
    private String observations;

    public ReviewUpdateRequestDTO() {}

    public ReviewUpdateRequestDTO(
            Integer rating,
            String comment,
            Boolean anonymous,
            String observations
    ) {
        this.rating = rating;
        this.comment = comment;
        this.anonymous = anonymous;
        this.observations = observations;
    }

    public Integer getRating() { return rating; }
    public String getComment() { return comment; }
    public Boolean getAnonymous() { return anonymous; }
    public String getObservations() { return observations; }

    public void setRating(Integer rating) { this.rating = rating; }
    public void setComment(String comment) { this.comment = comment; }
    public void setAnonymous(Boolean anonymous) { this.anonymous = anonymous; }
    public void setObservations(String observations) { this.observations = observations; }
}