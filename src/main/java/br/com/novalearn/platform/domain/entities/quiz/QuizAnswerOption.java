package br.com.novalearn.platform.domain.entities.quiz;

import br.com.novalearn.platform.core.exception.business.ValidationException;
import br.com.novalearn.platform.domain.converters.BooleanToYNConverter;
import br.com.novalearn.platform.domain.entities.base.BaseEntity;
import br.com.novalearn.platform.infra.database.sequences.DatabaseSequences;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "T_SINL_QUIZ_ANSWER_OPTION",
        indexes = {
                @Index(name = "IDX_T_SINL_QANSOPTION_QUESTION", columnList = "cod_quiz_question")
        }
)
@SequenceGenerator(
        name = DatabaseSequences.QUIZ_ANSWER_OPTION_SEQ,
        sequenceName = DatabaseSequences.QUIZ_ANSWER_OPTION_SEQ,
        allocationSize = 1
)
public class QuizAnswerOption extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = DatabaseSequences.QUIZ_ANSWER_OPTION_SEQ)
    @Column(name = "cod_quiz_answer_option", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cod_quiz_question", nullable = false)
    private QuizQuestion quizQuestion;

    @Column(name = "seq_option")
    private Integer sequence;

    @Lob
    @Column(name = "txt_option", nullable = false)
    private String optionText;

    @Convert(converter = BooleanToYNConverter.class)
    @Column(name = "ind_correct", length = 1, nullable = false)
    private boolean correct;

    @Column(name = "obs_option", length = 500)
    private String observations;

    protected QuizAnswerOption() {}

    public static QuizAnswerOption create(
            QuizQuestion question,
            Integer sequence,
            String optionText,
            boolean correct,
            String observations,
            Long actorId,
            LocalDateTime now
    ) {
        if(question == null) throw new ValidationException("QuizQuestion is required.");

        if(sequence == null || sequence < 1) throw new ValidationException("Sequence must be >= 1.");

        if(optionText == null || optionText.isBlank()) throw new ValidationException("Option text cannot be empty.");

        QuizAnswerOption option = new QuizAnswerOption();
        option.quizQuestion = question;
        option.sequence = sequence;
        option.optionText = optionText.trim();
        option.correct = correct;
        option.observations = observations;

        option.auditCreate(actorId, now);

        return option;
    }

    public void changeSequence(Integer newSequence) {
        if(newSequence == null || newSequence < 1) throw new ValidationException("Sequence must be >= 1.");
        this.sequence = newSequence;
    }

    public void changeText(String newText) {
        if(newText == null || newText.isBlank()) throw new ValidationException("Option text cannot be empty.");
        this.optionText = newText.trim();
    }

    public void changeCorrect(boolean correct) {
        this.correct = correct;
    }

    public void changeObservations(String observations) {
        this.observations = observations;
    }

    public boolean belongsTo(QuizQuestion question) {
        if(quizQuestion == null || question == null) return false;
        return quizQuestion.equals(question);
    }

    public void auditUpdate(Long actorId, LocalDateTime now) {
        super.auditUpdate(actorId, now);
    }

    public void attachToQuestion(QuizQuestion question) {
        if(question == null) throw new ValidationException("QuizQuestion cannot be null.");

        if(this.quizQuestion != null) throw new ValidationException("QuizAnswerOption is already attached to a QuizQuestion.");

        this.quizQuestion = question;
    }

    public Long getId() { return id; }
    public QuizQuestion getQuizQuestion() { return quizQuestion; }
    public Integer getSequence() { return sequence; }
    public String getOptionText() { return optionText; }
    public boolean isCorrect() { return correct; }
    public String getObservations() { return observations; }

    @Override
    public String toString() {
        return "QuizAnswerOption{" +
                "id=" + id +
                ", quizQuestionId=" + (quizQuestion != null ? quizQuestion.getId() : null) +
                ", sequence=" + sequence +
                ", correct=" + correct +
                ", active=" + isActive() +
                ", deleted=" + isDeleted() +
                '}';
    }
}