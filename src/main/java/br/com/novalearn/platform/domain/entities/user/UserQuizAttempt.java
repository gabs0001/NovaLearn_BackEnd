package br.com.novalearn.platform.domain.entities.user;

import br.com.novalearn.platform.core.exception.business.ForbiddenOperationException;
import br.com.novalearn.platform.core.exception.business.InvalidStateException;
import br.com.novalearn.platform.core.exception.business.ValidationException;
import br.com.novalearn.platform.domain.converters.BooleanToYNConverter;
import br.com.novalearn.platform.domain.entities.base.BaseEntity;
import br.com.novalearn.platform.domain.entities.quiz.Quiz;
import br.com.novalearn.platform.domain.enums.QuizAttemptStatus;
import br.com.novalearn.platform.infra.database.sequences.DatabaseSequences;
import br.com.novalearn.platform.infra.jpa.converter.status.QuizAttemptStatusConverter;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "T_SINL_USER_QUIZ_ATTEMPT")
@SequenceGenerator(
        name = DatabaseSequences.USER_QUIZ_ATTEMPT_SEQ,
        sequenceName = DatabaseSequences.USER_QUIZ_ATTEMPT_SEQ,
        allocationSize = 1
)
public class UserQuizAttempt extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = DatabaseSequences.USER_QUIZ_ATTEMPT_SEQ)
    @Column(name = "cod_user_quiz_attempt", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cod_user", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cod_quiz", nullable = false)
    private Quiz quiz;

    @Column(name = "seq_attempt", length = 5, nullable = false)
    private Integer seqAttempt;

    @Column(name = "val_score", precision = 5, scale = 2)
    private BigDecimal score;

    @Column(name = "val_max_score", precision = 5, scale = 2)
    private BigDecimal maxScore;

    @Convert(converter = QuizAttemptStatusConverter.class)
    @Column(name = "sta_attempt", nullable = false, length = 20)
    private QuizAttemptStatus status;

    @Column(name = "dat_started", nullable = false)
    private LocalDateTime startedAt;

    @Column(name = "dat_finished")
    private LocalDateTime finishedAt;

    @Convert(converter = BooleanToYNConverter.class)
    @Column(name = "ind_passed", nullable = false, length = 1)
    private boolean passed;

    @Column(name = "obs_attempt", length = 500)
    private String observations;

    protected UserQuizAttempt() {}

    public static UserQuizAttempt start(User user, Quiz quiz, int seq, Long actorId, LocalDateTime now) {
        if(user == null) throw new ValidationException("User cannot be null.");

        if(quiz == null) throw new ValidationException("Quiz cannot be null.");

        if(seq <= 0) throw new ValidationException("Attempt sequence must be greater than zero.");

        if(now == null) throw new ValidationException("Start date is required.");

        UserQuizAttempt attempt = new UserQuizAttempt();

        attempt.user = user;
        attempt.quiz = quiz;
        attempt.seqAttempt = seq;
        attempt.startedAt = now;
        attempt.status = QuizAttemptStatus.STARTED;
        attempt.passed = false;

        attempt.auditCreate(actorId, now);

        return attempt;
    }

    public void finish(BigDecimal score, BigDecimal maxScore, LocalDateTime finishedAt, Long actorId, LocalDateTime now) {
        ensureNotDeleted();
        ensureFinishable();
        validateScore(score, maxScore);

        this.score = score;
        this.maxScore = maxScore;
        this.finishedAt = Objects.requireNonNull(finishedAt, "Finished date is required.");
        this.status = QuizAttemptStatus.FINISHED;
        this.passed = isPassingScore(score, maxScore);

        auditUpdate(actorId, now);
    }

    public void cancel(Long actorId, LocalDateTime now) {
        ensureNotDeleted();

        if(this.status != QuizAttemptStatus.STARTED) throw new InvalidStateException("Only started attempts can be cancelled.");

        this.finishedAt = Objects.requireNonNull(now, "Cancellation date is required.");
        this.status = QuizAttemptStatus.CANCELLED;
        this.passed = false;

        auditUpdate(actorId, now);
    }

    private void ensureFinishable() {
        if(status != QuizAttemptStatus.STARTED) throw new InvalidStateException("Attempt in status " + status + " cannot be finished.");
    }

    private void validateScore(BigDecimal score, BigDecimal maxScore) {
        if(score == null || maxScore == null) throw new ValidationException("Score and max score are required.");
        if(score.compareTo(BigDecimal.ZERO) < 0) throw new ValidationException("Score cannot be negative.");
        if(maxScore.compareTo(BigDecimal.ZERO) <= 0) throw new ValidationException("Max score must be greater than zero.");
        if(score.compareTo(maxScore) > 0) throw new ValidationException("Score cannot be greater than max score.");
    }

    private boolean isPassingScore(BigDecimal score, BigDecimal maxScore) {
        return score
                .divide(maxScore, 2, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100))
                .compareTo(BigDecimal.valueOf(70)) >= 0;
    }

    public void ensureNotDeleted() {
        if(isDeleted()) throw new ForbiddenOperationException("Operation not allowed on deleted user quiz attempt.");
    }

    public Long getId() { return id; }
    public User getUser() { return user; }
    public Quiz getQuiz() { return quiz; }
    public Integer getSeqAttempt() { return seqAttempt; }
    public BigDecimal getScore() { return score; }
    public BigDecimal getMaxScore() { return maxScore; }
    public QuizAttemptStatus getStatus() { return status; }
    public LocalDateTime getStartedAt() { return startedAt; }
    public LocalDateTime getFinishedAt() { return finishedAt; }
    public boolean isPassed() { return passed; }
    public String getObservations() { return observations; }

    @Override
    public String toString() {
        return "UserQuizAttempt{" +
                "id=" + id +
                ", userId=" + (user != null ? user.getId() : null) +
                ", quizId=" + (quiz != null ? quiz.getId() : null) +
                ", attempt=" + seqAttempt +
                ", status=" + status +
                ", passed=" + passed +
                ", active=" + isActive() +
                ", deleted=" + isDeleted() +
                '}';
    }
}