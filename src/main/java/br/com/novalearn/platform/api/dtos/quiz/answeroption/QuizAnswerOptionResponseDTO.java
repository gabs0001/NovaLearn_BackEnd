package br.com.novalearn.platform.api.dtos.quiz.answeroption;

import br.com.novalearn.platform.api.dtos.BaseDTO;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class QuizAnswerOptionResponseDTO extends BaseDTO {
    private Long questionId;
    private Integer sequence;
    private String option;
    private Boolean correct;
    private Long createdBy;
    private Long updatedBy;

    public QuizAnswerOptionResponseDTO() {}

    public QuizAnswerOptionResponseDTO(
            Long id,
            Boolean active,
            Boolean deleted,
            LocalDateTime createdAt,
            LocalDateTime updatedAt,
            String observations,
            Long questionId,
            Integer sequence,
            String option,
            Boolean correct,
            Long createdBy,
            Long updatedBy
    ) {
        super(id, active, deleted, createdAt, updatedAt, observations);
        this.questionId = questionId;
        this.sequence = sequence;
        this.option = option;
        this.correct = correct;
        this.createdBy = createdBy;
        this.updatedBy = updatedBy;
    }

    public Long getQuestionId() { return questionId; }
    public Integer getSequence() { return sequence; }
    public String getOption() { return option; }
    public Boolean getCorrect() { return correct; }
    public Long getCreatedBy() { return createdBy; }
    public Long getUpdatedBy() { return updatedBy; }

    public void setQuestionId(Long questionId) { this.questionId = questionId; }
    public void setSequence(Integer sequence) { this.sequence = sequence; }
    public void setOption(String option) { this.option = option; }
    public void setCorrect(Boolean correct) { this.correct = correct; }
    public void setCreatedBy(Long createdBy) { this.createdBy = createdBy; }
    public void setUpdatedBy(Long updatedBy) { this.updatedBy = updatedBy; }
}