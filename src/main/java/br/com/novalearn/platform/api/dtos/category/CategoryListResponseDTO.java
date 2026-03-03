package br.com.novalearn.platform.api.dtos.category;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class CategoryListResponseDTO {
    private Long id;
    private Long parentCategoryId;
    private String name;
    private String abbreviation;
    private Boolean active;
    private Boolean deleted;

    public CategoryListResponseDTO() {}

    public CategoryListResponseDTO(
            Long id,
            Long parentCategoryId,
            String name,
            String abbreviation,
            Boolean active,
            Boolean deleted
    ) {
        this.id = id;
        this.parentCategoryId = parentCategoryId;
        this.name = name;
        this.abbreviation = abbreviation;
        this.active = active;
        this.deleted = deleted;
    }

    public Long getId() { return id; }
    public Long getParentCategoryId() { return parentCategoryId; }
    public String getName() { return name; }
    public String getAbbreviation() { return abbreviation; }
    public Boolean getActive() { return active; }
    public Boolean getDeleted() { return deleted; }

    public void setId(Long id) { this.id = id; }
    public void setParentCategoryId(Long parentCategoryId) { this.parentCategoryId = parentCategoryId; }
    public void setName(String name) { this.name = name; }
    public void setAbbreviation(String abbreviation) { this.abbreviation = abbreviation; }
    public void setActive(Boolean active) { this.active = active; }
    public void setDeleted(Boolean deleted) { this.deleted = deleted; }
}