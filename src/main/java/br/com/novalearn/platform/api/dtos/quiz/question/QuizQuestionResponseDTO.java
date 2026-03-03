package br.com.novalearn.platform.api.dtos.quiz.question;

import br.com.novalearn.platform.api.dtos.BaseDTO;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class QuizQuestionResponseDTO extends BaseDTO {
    private Long quizId;
    private Integer sequence;
    private String description;
    private Integer points;
    private Long createdBy;
    private Long updatedBy;

    public QuizQuestionResponseDTO() {}

    public QuizQuestionResponseDTO(
            Long id,
            Boolean active,
            Boolean deleted,
            LocalDateTime createdAt,
            LocalDateTime updatedAt,
            String observations,
            Long quizId,
            Integer sequence,
            String description,
            Integer points,
            Long createdBy,
            Long updatedBy
    ) {
        super(id, active, deleted, createdAt, updatedAt, observations);
        this.quizId = quizId;
        this.sequence = sequence;
        this.description = description;
        this.points = points;
        this.createdBy = createdBy;
        this.updatedBy = updatedBy;
    }

    public Long getQuizId() { return quizId; }
    public Integer getSequence() { return sequence; }
    public String getDescription() { return description; }
    public Integer getPoints() { return points; }
    public Long getCreatedBy() { return createdBy; }
    public Long getUpdatedBy() { return updatedBy; }

    public void setQuizId(Long quizId) { this.quizId = quizId; }
    public void setSequence(Integer sequence) { this.sequence = sequence; }
    public void setDescription(String description) { this.description = description; }
    public void setPoints(Integer points) { this.points = points; }
    public void setCreatedBy(Long createdBy) { this.createdBy = createdBy; }
    public void setUpdatedBy(Long updatedBy) { this.updatedBy = updatedBy; }
}