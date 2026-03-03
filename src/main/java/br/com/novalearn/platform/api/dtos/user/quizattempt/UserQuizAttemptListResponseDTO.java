package br.com.novalearn.platform.api.dtos.user.quizattempt;

import br.com.novalearn.platform.domain.enums.QuizAttemptStatus;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserQuizAttemptListResponseDTO {
    private Long id;
    private Long quizId;
    private Integer seqAttempt;
    private BigDecimal score;
    private Boolean passed;
    private QuizAttemptStatus status;
    private LocalDateTime finishedAt;

    public UserQuizAttemptListResponseDTO() {}

    public UserQuizAttemptListResponseDTO(
            Long id,
            Long quizId,
            Integer seqAttempt,
            BigDecimal score,
            Boolean passed,
            QuizAttemptStatus status,
            LocalDateTime finishedAt
    ) {
        this.id = id;
        this.quizId = quizId;
        this.seqAttempt = seqAttempt;
        this.score = score;
        this.passed = passed;
        this.status = status;
        this.finishedAt = finishedAt;
    }

    public Long getId() { return id; }
    public Long getQuizId() { return quizId; }
    public Integer getSeqAttempt() { return seqAttempt; }
    public BigDecimal getScore() { return score; }
    public Boolean getPassed() { return passed; }
    public QuizAttemptStatus getStatus() { return status; }
    public LocalDateTime getFinishedAt() { return finishedAt; }

    public void setId(Long id) { this.id = id; }
    public void setQuizId(Long quizId) { this.quizId = quizId; }
    public void setSeqAttempt(Integer seqAttempt) { this.seqAttempt = seqAttempt; }
    public void setScore(BigDecimal score) { this.score = score; }
    public void setPassed(Boolean passed) { this.passed = passed; }
    public void setStatus(QuizAttemptStatus status) { this.status = status; }
    public void setFinishedAt(LocalDateTime finishedAt) { this.finishedAt = finishedAt; }
}