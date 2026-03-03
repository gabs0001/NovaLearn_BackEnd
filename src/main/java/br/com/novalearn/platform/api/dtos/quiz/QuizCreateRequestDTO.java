package br.com.novalearn.platform.api.dtos.quiz;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class QuizCreateRequestDTO {
    @NotNull(message = "Module is required")
    private Long moduleId;

    @NotBlank(message = "Quiz name is required")
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

    @NotNull(message = "Random order flag is required")
    private Boolean randomOrder;

    public QuizCreateRequestDTO() {}

    public QuizCreateRequestDTO(
            Long moduleId,
            String name,
            String description,
            String instructions,
            Integer qtdQuestions,
            BigDecimal minScore,
            Integer maxAttempts,
            Boolean randomOrder
    ) {
        this.moduleId = moduleId;
        this.name = name;
        this.description = description;
        this.instructions = instructions;
        this.qtdQuestions = qtdQuestions;
        this.minScore = minScore;
        this.maxAttempts = maxAttempts;
        this.randomOrder = randomOrder;
    }

    public Long getModuleId() { return moduleId; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public String getInstructions() { return instructions; }
    public Integer getQtdQuestions() { return qtdQuestions; }
    public BigDecimal getMinScore() { return minScore; }
    public Integer getMaxAttempts() { return maxAttempts; }
    public Boolean getRandomOrder() { return randomOrder; }
}