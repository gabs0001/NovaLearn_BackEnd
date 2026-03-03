package br.com.novalearn.platform.api.dtos.user.quizanswer;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserQuizAnswerUpdateRequestDTO {
    @Positive(message = "quizAnswerOptionId must be a positive number")
    private Long quizAnswerOptionId;

    @Size(max = 500, message = "observations must have a maximum of 500 characters")
    private String observations;

    public UserQuizAnswerUpdateRequestDTO() {}

    public Long getQuizAnswerOptionId() {
        return quizAnswerOptionId;
    }
    public String getObservations() {
        return observations;
    }

    public void setQuizAnswerOptionId(Long quizAnswerOptionId) {
        this.quizAnswerOptionId = quizAnswerOptionId;
    }
    public void setObservations(String observations) {
        this.observations = observations;
    }
}