package br.com.novalearn.platform.api.dtos.category;

import br.com.novalearn.platform.api.dtos.BaseDTO;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class CategoryResponseDTO extends BaseDTO {
    private String name;
    private String description;
    private String abbreviation;
    private Long parentCategoryId;

    public CategoryResponseDTO() {}

    public CategoryResponseDTO(
            Long id,
            Boolean active,
            Boolean deleted,
            LocalDateTime createdAt,
            LocalDateTime updatedAt,
            String observations,
            String name,
            String description,
            String abbreviation,
            Long parentCategoryId
    ) {
        super(id, active, deleted, createdAt, updatedAt, observations);
        this.name = name;
        this.description = description;
        this.abbreviation = abbreviation;
        this.parentCategoryId = parentCategoryId;
    }

    public String getName() { return name; }
    public String getDescription() { return description; }
    public String getAbbreviation() { return abbreviation; }
    public Long getParentCategoryId() { return parentCategoryId; }

    public void setName(String name) { this.name = name; }
    public void setDescription(String description) { this.description = description; }
    public void setAbbreviation(String abbreviation) { this.abbreviation = abbreviation; }
    public void setParentCategoryId(Long parentCategoryId) { this.parentCategoryId = parentCategoryId; }
}