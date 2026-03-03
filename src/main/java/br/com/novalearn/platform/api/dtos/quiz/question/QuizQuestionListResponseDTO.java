package br.com.novalearn.platform.api.dtos.quiz.question;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class QuizQuestionListResponseDTO {
    private Long id;
    private Long quizId;
    private Integer sequence;
    private String description;
    private Integer points;
    private Boolean active;
    private Boolean deleted;

    public QuizQuestionListResponseDTO() {}

    public QuizQuestionListResponseDTO(
            Long id,
            Long quizId,
            Integer sequence,
            String description,
            Integer points,
            Boolean active,
            Boolean deleted
    ) {
        this.id = id;
        this.quizId = quizId;
        this.sequence = sequence;
        this.description = description;
        this.points = points;
        this.active = active;
        this.deleted = deleted;
    }

    public Long getId() { return id; }
    public Long getQuizId() { return quizId; }
    public Integer getSequence() { return sequence; }
    public String getDescription() { return description; }
    public Integer getPoints() { return points; }
    public Boolean getActive() { return active; }
    public Boolean getDeleted() { return deleted; }

    public void setId(Long id) { this.id = id; }
    public void setQuizId(Long quizId) { this.quizId = quizId; }
    public void setSequence(Integer sequence) { this.sequence = sequence; }
    public void setDescription(String description) { this.description = description; }
    public void setPoints(Integer points) { this.points = points; }
    public void setActive(Boolean active) { this.active = active; }
    public void setDeleted(Boolean deleted) { this.deleted = deleted; }
}