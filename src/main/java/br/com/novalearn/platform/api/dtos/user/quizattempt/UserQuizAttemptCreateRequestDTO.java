package br.com.novalearn.platform.api.dtos.user.quizattempt;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotNull;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserQuizAttemptCreateRequestDTO {
    @NotNull
    private Long quizId;

    public UserQuizAttemptCreateRequestDTO() {}

    public UserQuizAttemptCreateRequestDTO(Long quizId) { this.quizId = quizId; }

    public Long getQuizId() { return quizId; }

    public void setQuizId(Long quizId) { this.quizId = quizId; }
}