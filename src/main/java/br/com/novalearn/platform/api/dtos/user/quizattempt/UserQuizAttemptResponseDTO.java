package br.com.novalearn.platform.api.dtos.user.quizattempt;

import br.com.novalearn.platform.api.dtos.BaseDTO;
import br.com.novalearn.platform.domain.enums.QuizAttemptStatus;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserQuizAttemptResponseDTO extends BaseDTO {
    private Long quizId;
    private Integer seqAttempt;
    private BigDecimal score;
    private BigDecimal maxScore;
    private QuizAttemptStatus status;
    private boolean passed;
    private LocalDateTime startedAt;
    private LocalDateTime finishedAt;

    public UserQuizAttemptResponseDTO() {}

    public UserQuizAttemptResponseDTO(
            Long id,
            Boolean active,
            Boolean deleted,
            LocalDateTime createdAt,
            LocalDateTime updatedAt,
            String observations,
            Long quizId,
            Integer seqAttempt,
            BigDecimal score,
            BigDecimal maxScore,
            QuizAttemptStatus status,
            boolean passed,
            LocalDateTime startedAt,
            LocalDateTime finishedAt
    ) {
        super(id, active, deleted, createdAt, updatedAt, observations);
        this.quizId = quizId;
        this.seqAttempt = seqAttempt;
        this.score = score;
        this.maxScore = maxScore;
        this.status = status;
        this.passed = passed;
        this.startedAt = startedAt;
        this.finishedAt = finishedAt;
    }

    public Long getQuizId() { return quizId; }
    public Integer getSeqAttempt() { return seqAttempt; }
    public BigDecimal getScore() { return score; }
    public BigDecimal getMaxScore() { return maxScore; }
    public QuizAttemptStatus getStatus() { return status; }
    public boolean isPassed() { return passed; }
    public LocalDateTime getStartedAt() { return startedAt; }
    public LocalDateTime getFinishedAt() { return finishedAt; }

    public void setQuizId(Long quizId) { this.quizId = quizId; }
    public void setSeqAttempt(Integer seqAttempt) { this.seqAttempt = seqAttempt; }
    public void setScore(BigDecimal score) { this.score = score; }
    public void setMaxScore(BigDecimal maxScore) { this.maxScore = maxScore; }
    public void setStatus(QuizAttemptStatus status) { this.status = status; }
    public void setPassed(boolean passed) { this.passed = passed; }
    public void setStartedAt(LocalDateTime startedAt) { this.startedAt = startedAt; }
    public void setFinishedAt(LocalDateTime finishedAt) { this.finishedAt = finishedAt; }
}