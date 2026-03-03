package br.com.novalearn.platform.api.dtos.quiz.question;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class QuizQuestionUpdateRequestDTO {
    @Min(value = 1, message = "Sequence must be at least 1.")
    private Integer sequence;

    @Size(max = 500, message = "Question text must not exceed 500 characters.")
    private String description;

    @Min(value = 1, message = "Points must be at least 1.")
    private Integer points;

    @Size(max = 500, message = "Observations must not exceed 500 characters.")
    private String observations;

    public QuizQuestionUpdateRequestDTO() {}

    public QuizQuestionUpdateRequestDTO(Integer sequence, String description, Integer points, String observations) {
        this.sequence = sequence;
        this.description = description;
        this.points = points;
        this.observations = observations;
    }

    public Integer getSequence() { return sequence; }
    public String getDescription() { return description; }
    public Integer getPoints() { return points; }
    public String getObservations() { return observations; }

    public void setSequence(Integer sequence) { this.sequence = sequence; }
    public void setDescription(String description) { this.description = description; }
    public void setPoints(Integer points) { this.points = points; }
    public void setObservations(String observations) { this.observations = observations; }
}