package br.com.novalearn.platform.api.dtos.lesson;

import br.com.novalearn.platform.api.dtos.BaseDTO;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class LessonResponseDTO extends BaseDTO {
    private Long moduleId;
    private String name;
    private String description;
    private Integer sequence;
    private Integer durationSeconds;
    private Boolean requireCompletion;
    private Boolean visible;
    private String previewUrl;
    private String notes;
    private Long createdBy;
    private Long updatedBy;

    public LessonResponseDTO() {}

    public LessonResponseDTO(
            Long id,
            Boolean active,
            Boolean deleted,
            LocalDateTime createdAt,
            LocalDateTime updatedAt,
            String observations,
            Long moduleId,
            String name,
            String description,
            Integer sequence,
            Integer durationSeconds,
            Boolean requireCompletion,
            Boolean visible,
            String previewUrl,
            String notes,
            Long createdBy,
            Long updatedBy
    ) {
        super(id, active, deleted, createdAt, updatedAt, observations);
        this.moduleId = moduleId;
        this.name = name;
        this.description = description;
        this.sequence = sequence;
        this.durationSeconds = durationSeconds;
        this.requireCompletion = requireCompletion;
        this.visible = visible;
        this.previewUrl = previewUrl;
        this.notes = notes;
        this.createdBy = createdBy;
        this.updatedBy = updatedBy;
    }

    public Long getModuleId() { return moduleId; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public Integer getSequence() { return sequence; }
    public Integer getDurationSeconds() { return durationSeconds; }
    public Boolean getRequireCompletion() { return requireCompletion; }
    public Boolean getVisible() { return visible; }
    public String getPreviewUrl() { return previewUrl; }
    public String getNotes() { return notes; }
    public Long getCreatedBy() { return createdBy; }
    public Long getUpdatedBy() { return updatedBy; }

    public void setModuleId(Long moduleId) { this.moduleId = moduleId; }
    public void setName(String name) { this.name = name; }
    public void setDescription(String description) { this.description = description; }
    public void setSequence(Integer sequence) { this.sequence = sequence; }
    public void setDurationSeconds(Integer durationSeconds) { this.durationSeconds = durationSeconds; }
    public void setRequireCompletion(Boolean requireCompletion) { this.requireCompletion = requireCompletion; }
    public void setVisible(Boolean visible) { this.visible = visible; }
    public void setPreviewUrl(String previewUrl) { this.previewUrl = previewUrl; }
    public void setNotes(String notes) { this.notes = notes; }
    public void setCreatedBy(Long createdBy) { this.createdBy = createdBy; }
    public void setUpdatedBy(Long updatedBy) { this.updatedBy = updatedBy; }
}