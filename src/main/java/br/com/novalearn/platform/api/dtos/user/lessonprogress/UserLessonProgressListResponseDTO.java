package br.com.novalearn.platform.api.dtos.user.lessonprogress;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserLessonProgressListResponseDTO {
    private Long id;
    private Long userId;
    private Long lessonId;
    private Boolean completed;
    private Integer progressPercent;
    private Integer views;
    private Boolean active;
    private Boolean deleted;

    public UserLessonProgressListResponseDTO() {}

    public UserLessonProgressListResponseDTO(
            Long id,
            Long userId,
            Long lessonId,
            Boolean completed,
            Integer progressPercent,
            Integer views,
            Boolean active,
            Boolean deleted
    ) {
        this.id = id;
        this.userId = userId;
        this.lessonId = lessonId;
        this.completed = completed;
        this.progressPercent = progressPercent;
        this.views = views;
        this.active = active;
        this.deleted = deleted;
    }

    public Long getId() { return id; }
    public Long getUserId() { return userId; }
    public Long getLessonId() { return lessonId; }
    public Boolean getCompleted() { return completed; }
    public Integer getProgressPercent() { return progressPercent; }
    public Integer getViews() { return views; }
    public Boolean getActive() { return active; }
    public Boolean getDeleted() { return deleted; }

    public void setId(Long id) { this.id = id; }
    public void setUserId(Long userId) { this.userId = userId; }
    public void setLessonId(Long lessonId) { this.lessonId = lessonId; }
    public void setCompleted(Boolean completed) { this.completed = completed; }
    public void setProgressPercent(Integer progressPercent) { this.progressPercent = progressPercent; }
    public void setViews(Integer views) { this.views = views; }
    public void setActive(Boolean active) { this.active = active; }
    public void setDeleted(Boolean deleted) { this.deleted = deleted; }
}