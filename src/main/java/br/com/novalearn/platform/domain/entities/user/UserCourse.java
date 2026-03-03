package br.com.novalearn.platform.domain.entities.user;

import br.com.novalearn.platform.api.events.AggregateRoot;
import br.com.novalearn.platform.core.exception.business.ForbiddenOperationException;
import br.com.novalearn.platform.core.exception.business.InvalidStateException;
import br.com.novalearn.platform.core.exception.business.ValidationException;
import br.com.novalearn.platform.domain.converters.BooleanToYNConverter;
import br.com.novalearn.platform.domain.entities.course.Course;
import br.com.novalearn.platform.domain.enums.EnrollmentStatus;
import br.com.novalearn.platform.domain.events.certificate.CertificateIssuedEvent;
import br.com.novalearn.platform.domain.events.course.CourseCompletedEvent;
import br.com.novalearn.platform.domain.events.enrollment.EnrollmentCancelledEvent;
import br.com.novalearn.platform.infra.database.sequences.DatabaseSequences;
import br.com.novalearn.platform.infra.jpa.converter.status.EnrollmentStatusConverter;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

@Entity
@Table(name = "T_SINL_USER_COURSE")
@SequenceGenerator(
        name = DatabaseSequences.USER_COURSE_SEQ,
        sequenceName = DatabaseSequences.USER_COURSE_SEQ,
        allocationSize = 1
)
public class UserCourse extends AggregateRoot {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = DatabaseSequences.USER_COURSE_SEQ)
    @Column(name = "cod_user_course", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cod_user", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cod_course", nullable = false)
    private Course course;

    @NotNull
    @Column(name = "dat_enrolled", nullable = false)
    private LocalDateTime enrolledAt;

    @Column(name = "dat_completed")
    private LocalDateTime completedAt;

    @Convert(converter = EnrollmentStatusConverter.class)
    @Column(name = "sta_enrollment", length = 30)
    private EnrollmentStatus enrollmentStatus = EnrollmentStatus.ENROLLED;

    @Min(0)
    @Max(100)
    @Column(name = "num_progress_percent")
    private Integer progressPercent;

    @Column(name = "val_paid_amount", precision = 10, scale = 2)
    private BigDecimal paidAmount;

    @Size(max = 20)
    @Column(name = "sig_payment_method", length = 20)
    private String paymentMethod;

    @Convert(converter = BooleanToYNConverter.class)
    @Column(name = "ind_certificate_issued", length = 1, nullable = false)
    private boolean certificateIssued;

    @Column(name = "obs_enrollment", length = 500)
    private String observations;

    protected UserCourse() {}

    public static UserCourse enroll(User user, Course course, LocalDateTime now) {
        if(user == null) throw new ValidationException("User cannot be null for enrollment.");

        if(course == null) throw new ValidationException("Course cannot be null for enrollment.");

        if(now == null) throw new ValidationException("Enrollment date cannot be null.");

        UserCourse enrollment = new UserCourse();

        enrollment.user = user;
        enrollment.course = course;
        enrollment.enrolledAt = now;
        enrollment.enrollmentStatus = EnrollmentStatus.ENROLLED;
        enrollment.progressPercent = 0;
        enrollment.certificateIssued = false;

        return enrollment;
    }

    public void updateProgress(int progress, LocalDateTime now) {
        ensureNotDeleted();
        ensureProgressUpdatable();

        if(progress < 0 || progress > 100) throw new ValidationException("Progress must be between 0 and 100.");

        if(progress < this.progressPercent) throw new InvalidStateException("Progress cannot decrease");

        this.progressPercent = progress;

        if(progress > 0 && enrollmentStatus == EnrollmentStatus.ENROLLED)
            this.enrollmentStatus = EnrollmentStatus.IN_PROGRESS;

        if(progress == 100)
            complete(now);
    }

    public void registerPayment(
            BigDecimal amount,
            String method,
            LocalDateTime now
    ) {
        ensureNotDeleted();

        if(amount == null || amount.compareTo(BigDecimal.ZERO) <= 0)
            throw new ValidationException("Payment amount must be positive.");

        if(method == null || method.isBlank()) throw new ValidationException("Payment method is required.");

        if(this.paidAmount != null) throw new InvalidStateException("Payment already registered.");

        if(now == null) throw new ValidationException("Payment date is required.");

        this.paidAmount = amount;
        this.paymentMethod = method.trim();

        auditUpdate(5L, now);
    }

    public void complete(LocalDateTime now) {
        ensureNotDeleted();

        if(!canBeCompleted())
            throw new InvalidStateException("Enrollment cannot be completed from status: " + enrollmentStatus);

        this.enrollmentStatus = EnrollmentStatus.COMPLETED;
        this.progressPercent = 100;

        this.completedAt = Objects.requireNonNull(now, "Completion date cannot be null");

        registerEvent(new CourseCompletedEvent(
                this.user,
                this.course,
                this.completedAt
                )
        );
    }

    public void cancel(String reason) {
        ensureNotDeleted();

        if(isFinalState())
            throw new InvalidStateException("Enrollment cannot be cancelled from status: " + enrollmentStatus);

        if(reason == null || reason.isBlank())
            throw new ValidationException("Cancellation reason cannot be empty.");

        this.enrollmentStatus = EnrollmentStatus.CANCELLED;

        this.observations = reason;

        registerEvent(new EnrollmentCancelledEvent(
                this.getUser().getId(),
                this.getCourse().getId(),
                LocalDateTime.now()
        ));
    }

    public void refund(String reason) {
        ensureNotDeleted();

        if(enrollmentStatus == EnrollmentStatus.COMPLETED)
            throw new ForbiddenOperationException("Completed enrollment cannot be refunded.");

        this.enrollmentStatus = EnrollmentStatus.REFUNDED;

        this.observations = reason;
    }

    public void issueCertificate() {
        ensureNotDeleted();

        if(enrollmentStatus != EnrollmentStatus.COMPLETED)
            throw new InvalidStateException("Certificate can only be issued after course completion.");

        if(certificateIssued) throw new InvalidStateException("Certificate already issued.");

        this.certificateIssued = true;

        registerEvent(new CertificateIssuedEvent(
                this.getUser().getId(),
                this.getCourse().getId(),
                LocalDateTime.now()
        ));
    }

    private void ensureProgressUpdatable() {
        if(isFinalState()) {
            throw new InvalidStateException("Progress cannot be updated when enrollment is " + enrollmentStatus + ".");
        }
    }

    private boolean isFinalState() {
        return enrollmentStatus == EnrollmentStatus.COMPLETED
                || enrollmentStatus == EnrollmentStatus.CANCELLED
                || enrollmentStatus == EnrollmentStatus.REFUNDED
                || enrollmentStatus == EnrollmentStatus.EXPIRED;
    }

    private boolean canBeCompleted() {
        return enrollmentStatus == EnrollmentStatus.ENROLLED || enrollmentStatus == EnrollmentStatus.IN_PROGRESS;
    }

    public void ensureNotDeleted() {
        if(isDeleted()) throw new ForbiddenOperationException("Operation not allowed on deleted user course.");
    }

    public Long getId() { return id; }
    public User getUser() { return user; }
    public Course getCourse() { return course; }
    public LocalDateTime getEnrolledAt() { return enrolledAt; }
    public LocalDateTime getCompletedAt() { return completedAt; }
    public EnrollmentStatus getEnrollmentStatus() { return enrollmentStatus; }
    public Integer getProgressPercent() { return progressPercent; }
    public BigDecimal getPaidAmount() { return paidAmount; }
    public String getPaymentMethod() { return paymentMethod; }
    public boolean isCertificateIssued() { return certificateIssued; }
    public String getObservations() { return observations; }

    @Override
    public String toString() {
        return "UserCourse{" +
                "id=" + id +
                ", userId=" + (user != null ? user.getId() : null) +
                ", courseId=" + (course != null ? course.getId() : null) +
                ", status=" + enrollmentStatus +
                ", progress=" + progressPercent +
                ", certificateIssued=" + certificateIssued +
                ", active=" + isActive() +
                ", deleted=" + isDeleted() +
                '}';
    }
}