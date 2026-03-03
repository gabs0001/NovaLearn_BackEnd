package br.com.novalearn.platform.api.dtos.user.quizanswer;

import br.com.novalearn.platform.api.dtos.BaseDTO;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserQuizAnswerResponseDTO extends BaseDTO {
    private Long userId;
    private Long quizQuestionId;
    private Long quizAnswerOptionId;
    private Boolean correct;
    private LocalDateTime answeredAt;
    private Long createdBy;
    private Long updatedBy;

    public UserQuizAnswerResponseDTO() {}

    public UserQuizAnswerResponseDTO(
            Long id,
            Boolean active,
            Boolean deleted,
            LocalDateTime createdAt,
            LocalDateTime updatedAt,
            String observations,
            Long userId,
            Long quizQuestionId,
            Long quizAnswerOptionId,
            Boolean correct,
            LocalDateTime answeredAt,
            Long createdBy,
            Long updatedBy
    ) {
        super(id, active, deleted, createdAt, updatedAt, observations);
        this.userId = userId;
        this.quizQuestionId = quizQuestionId;
        this.quizAnswerOptionId = quizAnswerOptionId;
        this.correct = correct;
        this.answeredAt = answeredAt;
        this.createdBy = createdBy;
        this.updatedBy = updatedBy;
    }

    public Long getUserId() { return userId; }
    public Long getQuizQuestionId() { return quizQuestionId; }
    public Long getQuizAnswerOptionId() { return quizAnswerOptionId; }
    public Boolean getCorrect() { return correct; }
    public LocalDateTime getAnsweredAt() { return answeredAt; }
    public Long getCreatedBy() { return createdBy; }
    public Long getUpdatedBy() { return updatedBy; }

    public void setUserId(Long userId) { this.userId = userId; }
    public void setQuizQuestionId(Long quizQuestionId) { this.quizQuestionId = quizQuestionId; }
    public void setQuizAnswerOptionId(Long quizAnswerOptionId) { this.quizAnswerOptionId = quizAnswerOptionId; }
    public void setCorrect(Boolean correct) { this.correct = correct; }
    public void setAnsweredAt(LocalDateTime answeredAt) { this.answeredAt = answeredAt; }
    public void setCreatedBy(Long createdBy) { this.createdBy = createdBy; }
    public void setUpdatedBy(Long updatedBy) { this.updatedBy = updatedBy; }
}