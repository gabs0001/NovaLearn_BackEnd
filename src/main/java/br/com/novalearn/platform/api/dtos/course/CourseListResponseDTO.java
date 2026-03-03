package br.com.novalearn.platform.api.dtos.course;

import br.com.novalearn.platform.domain.enums.CourseStatus;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.math.BigDecimal;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class CourseListResponseDTO {
    private Long id;
    private Long userId;
    private Long categoryId;
    private String name;
    private String shortDescription;
    private CourseStatus status;
    private String thumbnailUrl;
    private BigDecimal price;
    private Boolean paid;
    private Integer numStudents;
    private Integer numRatingTotal;
    private Integer numRatingCount;
    private String slug;
    private Boolean active;
    private Boolean deleted;

    public CourseListResponseDTO() {}

    public CourseListResponseDTO(
            Long id,
            Long userId,
            Long categoryId,
            String name,
            String shortDescription,
            CourseStatus status,
            String thumbnailUrl,
            BigDecimal price,
            Boolean paid,
            Integer numStudents,
            Integer numRatingTotal,
            Integer numRatingCount,
            String slug,
            Boolean active,
            Boolean deleted
    ) {
        this.id = id;
        this.userId = userId;
        this.categoryId = categoryId;
        this.name = name;
        this.shortDescription = shortDescription;
        this.status = status;
        this.thumbnailUrl = thumbnailUrl;
        this.price = price;
        this.paid = paid;
        this.numStudents = numStudents;
        this.numRatingTotal = numRatingTotal;
        this.numRatingCount = numRatingCount;
        this.slug = slug;
        this.active = active;
        this.deleted = deleted;
    }

    public Long getId() { return id; }
    public Long getUserId() { return userId; }
    public Long getCategoryId() { return categoryId; }
    public String getName() { return name; }
    public String getShortDescription() { return shortDescription; }
    public CourseStatus getStatus() { return status; }
    public String getThumbnailUrl() { return thumbnailUrl; }
    public BigDecimal getPrice() { return price; }
    public Boolean getPaid() { return paid; }
    public Integer getNumStudents() { return numStudents; }
    public Integer getNumRatingTotal() { return numRatingTotal; }
    public Integer getNumRatingCount() { return numRatingCount; }
    public String getSlug() { return slug; }
    public Boolean getActive() { return active; }
    public Boolean getDeleted() { return deleted; }

    public void setId(Long id) { this.id = id; }
    public void setUserId(Long userId) { this.userId = userId; }
    public void setCategoryId(Long categoryId) { this.categoryId = categoryId; }
    public void setName(String name) { this.name = name; }
    public void setShortDescription(String shortDescription) { this.shortDescription = shortDescription; }
    public void setStatus(CourseStatus status) { this.status = status; }
    public void setThumbnailUrl(String thumbnailUrl) { this.thumbnailUrl = thumbnailUrl; }
    public void setPrice(BigDecimal price) { this.price = price; }
    public void setPaid(Boolean paid) { this.paid = paid; }
    public void setNumStudents(Integer numStudents) { this.numStudents = numStudents; }
    public void setNumRatingTotal(Integer numRatingTotal) { this.numRatingTotal = numRatingTotal; }
    public void setNumRatingCount(Integer numRatingCount) { this.numRatingCount = numRatingCount; }
    public void setSlug(String slug) { this.slug = slug; }
    public void setActive(Boolean active) { this.active = active; }
    public void setDeleted(Boolean deleted) { this.deleted = deleted; }
}