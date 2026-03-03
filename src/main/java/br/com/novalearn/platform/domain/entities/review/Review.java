package br.com.novalearn.platform.domain.entities.review;

import br.com.novalearn.platform.core.exception.business.InvalidStateException;
import br.com.novalearn.platform.core.exception.business.ValidationException;
import br.com.novalearn.platform.domain.converters.BooleanToYNConverter;
import br.com.novalearn.platform.domain.entities.base.BaseEntity;
import br.com.novalearn.platform.domain.entities.course.Course;
import br.com.novalearn.platform.domain.entities.user.User;
import br.com.novalearn.platform.domain.enums.ReviewStatus;
import br.com.novalearn.platform.infra.database.sequences.DatabaseSequences;
import br.com.novalearn.platform.infra.jpa.converter.status.ReviewStatusConverter;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

import java.time.LocalDateTime;

@Entity
@Table(name = "T_SINL_REVIEW")
@SequenceGenerator(
        name = DatabaseSequences.REVIEW_SEQ,
        sequenceName = DatabaseSequences.REVIEW_SEQ,
        allocationSize = 1
)
public class Review extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = DatabaseSequences.REVIEW_SEQ)
    @Column(name = "cod_review")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cod_course", nullable = false)
    private Course course;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cod_user", nullable = false)
    private User user;

    @Min(1)
    @Max(5)
    @Column(name = "num_rating")
    private Integer rating;

    @Lob
    @Column(name = "txt_comment")
    private String comment;

    @Convert(converter = ReviewStatusConverter.class)
    @Column(name = "sta_review", length = 30)
    private ReviewStatus status = ReviewStatus.PENDING;

    @Column(name = "dat_review")
    private LocalDateTime reviewAt;

    @Column(name = "dat_published")
    private LocalDateTime publishedAt;

    @Convert(converter = BooleanToYNConverter.class)
    @Column(name = "ind_anonymous", length = 1, nullable = false)
    private boolean anonymous;

    @Column(name = "obs_review", length = 500)
    private String observations;

    protected Review() {}

    public static Review create(
            User user,
            Course course,
            Integer rating,
            String comment,
            boolean anonymous,
            String observations,
            LocalDateTime now
    ) {
        validateCreation(user, course, rating, now);

        Review review = new Review();

        review.user = user;
        review.course = course;
        review.rating = rating;
        review.comment = comment;
        review.status = ReviewStatus.PENDING;
        review.anonymous = anonymous;
        review.observations = observations;
        review.reviewAt = now;
        review.publishedAt = null;

        review.activate();
        review.markAsNotDeleted();
        review.auditCreate(user.getId(), now);

        return review;
    }

    private static void validateCreation(
            User user,
            Course course,
            Integer rating,
            LocalDateTime now
    ) {
        if(user == null)  throw new ValidationException("User is required.");

        if(course == null) throw new ValidationException("Course is required.");

        if(rating == null || rating < 1 || rating > 5) throw new ValidationException("Rating must be between 1 and 5.");

        if(now == null) throw new ValidationException("Review date is required.");
    }

    public void edit(Integer newRating, String newComment, Boolean anonymous) {
        ensureNotDeleted();

        if(this.status != ReviewStatus.PENDING)
            throw new InvalidStateException("Only pending reviews can be edited.");

        if(newRating != null) {
            if(newRating < 1 || newRating > 5) throw new ValidationException("Rating must be between 1 and 5.");
            this.rating = newRating;
        }

        if(newComment != null) {
            if(newComment.length() < 3) throw new ValidationException("Comment must contain at least 3 characters.");
            this.comment = newComment;
        }

        if(anonymous != null) this.anonymous = anonymous;
    }

    public void approveAndPublish(LocalDateTime now) {
        ensureNotDeleted();

        if(this.status != ReviewStatus.PENDING)
            throw new InvalidStateException("Only pending reviews can be approved.");

        if(now == null) throw new ValidationException("Publish date is required.");

        this.status =  ReviewStatus.APPROVED;
        this.publishedAt = now;

        course.registerRating(this.rating);
    }

    public void reject(String observation) {
        ensureNotDeleted();

        if(this.status != ReviewStatus.PENDING)
            throw new InvalidStateException("Only pending reviews can be rejected.");

        this.status = ReviewStatus.REJECTED;
        this.observations = observation;
    }

    public boolean isApproved() {
        return this.status == ReviewStatus.APPROVED;
    }

    public Long getId() { return id; }
    public Course getCourse() { return course; }
    public User getUser() { return user; }
    public Integer getRating() { return rating; }
    public String getComment() { return comment; }
    public ReviewStatus getStatus() { return status; }
    public LocalDateTime getReviewAt() { return reviewAt; }
    public LocalDateTime getPublishedAt() { return publishedAt; }
    public boolean isAnonymous() { return anonymous; }
    public String getObservations() { return observations; }

    @Override
    public String toString() {
        return "Review{" +
                "id=" + id +
                ", courseId=" + (course != null ? course.getId() : null) +
                ", userId=" + (user != null ? user.getId() : null) +
                ", rating=" + rating +
                ", anonymous=" + anonymous +
                ", publishedAt=" + publishedAt +
                ", active=" + isActive() +
                ", deleted=" + isDeleted() +
                '}';
    }
}