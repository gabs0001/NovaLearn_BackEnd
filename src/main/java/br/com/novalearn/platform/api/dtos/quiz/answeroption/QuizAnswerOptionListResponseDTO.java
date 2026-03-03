package br.com.novalearn.platform.api.dtos.quiz.answeroption;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class QuizAnswerOptionListResponseDTO {
    private Long id;
    private Long questionId;
    private Integer sequence;
    private String option;
    private Boolean correct;
    private Boolean active;
    private Boolean deleted;

    public QuizAnswerOptionListResponseDTO() {}

    public QuizAnswerOptionListResponseDTO(
            Long id,
            Long questionId,
            Integer sequence,
            String option,
            Boolean correct,
            Boolean active,
            Boolean deleted
    ) {
        this.id = id;
        this.questionId = questionId;
        this.sequence = sequence;
        this.option = option;
        this.correct = correct;
        this.active = active;
        this.deleted = deleted;
    }

    public Long getId() { return id; }
    public Long getQuestionId() { return questionId; }
    public Integer getSequence() { return sequence; }
    public String getOption() { return option; }
    public Boolean getCorrect() { return correct; }
    public Boolean getActive() { return active; }
    public Boolean getDeleted() { return deleted; }

    public void setId(Long id) { this.id = id; }
    public void setQuestionId(Long questionId) { this.questionId = questionId; }
    public void setSequence(Integer sequence) { this.sequence = sequence; }
    public void setOption(String option) { this.option = option; }
    public void setCorrect(Boolean correct) { this.correct = correct; }
    public void setActive(Boolean active) { this.active = active; }
    public void setDeleted(Boolean deleted) { this.deleted = deleted; }
}