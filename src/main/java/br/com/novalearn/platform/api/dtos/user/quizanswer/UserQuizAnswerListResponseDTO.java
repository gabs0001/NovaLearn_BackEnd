package br.com.novalearn.platform.api.dtos.user.quizanswer;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserQuizAnswerListResponseDTO {
    private Long id;
    private Long userId;
    private Long quizQuestionId;
    private Long quizAnswerOptionId;
    private Boolean correct;
    private LocalDateTime answeredAt;
    private Boolean active;
    private Boolean deleted;

    public UserQuizAnswerListResponseDTO() {}

    public UserQuizAnswerListResponseDTO(
            Long id,
            Long userId,
            Long quizQuestionId,
            Long quizAnswerOptionId,
            Boolean correct,
            LocalDateTime answeredAt,
            Boolean active,
            Boolean deleted
    ) {
        this.id = id;
        this.userId = userId;
        this.quizQuestionId = quizQuestionId;
        this.quizAnswerOptionId = quizAnswerOptionId;
        this.correct = correct;
        this.answeredAt = answeredAt;
        this.active = active;
        this.deleted = deleted;
    }

    public Long getId() { return id; }
    public Long getUserId() { return userId; }
    public Long getQuizQuestionId() { return quizQuestionId; }
    public Long getQuizAnswerOptionId() { return quizAnswerOptionId; }
    public Boolean getCorrect() { return correct; }
    public LocalDateTime getAnsweredAt() { return answeredAt; }
    public Boolean getActive() { return active; }
    public Boolean getDeleted() { return deleted; }

    public void setId(Long id) { this.id = id; }
    public void setUserId(Long userId) { this.userId = userId; }
    public void setQuizQuestionId(Long quizQuestionId) { this.quizQuestionId = quizQuestionId; }
    public void setQuizAnswerOptionId(Long quizAnswerOptionId) { this.quizAnswerOptionId = quizAnswerOptionId; }
    public void setCorrect(Boolean correct) { this.correct = correct; }
    public void setAnsweredAt(LocalDateTime answeredAt) { this.answeredAt = answeredAt; }
    public void setActive(Boolean active) { this.active = active; }
    public void setDeleted(Boolean deleted) { this.deleted = deleted; }
}