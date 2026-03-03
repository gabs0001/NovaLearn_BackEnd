package br.com.novalearn.platform.api.dtos.user.quizattempt;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserQuizAttemptUpdateRequestDTO {
    @NotNull
    @DecimalMin("0.0")
    private BigDecimal score;

    @NotNull
    @DecimalMin("0.0")
    private BigDecimal maxScore;

    private String observations;

    public UserQuizAttemptUpdateRequestDTO() {}

    public UserQuizAttemptUpdateRequestDTO(
            BigDecimal score,
            BigDecimal maxScore,
            String observations
    ) {
        this.score = score;
        this.maxScore = maxScore;
        this.observations = observations;
    }

    public BigDecimal getScore() { return score; }
    public BigDecimal getMaxScore() { return maxScore; }
    public String getObservations() { return observations; }

    public void setScore(BigDecimal score) { this.score = score; }
    public void setMaxScore(BigDecimal maxScore) { this.maxScore = maxScore; }
    public void setObservations(String observations) { this.observations = observations; }
}