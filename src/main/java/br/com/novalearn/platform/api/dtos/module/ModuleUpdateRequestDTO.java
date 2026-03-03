package br.com.novalearn.platform.api.dtos.module;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ModuleUpdateRequestDTO {
    @Size(min = 3, max = 150, message = "name must be between 3 and 150 characters.")
    private String name;

    @Size(max = 500, message = "description must be a maximum of 500 characters.")
    private String description;

    @Positive(message = "sequence must be a positive number.")
    private Integer sequence;

    private Boolean active;
    private Boolean deleted;

    @Size(max = 500, message = "observations must be a maximum of 500 characters.")
    private String observations;

    public ModuleUpdateRequestDTO() {}

    public ModuleUpdateRequestDTO(
            String name,
            String description,
            Integer sequence,
            Boolean active,
            Boolean deleted,
            String observations
    ) {
        this.name = name;
        this.description = description;
        this.sequence = sequence;
        this.active = active;
        this.deleted = deleted;
        this.observations = observations;
    }

    public String getName() { return name; }
    public String getDescription() { return description; }
    public Integer getSequence() { return sequence; }
    public Boolean getActive() { return active; }
    public Boolean getDeleted() { return deleted; }
    public String getObservations() { return observations; }

    public void setName(String name) { this.name = name; }
    public void setDescription(String description) { this.description = description; }
    public void setSequence(Integer sequence) { this.sequence = sequence; }
    public void setActive(Boolean active) { this.active = active; }
    public void setDeleted(Boolean deleted) { this.deleted = deleted; }
    public void setObservations(String observations) { this.observations = observations; }
}