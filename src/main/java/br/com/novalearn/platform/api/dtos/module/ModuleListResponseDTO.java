package br.com.novalearn.platform.api.dtos.module;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ModuleListResponseDTO {
    private Long id;
    private Long courseId;
    private String name;
    private Integer sequence;
    private Boolean active;
    private Boolean deleted;

    public ModuleListResponseDTO() {}

    public ModuleListResponseDTO(
            Long id,
            Long courseId,
            String name,
            Integer sequence,
            Boolean active,
            Boolean deleted
    ) {
        this.id = id;
        this.courseId = courseId;
        this.name = name;
        this.sequence = sequence;
        this.active = active;
        this.deleted = deleted;
    }

    public Long getId() { return id; }
    public Long getCourseId() { return courseId; }
    public String getName() { return name; }
    public Integer getSequence() { return sequence; }
    public Boolean getActive() { return active; }
    public Boolean getDeleted() { return deleted; }

    public void setId(Long id) { this.id = id; }
    public void setCourseId(Long courseId) { this.courseId = courseId; }
    public void setName(String name) { this.name = name; }
    public void setSequence(Integer sequence) { this.sequence = sequence; }
    public void setActive(Boolean active) { this.active = active; }
    public void setDeleted(Boolean deleted) { this.deleted = deleted; }
}