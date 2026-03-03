package br.com.novalearn.platform.domain.entities.course;

import br.com.novalearn.platform.core.exception.business.ValidationException;
import br.com.novalearn.platform.domain.converters.BooleanToYNConverter;
import br.com.novalearn.platform.domain.entities.base.BaseEntity;
import br.com.novalearn.platform.domain.entities.category.Category;
import br.com.novalearn.platform.domain.entities.user.User;
import br.com.novalearn.platform.domain.enums.CourseStatus;
import br.com.novalearn.platform.infra.database.sequences.DatabaseSequences;
import br.com.novalearn.platform.infra.jpa.converter.status.CourseStatusConverter;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "T_SINL_COURSE",
        indexes = {
                @Index(name = "IDX_T_SINL_COURSE_CATEGORY", columnList = "cod_category"),
                @Index(name = "IDX_T_SINL_COURSE_INSTRUCTOR", columnList = "cod_user")
        }
)
@SequenceGenerator(
        name = DatabaseSequences.COURSE_SEQ,
        sequenceName = DatabaseSequences.COURSE_SEQ,
        allocationSize = 1
)
public class Course extends BaseEntity {
    @Id
    @Column(name = "cod_course", nullable = false)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = DatabaseSequences.COURSE_SEQ)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cod_user")
    private User instructor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cod_category")
    private Category category;

    @Column(name = "nom_course", length = 120, nullable = false)
    private String name;

    @Column(name = "des_course", length = 255)
    private String shortDescription;

    @Lob
    @Column(name = "txt_course")
    private String longDescription;

    @Column(name = "val_price", precision = 10, scale = 2)
    private BigDecimal price;

    @Convert(converter = CourseStatusConverter.class)
    @Column(name = "sta_course", length = 30)
    private CourseStatus status = CourseStatus.DRAFT;

    @Convert(converter = BooleanToYNConverter.class)
    @Column(name = "ind_paid", length = 1, nullable = false)
    private boolean paid;

    @Column(name = "val_thumbnail_url", length = 500)
    private String thumbnailUrl;

    @Column(name = "num_duration_minutes")
    private Integer durationMinutes;

    @Column(name = "num_lessons")
    private Integer numLessons;

    @Column(name = "num_students")
    private Integer numStudents;

    @Column(name = "num_rating_total")
    private Integer ratingTotal;

    @Column(name = "num_rating_count")
    private Integer ratingCount;

    @Column(name = "dat_published")
    private LocalDateTime publishedAt;

    @Column(name = "txt_slug", length = 255, unique = true)
    private String slug;

    @Column(name = "obs_course", length = 500)
    private String observations;

    protected Course() {}

    public static Course create(String name, Category category, User instructor) {
        Course c = new Course();
        c.name = name;
        c.category = category;
        c.instructor = instructor;
        c.active = true;
        c.deleted = false;
        c.status = CourseStatus.DRAFT;
        c.sanitize();
        c.validateCreation();
        return c;
    }

    public void updateInfo(String name, String shortDesc, String longDesc, String observations) {
        if(name != null) this.name = name;
        if(shortDesc != null) this.shortDescription = shortDesc;
        if(longDesc != null) this.longDescription = longDesc;
        if(observations != null) this.observations = observations;

        sanitize();
        validateInvariants();
    }

    public void changeCategory(Category category) {
        if(category == null) throw new ValidationException("Category is required.");

        if(!category.isActive() || category.isDeleted())
            throw new ValidationException("Category must be active.");

        this.category = category;
    }

    public void defineSlug(String slug) {
        if(slug == null || slug.isBlank())
            throw new ValidationException("Course must have a slug to be published.");
        this.slug = slug.trim().toLowerCase();
    }

    public void registerLesson() {
        if(numLessons == null) numLessons = 0;
        numLessons++;
    }

    public void publish(LocalDateTime now) {
        if(this.status == CourseStatus.PUBLISHED)
            throw new ValidationException("Course is already published.");

        validatePublish();

        this.status = CourseStatus.PUBLISHED;

        if(now == null) throw new ValidationException("Publish date cannot be null.");
        this.publishedAt = now;
    }

    public void definePricing(boolean paid, BigDecimal price) {
        if(paid && (price == null || price.compareTo(BigDecimal.ZERO) <= 0)) {
            throw new ValidationException("Paid courses must have a price.");
        }

        this.paid = paid;
        if(!paid) this.price = BigDecimal.ZERO;
        else this.price = price;
    }

    public void registerEnrollment() {
        if(numStudents == null) numStudents = 0;
        numStudents++;
    }

    public void registerRating(int rating) {
        if(rating < 1 || rating > 5) throw new ValidationException("Rating must be between 1 and 5.");
        if(ratingTotal == null) ratingTotal = 0;
        if(ratingCount == null) ratingCount = 0;

        ratingTotal += rating;
        ratingCount++;
    }

    private void sanitize() {
        if(name != null) {
            name = name.trim();

            if (this.slug == null || this.slug.isBlank()) {
                this.slug = name.toLowerCase()
                        .replaceAll("[^a-zA-Z0-9]", "-")
                        .replaceAll("-+", "-");
            }
        }

        if(shortDescription != null) shortDescription = shortDescription.trim();
        if(longDescription != null) longDescription = longDescription.trim();
        if(slug != null) slug = slug.trim().toLowerCase();
        if(observations != null) observations = observations.trim();
    }

    private void validateCreation() {
        if(name == null || name.isBlank()) throw new ValidationException("Course name is required.");
        if(category == null) throw new ValidationException("Category is required.");
        if(instructor == null) throw new ValidationException("Instructor is required.");
    }

    protected void validateInvariants() {
        if(active && deleted) throw new ValidationException("Course cannot be active and deleted.");
    }

    private void validatePublish() {
        if(name == null || name.isBlank()) throw new ValidationException("Course must have a name.");
        if(category == null) throw new ValidationException("Course must have a category.");
        if(instructor == null) throw new ValidationException("Course must have an instructor.");
        if(numLessons == null || numLessons <= 0) throw new ValidationException("Course must have at least one lesson.");
        if(slug == null || slug.isBlank()) throw new ValidationException("Course must have a slug to be published.");
    }

    public Long getId() { return id; }
    public User getInstructor() { return instructor; }
    public Category getCategory() { return category; }
    public String getName() { return name; }
    public String getShortDescription() { return shortDescription; }
    public String getLongDescription() { return longDescription; }
    public BigDecimal getPrice() { return price; }
    public CourseStatus getStatus() { return status; }
    public boolean isPaid() { return paid; }
    public String getThumbnailUrl() { return thumbnailUrl; }
    public Integer getDuration() { return durationMinutes; }
    public Integer getNumLessons() { return numLessons; }
    public Integer getNumStudents() { return numStudents; }
    public Integer getNumRatingTotal() { return ratingTotal; }
    public Integer getNumRatingCount() { return ratingCount; }
    public LocalDateTime getPublishedAt() { return publishedAt; }
    public String getSlug() { return slug; }
    public String getObservations() { return observations; }

    @Override
    public String toString() {
        return "Course{" +
                "id=" + id +
                ", instructorId=" + (instructor != null ? instructor.getId() : null) +
                ", categoryId=" + (category != null ? category.getId() : null) +
                ", name='" + name + '\'' +
                ", status=" + status +
                ", paid=" + paid +
                ", price=" + price +
                ", numLessons=" + numLessons +
                ", numStudents=" + numStudents +
                ", ratingCount=" + ratingCount +
                ", publishedAt=" + publishedAt +
                ", active=" + isActive() +
                ", deleted=" + isDeleted() +
                '}';
    }
}