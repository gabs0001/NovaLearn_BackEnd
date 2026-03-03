package br.com.novalearn.platform.domain.entities.quiz;

import br.com.novalearn.platform.core.exception.business.ForbiddenOperationException;
import br.com.novalearn.platform.core.exception.business.ValidationException;
import br.com.novalearn.platform.domain.entities.base.BaseEntity;
import br.com.novalearn.platform.domain.entities.user.UserQuizAnswer;
import br.com.novalearn.platform.infra.database.sequences.DatabaseSequences;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "T_SINL_QUIZ_QUESTION")
@SequenceGenerator(
        name = DatabaseSequences.QUIZ_QUESTION_SEQ,
        sequenceName = DatabaseSequences.QUIZ_QUESTION_SEQ,
        allocationSize = 1
)
public class QuizQuestion extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = DatabaseSequences.QUIZ_QUESTION_SEQ)
    @Column(name = "cod_quiz_question", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cod_quiz", nullable = false)
    private Quiz quiz;

    @OneToMany(mappedBy = "quizQuestion", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<QuizAnswerOption> options;

    @OneToMany(mappedBy = "quizQuestion", fetch = FetchType.LAZY)
    private List<UserQuizAnswer> userAnswers;

    @Column(name = "seq_question")
    private Integer sequence;

    @Lob
    @Column(name = "txt_question", nullable = false)
    private String description;

    @Column(name = "val_points")
    private Integer points;

    @Column(name = "obs_question", length = 500)
    private String observations;

    protected QuizQuestion() {}

    public static QuizQuestion create(
            Quiz quiz,
            Integer sequence,
            String description,
            Integer points
    ) {
        validateCreation(quiz, sequence, description, points);

        QuizQuestion question = new QuizQuestion();

        question.quiz = quiz;
        question.sequence = sequence;
        question.description = description;
        question.points = points;
        question.options = new ArrayList<>();

        return question;
    }

    private static void validateCreation(
            Quiz quiz,
            Integer sequence,
            String description,
            Integer points
    ) {
        if(quiz == null) throw new ValidationException("Quiz is required.");

        if(sequence == null || sequence < 1) throw new ValidationException("Question sequence must be at least 1.");

        if(description == null || description.isBlank()) throw new ValidationException("Question description is required.");

        if(points == null || points < 1) throw new ValidationException("Question points must be at least 1.");
    }

    public void changeDescription(String description) {
        ensureEditable();

        if(description == null || description.isBlank()) throw new ValidationException("Question description cannot be blank.");

        this.description = description;
    }

    public void changePoints(int points) {
        ensureEditable();

        if(points < 1) throw new ValidationException("Points must be at least 1.");

        this.points = points;
    }

    public void addOption(QuizAnswerOption option) {
        ensureEditable();

        if(option == null) throw new ValidationException("Answer option is required.");

        options.add(option);
        option.attachToQuestion(this);
    }

    public void removeOption(QuizAnswerOption option) {
        ensureEditable();
        options.remove(option);
    }

    public void validateHasCorrectOption() {
        boolean hasCorrect = options.stream().anyMatch(QuizAnswerOption::isCorrect);
        if(!hasCorrect) throw new ValidationException("Question must have at least one correct answer.");
    }

    private void ensureEditable() {
        if(isDeleted()) throw new ForbiddenOperationException("Deleted question cannot be modified.");
        if(!quiz.isActive()) throw new ForbiddenOperationException("Question cannot be modified when quiz is inactive.");
    }

    public Long getId() { return id; }
    public Quiz getQuiz() { return quiz; }
    public Integer getSequence() { return sequence; }
    public String getDescription() { return description; }
    public Integer getPoints() { return points; }
    public List<QuizAnswerOption> getOptions() { return List.copyOf(options); }
    public String getObservations() { return observations; }

    @Override
    public String toString() {
        return "QuizQuestion{" +
                "id=" + id +
                ", quizId=" + (quiz != null ? quiz.getId() : null) +
                ", sequence=" + sequence +
                ", description='" + description + '\'' +
                ", points=" + points +
                ", optionsCount=" + (options != null ? options.size() : 0) +
                ", observations='" + observations + '\'' +
                ", active=" + isActive() +
                ", deleted=" + isDeleted() +
                '}';
    }
}