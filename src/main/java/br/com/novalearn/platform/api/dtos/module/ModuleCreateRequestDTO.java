package br.com.novalearn.platform.api.dtos.module;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ModuleCreateRequestDTO {
    @NotNull(message = "courseId is required.")
    private Long courseId;

    @NotBlank(message = "name is required.")
    @Size(min = 3, max = 150, message = "name must be between 3 and 150 characters.")
    private String name;

    @Size(max = 500, message = "description must be a maximum of 500 characters.")
    private String description;

    @NotNull(message = "sequence is required.")
    @Positive(message = "sequence must be a positive number.")
    private Integer sequence;

    @Size(max = 500, message = "observations must be a maximum of 500 characters.")
    private String observations;

    public ModuleCreateRequestDTO() {}

    public ModuleCreateRequestDTO(
            Long courseId,
            String name,
            String description,
            Integer sequence,
            String observations
    ) {
        this.courseId = courseId;
        this.name = name;
        this.description = description;
        this.sequence = sequence;
        this.observations = observations;
    }

    public Long getCourseId() { return courseId; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public Integer getSequence() { return sequence; }
    public String getObservations() { return observations; }

    public void setCourseId(Long courseId) { this.courseId = courseId; }
    public void setName(String name) { this.name = name; }
    public void setDescription(String description) { this.description = description; }
    public void setSequence(Integer sequence) { this.sequence = sequence; }
    public void setObservations(String observations) { this.observations = observations; }
}