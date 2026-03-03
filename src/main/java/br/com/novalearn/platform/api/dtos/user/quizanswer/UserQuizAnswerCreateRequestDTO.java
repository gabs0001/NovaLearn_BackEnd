package br.com.novalearn.platform.api.dtos.user.quizanswer;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotNull;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserQuizAnswerCreateRequestDTO {
    @NotNull(message = "userId is required")
    private Long userId;

    @NotNull(message = "quizQuestionId is required")
    private Long quizQuestionId;

    @NotNull(message = "quizAnswerOptionId is required")
    private Long quizAnswerOptionId;

    public UserQuizAnswerCreateRequestDTO() {}

    public UserQuizAnswerCreateRequestDTO(
            Long userId,
            Long quizQuestionId,
            Long quizAnswerOptionId
    ) {
        this.userId = userId;
        this.quizQuestionId = quizQuestionId;
        this.quizAnswerOptionId = quizAnswerOptionId;
    }

    public Long getUserId() { return userId; }
    public Long getQuizQuestionId() {
        return quizQuestionId;
    }
    public Long getQuizAnswerOptionId() {
        return quizAnswerOptionId;
    }

    public void setUserId(Long userId) { this.userId = userId; }
    public void setQuizQuestionId(Long quizQuestionId) { this.quizQuestionId = quizQuestionId; }
    public void setQuizAnswerOptionId(Long quizAnswerOptionId) { this.quizAnswerOptionId = quizAnswerOptionId; }
}