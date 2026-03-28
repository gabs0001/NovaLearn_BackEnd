package br.com.novalearn.platform.api.dtos.course;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class CourseUpdateRequestDTO {
    @Size(max = 120, message = "Course name must not exceed 120 characters.")
    private String name;

    @Size(max = 200, message = "Short description must not exceed 200 characters.")
    private String shortDescription;

    @Size(max = 5000, message = "Long description must not exceed 5000 characters.")
    private String longDescription;

    @DecimalMin(value = "0.0", inclusive = true, message = "Price cannot be negative.")
    private BigDecimal price;

    private Boolean paid;

    @Size(max = 500, message = "Thumbnail URL must not exceed 500 characters.")
    @Pattern(
            regexp = "^(http|https)://.*$",
            message = "Thumbnail URL must be a valid URL."
    )
    private String thumbnailUrl;

    private Long categoryId;

    @Size(max = 200, message = "Slug must not exceed 200 characters.")
    @Pattern(
            regexp = "^[a-z0-9]+(?:-[a-z0-9]+)*$",
            message = "Slug must contain only lowercase letters, numbers, and hyphens."
    )
    private String slug;

    @Size(max = 2000, message = "Observations must not exceed 2000 characters.")
    private String observations;

    private Boolean active;
    private Boolean deleted;

    public CourseUpdateRequestDTO() {}

    public CourseUpdateRequestDTO(
            String name,
            String shortDescription,
            String longDescription,
            BigDecimal price,
            Boolean paid,
            String thumbnailUrl,
            Long categoryId,
            String slug,
            String observations,
            Boolean active,
            Boolean deleted
    ) {
        this.name = name;
        this.shortDescription = shortDescription;
        this.longDescription = longDescription;
        this.price = price;
        this.paid = paid;
        this.thumbnailUrl = thumbnailUrl;
        this.categoryId = categoryId;
        this.slug = slug;
        this.observations = observations;
        this.active = active;
        this.deleted = deleted;
    }

    public String getName() { return name; }
    public String getShortDescription() { return shortDescription; }
    public String getLongDescription() { return longDescription; }
    public BigDecimal getPrice() { return price; }
    public Boolean getPaid() { return paid; }
    public String getThumbnailUrl() { return thumbnailUrl; }
    public Long getCategoryId() { return categoryId; }
    public String getSlug() { return slug; }
    public String getObservations() { return observations; }
    public Boolean getActive() { return active; }
    public Boolean getDeleted() { return deleted; }

    public void setName(String name) { this.name = name; }
    public void setShortDescription(String shortDescription) { this.shortDescription = shortDescription; }
    public void setLongDescription(String longDescription) { this.longDescription = longDescription; }
    public void setPrice(BigDecimal price) { this.price = price; }
    public void setPaid(Boolean paid) { this.paid = paid; }
    public void setThumbnailUrl(String thumbnailUrl) { this.thumbnailUrl = thumbnailUrl; }
    public void setCategoryId(Long categoryId) { this.categoryId = categoryId; }
    public void setSlug(String slug) { this.slug = slug; }
    public void setObservations(String observations) { this.observations = observations; }
    public void setActive(Boolean active) { this.active = active; }
    public void setDeleted(Boolean deleted) { this.deleted = deleted; }
}