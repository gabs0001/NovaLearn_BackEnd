package br.com.novalearn.platform.api.dtos.quiz;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class QuizUpdateRequestDTO {
    @Size(max = 120, message = "Quiz name must have at most 120 characters")
    private String name;

    @Size(max = 255, message = "Description must have at most 255 characters")
    private String description;

    @Size(max = 1000, message = "Instructions must have at most 1000 characters")
    private String instructions;

    @Positive(message = "Number of questions must be positive")
    private Integer qtdQuestions;

    @DecimalMin(value = "0.0", inclusive = true, message = "Minimum score must be zero or greater")
    @Digits(integer = 3, fraction = 2, message = "Invalid score format")
    private BigDecimal minScore;

    @Positive(message = "Max attempts must be positive")
    private Integer maxAttempts;

    private Boolean randomOrder;

    @Size(max = 500, message = "Observations must have at most 500 characters")
    private String observations;

    public QuizUpdateRequestDTO() {}

    public QuizUpdateRequestDTO(
            String name,
            String description,
            String instructions,
            Integer qtdQuestions,
            BigDecimal minScore,
            Integer maxAttempts,
            Boolean randomOrder,
            String observations
    ) {
        this.name = name;
        this.description = description;
        this.instructions = instructions;
        this.qtdQuestions = qtdQuestions;
        this.minScore = minScore;
        this.maxAttempts = maxAttempts;
        this.randomOrder = randomOrder;
        this.observations = observations;
    }

    public String getName() { return name; }
    public String getDescription() { return description; }
    public String getInstructions() { return instructions; }
    public Integer getQtdQuestions() { return qtdQuestions; }
    public BigDecimal getMinScore() { return minScore; }
    public Integer getMaxAttempts() { return maxAttempts; }
    public Boolean getRandomOrder() { return randomOrder; }
    public String getObservations() { return observations; }

    public void setName(String name) { this.name = name; }
    public void setDescription(String description) { this.description = description; }
    public void setInstructions(String instructions) { this.instructions = instructions; }
    public void setQtdQuestions(Integer qtdQuestions) { this.qtdQuestions = qtdQuestions; }
    public void setMinScore(BigDecimal minScore) { this.minScore = minScore; }
    public void setMaxAttempts(Integer maxAttempts) { this.maxAttempts = maxAttempts; }
    public void setRandomOrder(Boolean randomOrder) { this.randomOrder = randomOrder; }
    public void setObservations(String observations) { this.observations = observations; }
}