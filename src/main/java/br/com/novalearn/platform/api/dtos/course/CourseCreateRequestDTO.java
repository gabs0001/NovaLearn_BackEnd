package br.com.novalearn.platform.api.dtos.course;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.*;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class CourseCreateRequestDTO {
    @NotNull(message = "Category ID is required.")
    private Long categoryId;

    @NotBlank(message = "Course name is required!")
    @Size(max = 120, message = "Course name must not exceed 120 characters")
    private String name;

    @NotBlank(message = "Short description is required!")
    @Size(max = 200, message = "Short description must not exceed 200 characters.")
    private String shortDescription;

    @NotBlank(message = "Long description is required.")
    @Size(max = 5000, message = "Long description must not exceed 5000 characters.")
    private String longDescription;

    @NotBlank(message = "Thumbnail URL is required.")
    @Size(max = 500, message = "Thumbnail URL must not exceed 500 characters.")
    @Pattern(
            regexp = "^(http|https)://.*$",
            message = "Thumbnail URL must be a valid URL."
    )
    private String thumbnailUrl;

    public CourseCreateRequestDTO() {}

    public CourseCreateRequestDTO(
            Long categoryId,
            String name,
            String shortDescription,
            String longDescription,
            String thumbnailUrl
    ) {
        this.categoryId = categoryId;
        this.name = name;
        this.shortDescription = shortDescription;
        this.longDescription = longDescription;
        this.thumbnailUrl = thumbnailUrl;
    }

    public Long getCategoryId() { return categoryId; }
    public String getName() { return name; }
    public String getShortDescription() { return shortDescription; }
    public String getLongDescription() { return longDescription; }
    public String getThumbnailUrl() { return thumbnailUrl; }

    public void setCategoryId(Long categoryId) { this.categoryId = categoryId; }
    public void setName(String name) { this.name = name; }
    public void setShortDescription(String shortDescription) { this.shortDescription = shortDescription; }
    public void setLongDescription(String longDescription) { this.longDescription = longDescription; }
    public void setThumbnailUrl(String thumbnailUrl) { this.thumbnailUrl = thumbnailUrl; }
}