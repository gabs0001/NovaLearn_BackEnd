package br.com.novalearn.platform.api.dtos.quiz.question;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class QuizQuestionCreateRequestDTO {
    @NotNull(message = "Quiz ID is required.")
    private Long quizId;

    @NotNull(message = "Sequence is required.")
    @Min(value = 1, message = "Sequence must be at least 1.")
    private Integer sequence;

    @NotBlank(message = "Question text is required.")
    @Size(max = 500, message = "Question text must not exceed 500 characters.")
    private String description;

    @NotNull(message = "Points value is required.")
    @Min(value = 1, message = "Points must be at least 1.")
    private Integer points;

    @Size(max = 500, message = "Observations must not exceed 500 characters.")
    private String observations;

    public QuizQuestionCreateRequestDTO() {}

    public QuizQuestionCreateRequestDTO(
            Long quizId,
            Integer sequence,
            String description,
            Integer points,
            String observations
    ) {
        this.quizId = quizId;
        this.sequence = sequence;
        this.description = description;
        this.points = points;
        this.observations = observations;
    }

    public Long getQuizId() { return quizId; }
    public Integer getSequence() { return sequence; }
    public String getDescription() { return description; }
    public Integer getPoints() { return points; }
    public String getObservations() { return observations; }

    public void setQuizId(Long quizId) { this.quizId = quizId; }
    public void setSequence(Integer sequence) { this.sequence = sequence; }
    public void setDescription(String description) { this.description = description; }
    public void setPoints(Integer points) { this.points = points; }
    public void setObservations(String observations) { this.observations = observations; }
}