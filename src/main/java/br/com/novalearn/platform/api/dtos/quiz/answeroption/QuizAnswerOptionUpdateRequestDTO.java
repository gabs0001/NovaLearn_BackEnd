package br.com.novalearn.platform.api.dtos.quiz.answeroption;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class QuizAnswerOptionUpdateRequestDTO {
    @Positive(message = "sequence must be greater than zero")
    private Integer sequence;

    @Size(max = 500, message = "option must be at most 500 characters")
    private String option;

    private Boolean correct;
    private Boolean active;

    @Size(max = 500, message = "observations must be a maximum of 500 characters.")
    private String observations;

    public QuizAnswerOptionUpdateRequestDTO() {}

    public QuizAnswerOptionUpdateRequestDTO(
            Integer sequence,
            String option,
            Boolean correct,
            Boolean active,
            String observations
    ) {
        this.sequence = sequence;
        this.option = option;
        this.correct = correct;
        this.active = active;
        this.observations = observations;
    }

    public Integer getSequence() { return sequence; }
    public String getOption() { return option; }
    public Boolean getCorrect() { return correct; }
    public Boolean getActive() { return active; }
    public String getObservations() { return observations; }

    public void setSequence(Integer sequence) { this.sequence = sequence; }
    public void setOption(String option) { this.option = option; }
    public void setCorrect(Boolean correct) { this.correct = correct; }
    public void setActive(Boolean active) { this.active = active; }
    public void setObservations(String observations) { this.observations = observations; }
}