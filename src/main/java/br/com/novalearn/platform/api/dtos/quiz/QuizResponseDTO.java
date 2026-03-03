package br.com.novalearn.platform.api.dtos.quiz;

import br.com.novalearn.platform.api.dtos.BaseDTO;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class QuizResponseDTO extends BaseDTO {
    private Long moduleId;
    private String name;
    private String description;
    private String instructions;
    private Integer qtdQuestions;
    private BigDecimal minScore;
    private Integer maxAttempts;
    private boolean randomOrder;
    private Long createdBy;
    private Long updatedBy;

    public QuizResponseDTO() {}

    public QuizResponseDTO(
            Long id,
            Boolean active,
            Boolean deleted,
            LocalDateTime createdAt,
            LocalDateTime updatedAt,
            String observations,
            Long moduleId,
            String name,
            String description,
            String instructions,
            Integer qtdQuestions,
            BigDecimal minScore,
            Integer maxAttempts,
            boolean randomOrder,
            Long createdBy,
            Long updatedBy
    ) {
        super(id, active, deleted, createdAt, updatedAt, observations);
        this.moduleId = moduleId;
        this.name = name;
        this.description = description;
        this.instructions = instructions;
        this.qtdQuestions = qtdQuestions;
        this.minScore = minScore;
        this.maxAttempts = maxAttempts;
        this.randomOrder = randomOrder;
        this.createdBy = createdBy;
        this.updatedBy = updatedBy;
    }

    public Long getModuleId() { return moduleId; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public String getInstructions() { return instructions; }
    public Integer getQtdQuestions() { return qtdQuestions; }
    public BigDecimal getMinScore() { return minScore; }
    public Integer getMaxAttempts() { return maxAttempts; }
    public boolean isRandomOrder() { return randomOrder; }
    public Long getCreatedBy() { return createdBy; }
    public Long getUpdatedBy() { return updatedBy; }

    public void setModuleId(Long moduleId) { this.moduleId = moduleId; }
    public void setName(String name) { this.name = name; }
    public void setDescription(String description) { this.description = description; }
    public void setInstructions(String instructions) { this.instructions = instructions; }
    public void setQtdQuestions(Integer qtdQuestions) { this.qtdQuestions = qtdQuestions; }
    public void setMinScore(BigDecimal minScore) { this.minScore = minScore; }
    public void setMaxAttempts(Integer maxAttempts) { this.maxAttempts = maxAttempts; }
    public void setRandomOrder(boolean randomOrder) { this.randomOrder = randomOrder; }
    public void setCreatedBy(Long createdBy) { this.createdBy = createdBy; }
    public void setUpdatedBy(Long updatedBy) { this.updatedBy = updatedBy; }
}