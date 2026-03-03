package br.com.novalearn.platform.api.dtos.lesson;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class LessonCreateRequestDTO {
    @NotNull(message = "Module ID is required.")
    private Long moduleId;

    @NotBlank
    @Size(min = 3, max = 255)
    private String name;

    @Size(max = 2000, message = "Description must not exceed 2000 characters.")
    private String description;

    @NotNull(message = "Sequence is required.")
    @Min(value = 1, message = "Sequence must be at least 1.")
    private Integer sequence;

    @NotNull(message = "Duration is required.")
    @Min(value = 30, message = "Duration must be at least 30 seconds.")
    private Integer durationSeconds;

    @NotNull(message = "RequireCompletion is required.")
    private Boolean requireCompletion;

    @NotNull(message = "Visible is required.")
    private Boolean visible;

    @Size(max = 500, message = "Preview URL must not exceed 500 characters.")
    private String previewUrl;

    @Size(max = 1000, message = "Notes must not exceed 1000 characters.")
    private String notes;

    @Size(max = 1000, message = "Observations must not exceed 1000 characters.")
    private String observations;

    public LessonCreateRequestDTO() {}

    public LessonCreateRequestDTO(
            Long moduleId,
            String name,
            String description,
            Integer sequence,
            Integer durationSeconds,
            Boolean requireCompletion,
            Boolean visible,
            String previewUrl,
            String notes,
            String observations
    ) {
        this.moduleId = moduleId;
        this.name = name;
        this.description = description;
        this.sequence = sequence;
        this.durationSeconds = durationSeconds;
        this.requireCompletion = requireCompletion;
        this.visible = visible;
        this.previewUrl = previewUrl;
        this.notes = notes;
        this.observations = observations;
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
    public String getObservations() { return observations; }

    public void setModuleId(Long moduleId) { this.moduleId = moduleId; }
    public void setName(String name) { this.name = name; }
    public void setDescription(String description) { this.description = description; }
    public void setSequence(Integer sequence) { this.sequence = sequence; }
    public void setDurationSeconds(Integer durationSeconds) { this.durationSeconds = durationSeconds; }
    public void setRequireCompletion(Boolean requireCompletion) { this.requireCompletion = requireCompletion; }
    public void setVisible(Boolean visible) { this.visible = visible; }
    public void setPreviewUrl(String previewUrl) { this.previewUrl = previewUrl; }
    public void setNotes(String notes) { this.notes = notes; }
    public void setObservations(String observations) { this.observations = observations; }
}