package br.com.novalearn.platform.domain.entities.user;

import br.com.novalearn.platform.core.exception.business.InvalidStateException;
import br.com.novalearn.platform.core.exception.business.ValidationException;
import br.com.novalearn.platform.domain.converters.BooleanToYNConverter;
import br.com.novalearn.platform.domain.entities.base.BaseEntity;
import br.com.novalearn.platform.domain.entities.lesson.Lesson;
import br.com.novalearn.platform.infra.database.sequences.DatabaseSequences;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "T_SINL_USER_LESSON_PROGRESS")
@SequenceGenerator(
        name = DatabaseSequences.USER_LESSON_PROGRESS_SEQ,
        sequenceName = DatabaseSequences.USER_LESSON_PROGRESS_SEQ,
        allocationSize = 1
)
public class UserLessonProgress extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = DatabaseSequences.USER_LESSON_PROGRESS_SEQ)
    @Column(name = "cod_user_lesson_progress", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cod_user", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cod_lesson", nullable = false)
    private Lesson lesson;

    @Convert(converter = BooleanToYNConverter.class)
    @Column(name = "ind_completed", nullable = false)
    private boolean completed;

    @Column(name = "dat_first_view")
    private LocalDateTime firstViewAt;

    @Column(name = "dat_last_view")
    private LocalDateTime lastViewAt;

    @Column(name = "dat_completed")
    private LocalDateTime completedAt;

    @Min(0)
    @Max(100)
    @Column(name = "num_progress_percent")
    private Integer progressPercent;

    @Min(0)
    @Column(name = "qtd_views")
    private Integer views;

    @Column(name = "obs_progress", length = 500)
    private String observations;

    protected UserLessonProgress() {}

    public static UserLessonProgress start(User user, Lesson lesson, LocalDateTime now) {
        if(user == null) throw new ValidationException("User cannot be null.");

        if(lesson == null) throw new ValidationException("Lesson cannot be null.");

        if(now == null) throw new ValidationException("Start date is required.");

        UserLessonProgress progress = new UserLessonProgress();

        progress.user = user;
        progress.lesson = lesson;
        progress.firstViewAt = now;
        progress.lastViewAt = now;
        progress.progressPercent = 0;
        progress.views = 1;
        progress.completed = false;
        progress.activate();
        progress.markAsNotDeleted();
        progress.setCreatedAt(now);

        return progress;
    }

    public void registerView(LocalDateTime now) {
        ensureNotDeleted();

        if(now == null) throw new ValidationException("View date is required.");

        if(lastViewAt != null && now.isBefore(lastViewAt)) throw new InvalidStateException("View date cannot go backwards.");

        this.lastViewAt = now;
        this.views = (views == null ? 1 : views + 1);
    }

    public void updateProgress(int progress, LocalDateTime now) {
        ensureNotDeleted();

        if(progress < 0 || progress > 100) throw new ValidationException("Progress must be between 0 and 100.");

        if(completed) throw new InvalidStateException("Completed lesson cannot have progress updated.");

        if(now == null) throw new ValidationException("Progress update date is required.");

        this.progressPercent = progress;
        this.lastViewAt = now;

        if(progress == 100) complete(now);
    }

    public void complete(LocalDateTime now) {
        ensureNotDeleted();

        if(completed) throw new InvalidStateException("Lesson already completed.");

        this.completed = true;
        this.progressPercent = 100;
        this.completedAt = Objects.requireNonNull(now, "Completion date is required.");
    }

    public Long getId() { return id; }
    public User getUser() { return user; }
    public Lesson getLesson() { return lesson; }
    public boolean isCompleted() { return completed; }
    public LocalDateTime getFirstViewAt() { return firstViewAt; }
    public LocalDateTime getLastViewAt() { return lastViewAt; }
    public LocalDateTime getCompletedAt() { return completedAt; }
    public Integer getProgressPercent() { return progressPercent; }
    public Integer getViews() { return views; }
    public String getObservations() { return observations; }

    @Override
    public String toString() {
        return "UserLessonProgress{" +
                "id=" + id +
                ", userId=" + (user != null ? user.getId() : null) +
                ", lessonId=" + (lesson != null ? lesson.getId() : null) +
                ", progress=" + progressPercent +
                ", completed=" + completed +
                ", views=" + views +
                ", active=" + isActive() +
                ", deleted=" + isDeleted() +
                '}';
    }
}