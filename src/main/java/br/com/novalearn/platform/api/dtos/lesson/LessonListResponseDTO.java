package br.com.novalearn.platform.api.dtos.lesson;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class LessonListResponseDTO {
    private Long id;
    private Long moduleId;
    private String name;
    private Integer sequence;
    private Integer durationSeconds;
    private Boolean visible;
    private Boolean active;

    public LessonListResponseDTO() {}

    public LessonListResponseDTO(
            Long id,
            Long moduleId,
            String name,
            Integer sequence,
            Integer durationSeconds,
            Boolean visible,
            Boolean active
    ) {
        this.id = id;
        this.moduleId = moduleId;
        this.name = name;
        this.sequence = sequence;
        this.durationSeconds = durationSeconds;
        this.visible = visible;
        this.active = active;
    }

    public Long getId() { return id; }
    public Long getModuleId() { return moduleId; }
    public String getName() { return name; }
    public Integer getSequence() { return sequence; }
    public Integer getDurationSeconds() { return durationSeconds; }
    public Boolean getVisible() { return visible; }
    public Boolean getActive() { return active; }

    public void setId(Long id) { this.id = id; }
    public void setModuleId(Long moduleId) { this.moduleId = moduleId; }
    public void setName(String name) { this.name = name; }
    public void setSequence(Integer sequence) { this.sequence = sequence; }
    public void setDurationSeconds(Integer durationSeconds) { this.durationSeconds = durationSeconds; }
    public void setVisible(Boolean visible) { this.visible = visible; }
    public void setActive(Boolean active) { this.active = active; }
}