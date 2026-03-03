package br.com.novalearn.platform.api.dtos.quiz.answeroption;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class QuizAnswerOptionCreateRequestDTO {
    @NotNull(message = "questionId is required")
    private Long questionId;

    @NotNull(message = "sequence is required")
    @Positive(message = "sequence must be greater than zero")
    private Integer sequence;

    @NotBlank(message = "option is required")
    private String option;

    @NotNull(message = "correct is required")
    private Boolean correct;

    @Size(max = 500, message = "observations must be a maximum of 500 characters.")
    private String observations;

    public QuizAnswerOptionCreateRequestDTO() {}

    public QuizAnswerOptionCreateRequestDTO(
            Long questionId,
            Integer sequence,
            String option,
            Boolean correct,
            String observations
    ) {
        this.questionId = questionId;
        this.sequence = sequence;
        this.option = option;
        this.correct = correct;
        this.observations = observations;
    }

    public Long getQuestionId() { return questionId; }
    public Integer getSequence() { return sequence; }
    public String getOption() { return option; }
    public Boolean getCorrect() { return correct; }
    public String getObservations() { return observations; }

    public void setQuestionId(Long questionId) { this.questionId = questionId; }
    public void setSequence(Integer sequence) { this.sequence = sequence; }
    public void setOption(String option) { this.option = option; }
    public void setCorrect(Boolean correct) { this.correct = correct; }
    public void setObservations(String observations) { this.observations = observations; }
}