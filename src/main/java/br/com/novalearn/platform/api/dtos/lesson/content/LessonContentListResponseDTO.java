package br.com.novalearn.platform.api.dtos.lesson.content;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class LessonContentListResponseDTO {
    private Long id;
    private Long lessonId;
    private Boolean hasQuiz;
    private Boolean mainContent;
    private Boolean active;
    private Boolean deleted;

    public LessonContentListResponseDTO() {}

    public LessonContentListResponseDTO(Long id, Long lessonId, Boolean hasQuiz, Boolean mainContent, Boolean active, Boolean deleted) {
        this.id = id;
        this.lessonId = lessonId;
        this.hasQuiz = hasQuiz;
        this.mainContent = mainContent;
        this.active = active;
        this.deleted = deleted;
    }

    public Long getId() { return id; }
    public Long getLessonId() { return lessonId; }
    public Boolean getHasQuiz() { return hasQuiz; }
    public Boolean getMainContent() { return mainContent; }
    public Boolean getActive() { return active; }
    public Boolean getDeleted() { return deleted; }

    public void setId(Long id) { this.id = id; }
    public void setLessonId(Long lessonId) { this.lessonId = lessonId; }
    public void setHasQuiz(Boolean hasQuiz) { this.hasQuiz = hasQuiz; }
    public void setMainContent(Boolean mainContent) { this.mainContent = mainContent; }
    public void setActive(Boolean active) { this.active = active; }
    public void setDeleted(Boolean deleted) { this.deleted = deleted; }
}