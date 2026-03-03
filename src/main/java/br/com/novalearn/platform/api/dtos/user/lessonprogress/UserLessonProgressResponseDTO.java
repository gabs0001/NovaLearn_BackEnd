package br.com.novalearn.platform.api.dtos.user.lessonprogress;

import br.com.novalearn.platform.api.dtos.BaseDTO;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserLessonProgressResponseDTO extends BaseDTO {
    private Long userId;
    private Long lessonId;
    private Boolean completed;
    private LocalDateTime completedAt;
    private Integer progressPercent;
    private Integer views;
    private LocalDateTime firstViewAt;
    private LocalDateTime lastViewAt;
    private Long createdBy;
    private Long updatedBy;

    public UserLessonProgressResponseDTO() {}

    public UserLessonProgressResponseDTO(
            Long id,
            Boolean active,
            Boolean deleted,
            LocalDateTime createdAt,
            LocalDateTime updatedAt,
            String observations,
            Long userId,
            Long lessonId,
            Boolean completed,
            LocalDateTime completedAt,
            Integer progressPercent,
            Integer views,
            LocalDateTime firstViewAt,
            LocalDateTime lastViewAt,
            Long createdBy,
            Long updatedBy
    ) {
        super(id, active, deleted, createdAt, updatedAt, observations);
        this.userId = userId;
        this.lessonId = lessonId;
        this.completed = completed;
        this.completedAt = completedAt;
        this.progressPercent = progressPercent;
        this.views = views;
        this.firstViewAt = firstViewAt;
        this.lastViewAt = lastViewAt;
        this.createdBy = createdBy;
        this.updatedBy = updatedBy;
    }

    public Long getUserId() { return userId; }
    public Long getLessonId() { return lessonId; }
    public Boolean getCompleted() { return completed; }
    public LocalDateTime getCompletedAt() { return completedAt; }
    public Integer getProgressPercent() { return progressPercent; }
    public Integer getViews() { return views; }
    public LocalDateTime getFirstViewAt() { return firstViewAt; }
    public LocalDateTime getLastViewAt() { return lastViewAt; }
    public Long getCreatedBy() { return createdBy; }
    public Long getUpdatedBy() { return updatedBy; }

    public void setUserId(Long userId) { this.userId = userId; }
    public void setLessonId(Long lessonId) { this.lessonId = lessonId; }
    public void setCompleted(Boolean completed) { this.completed = completed; }
    public void setCompletedAt(LocalDateTime completedAt) { this.completedAt = completedAt; }
    public void setProgressPercent(Integer progressPercent) { this.progressPercent = progressPercent; }
    public void setViews(Integer views) { this.views = views; }
    public void setFirstViewAt(LocalDateTime firstViewAt) { this.firstViewAt = firstViewAt; }
    public void setLastViewAt(LocalDateTime lastViewAt) { this.lastViewAt = lastViewAt; }
    public void setCreatedBy(Long createdBy) { this.createdBy = createdBy; }
    public void setUpdatedBy(Long updatedBy) { this.updatedBy = updatedBy; }
}