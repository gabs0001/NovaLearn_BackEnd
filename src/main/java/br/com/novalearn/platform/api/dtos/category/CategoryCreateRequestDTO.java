package br.com.novalearn.platform.api.dtos.category;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class CategoryCreateRequestDTO {
    private Long parentCategoryId;

    @NotBlank(message = "Name is required.")
    @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters.")
    private String name;

    @Size(max = 10, message = "Abbreviation must be at most 10 characters.")
    private String abbreviation;

    @Size(max = 255, message = "Description must be at most 500 characters.")
    private String description;

    @Size(max = 500)
    private String observations;

    public CategoryCreateRequestDTO() {}

    public CategoryCreateRequestDTO(
            Long parentCategoryId,
            String name,
            String abbreviation,
            String description,
            String observations
    ) {
        this.parentCategoryId = parentCategoryId;
        this.name = name;
        this.abbreviation = abbreviation;
        this.description = description;
        this.observations = observations;
    }

    public Long getParentCategoryId() { return parentCategoryId; }
    public String getName() { return name; }
    public String getAbbreviation() { return abbreviation; }
    public String getDescription() { return description; }
    public String getObservations() { return observations; }

    public void setParentCategoryId(Long parentCategoryId) { this.parentCategoryId = parentCategoryId; }
    public void setName(String name) { this.name = name; }
    public void setAbbreviation(String abbreviation) { this.abbreviation = abbreviation; }
    public void setDescription(String description) { this.description = description; }
    public void setObservations(String observations) { this.observations = observations; }
}