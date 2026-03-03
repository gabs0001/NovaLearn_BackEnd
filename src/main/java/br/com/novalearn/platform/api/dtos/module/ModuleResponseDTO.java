package br.com.novalearn.platform.api.dtos.module;

import br.com.novalearn.platform.api.dtos.BaseDTO;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ModuleResponseDTO extends BaseDTO {
    private Long courseId;
    private String name;
    private String description;
    private Integer sequence;
    private Long createdBy;
    private Long updatedBy;

    public ModuleResponseDTO() {}

    public ModuleResponseDTO(
            Long id,
            Boolean active,
            Boolean deleted,
            LocalDateTime createdAt,
            LocalDateTime updatedAt,
            String observations,
            Long courseId,
            String name,
            String description,
            Integer sequence,
            Long createdBy,
            Long updatedBy
    ) {
        super(id, active, deleted, createdAt, updatedAt, observations);
        this.courseId = courseId;
        this.name = name;
        this.description = description;
        this.sequence = sequence;
        this.createdBy = createdBy;
        this.updatedBy = updatedBy;
    }

    public Long getCourseId() { return courseId; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public Integer getSequence() { return sequence; }
    public Long getCreatedBy() { return createdBy; }
    public Long getUpdatedBy() { return updatedBy; }

    public void setCourseId(Long courseId) { this.courseId = courseId; }
    public void setName(String name) { this.name = name; }
    public void setDescription(String description) { this.description = description; }
    public void setSequence(Integer sequence) { this.sequence = sequence; }
    public void setCreatedBy(Long createdBy) { this.createdBy = createdBy; }
    public void setUpdatedBy(Long updatedBy) { this.updatedBy = updatedBy; }
}