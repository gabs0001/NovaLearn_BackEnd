package br.com.novalearn.platform.api.dtos.quiz;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class QuizListResponseDTO {
    private Long id;
    private Long moduleId;
    private String name;
    private Integer qtdQuestions;
    private boolean active;

    public QuizListResponseDTO() {}

    public QuizListResponseDTO(
            Long id,
            Long moduleId,
            String name,
            Integer qtdQuestions,
            boolean active
    ) {
        this.id = id;
        this.moduleId = moduleId;
        this.name = name;
        this.qtdQuestions = qtdQuestions;
        this.active = active;
    }

    public Long getId() { return id; }
    public Long getModuleId() { return moduleId; }
    public String getName() { return name; }
    public Integer getQtdQuestions() { return qtdQuestions; }
    public boolean isActive() { return active; }

    public void setId(Long id) { this.id = id; }
    public void setModuleId(Long moduleId) { this.moduleId = moduleId; }
    public void setName(String name) { this.name = name; }
    public void setQtdQuestions(Integer qtdQuestions) { this.qtdQuestions = qtdQuestions; }
    public void setActive(boolean active) { this.active = active; }
}