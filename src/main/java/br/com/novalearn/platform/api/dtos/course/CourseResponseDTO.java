package br.com.novalearn.platform.api.dtos.course;

import br.com.novalearn.platform.api.dtos.BaseDTO;
import br.com.novalearn.platform.domain.enums.CourseStatus;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class CourseResponseDTO extends BaseDTO {
    private Long userId;
    private Long categoryId;
    private String name;
    private String shortDescription;
    private String longDescription;
    private BigDecimal price;
    private Boolean paid;
    private String thumbnailUrl;
    private Integer duration;
    private Integer numLessons;
    private Integer numStudents;
    private Integer numRatingTotal;
    private Integer numRatingCount;
    private LocalDateTime publishedAt;
    private String slug;
    private CourseStatus status;
    private Long createdBy;
    private Long updatedBy;

    public CourseResponseDTO() {}

    public CourseResponseDTO(
            Long id,
            Boolean active,
            Boolean deleted,
            LocalDateTime createdAt,
            LocalDateTime updatedAt,
            String observations,
            Long userId,
            Long categoryId,
            String name,
            String shortDescription,
            String longDescription,
            BigDecimal price,
            Boolean paid,
            String thumbnailUrl,
            Integer duration,
            Integer numLessons,
            Integer numStudents,
            Integer numRatingTotal,
            Integer numRatingCount,
            LocalDateTime publishedAt,
            String slug,
            CourseStatus status,
            Long createdBy,
            Long updatedBy
    ) {
        super(id, active, deleted, createdAt, updatedAt, observations);
        this.userId = userId;
        this.categoryId = categoryId;
        this.name = name;
        this.shortDescription = shortDescription;
        this.longDescription = longDescription;
        this.price = price;
        this.paid = paid;
        this.thumbnailUrl = thumbnailUrl;
        this.duration = duration;
        this.numLessons = numLessons;
        this.numStudents = numStudents;
        this.numRatingTotal = numRatingTotal;
        this.numRatingCount = numRatingCount;
        this.publishedAt = publishedAt;
        this.slug = slug;
        this.status = status;
        this.createdBy = createdBy;
        this.updatedBy = updatedBy;
    }

    public Long getUserId() { return userId; }
    public Long getCategoryId() { return categoryId; }
    public String getName() { return name; }
    public String getShortDescription() { return shortDescription; }
    public String getLongDescription() { return longDescription; }
    public BigDecimal getPrice() { return price; }
    public Boolean getPaid() { return paid; }
    public String getThumbnailUrl() { return thumbnailUrl; }
    public Integer getDuration() { return duration; }
    public Integer getNumLessons() { return numLessons; }
    public Integer getNumStudents() { return numStudents; }
    public Integer getNumRatingTotal() { return numRatingTotal; }
    public Integer getNumRatingCount() { return numRatingCount; }
    public LocalDateTime getPublishedAt() { return publishedAt; }
    public String getSlug() { return slug; }
    public CourseStatus getStatus() { return status; }
    public Long getCreatedBy() { return createdBy; }
    public Long getUpdatedBy() { return updatedBy; }

    public void setUserId(Long userId) { this.userId = userId; }
    public void setCategoryId(Long categoryId) { this.categoryId = categoryId; }
    public void setName(String name) { this.name = name; }
    public void setShortDescription(String shortDescription) { this.shortDescription = shortDescription; }
    public void setLongDescription(String longDescription) { this.longDescription = longDescription; }
    public void setPrice(BigDecimal price) { this.price = price; }
    public void setPaid(Boolean paid) { this.paid = paid; }
    public void setThumbnailUrl(String thumbnailUrl) { this.thumbnailUrl = thumbnailUrl; }
    public void setDuration(Integer duration) { this.duration = duration; }
    public void setNumLessons(Integer numLessons) { this.numLessons = numLessons; }
    public void setNumStudents(Integer numStudents) { this.numStudents = numStudents; }
    public void setNumRatingTotal(Integer numRatingTotal) { this.numRatingTotal = numRatingTotal; }
    public void setNumRatingCount(Integer numRatingCount) { this.numRatingCount = numRatingCount; }
    public void setPublishedAt(LocalDateTime publishedAt) { this.publishedAt = publishedAt; }
    public void setSlug(String slug) { this.slug = slug; }
    public void setStatus(CourseStatus status) { this.status = status; }
    public void setCreatedBy(Long createdBy) { this.createdBy = createdBy; }
    public void setUpdatedBy(Long updatedBy) { this.updatedBy = updatedBy; }
}