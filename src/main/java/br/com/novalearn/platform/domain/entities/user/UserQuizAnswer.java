package br.com.novalearn.platform.domain.entities.user;

import br.com.novalearn.platform.core.exception.business.ValidationException;
import br.com.novalearn.platform.domain.converters.BooleanToYNConverter;
import br.com.novalearn.platform.domain.entities.base.BaseEntity;
import br.com.novalearn.platform.domain.entities.quiz.QuizAnswerOption;
import br.com.novalearn.platform.domain.entities.quiz.QuizQuestion;
import br.com.novalearn.platform.infra.database.sequences.DatabaseSequences;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(
        name = "T_SINL_USER_QUIZ_ANSWER",
        indexes = {
                @Index(name = "IDX_T_SINL_UQANSWER_USER", columnList = "cod_user"),
                @Index(name = "IDX_T_SINL_UQANSWER_QUESTION", columnList = "cod_quiz_question"),
                @Index(name = "IDX_T_SINL_UQANSWER_OPTION", columnList = "cod_quiz_answer_option")
        }
)
@SequenceGenerator(
        name = DatabaseSequences.USER_QUIZ_ANSWER_SEQ,
        sequenceName = DatabaseSequences.USER_QUIZ_ANSWER_SEQ,
        allocationSize = 1
)
public class UserQuizAnswer extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = DatabaseSequences.USER_QUIZ_ANSWER_SEQ)
    @Column(name = "cod_user_quiz_answer", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cod_user", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cod_quiz_question", nullable = false)
    private QuizQuestion quizQuestion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cod_quiz_answer_option", nullable = false)
    private QuizAnswerOption quizAnswerOption;

    @Column(name = "dat_answered")
    private LocalDateTime answeredAt;

    @Convert(converter = BooleanToYNConverter.class)
    @Column(name = "ind_correct", length = 1, nullable = false)
    private boolean correct;

    @Column(name = "obs_answer", length = 500)
    private String observations;

    protected UserQuizAnswer() {}

    public static UserQuizAnswer answer(
            User user,
            QuizQuestion question,
            QuizAnswerOption selectedOption,
            LocalDateTime answeredAt,
            LocalDateTime now
    ) {
        validateCreation(user, question, selectedOption, answeredAt);

        UserQuizAnswer answer = new UserQuizAnswer();

        answer.user = user;
        answer.quizQuestion = question;
        answer.quizAnswerOption = selectedOption;
        answer.answeredAt = answeredAt;
        answer.correct = selectedOption.isCorrect();

        answer.auditCreate(user.getId(), now);

        return answer;
    }

    private static void validateCreation(
            User user,
            QuizQuestion question,
            QuizAnswerOption option,
            LocalDateTime answeredAt
    ) {
        if(user == null) throw new ValidationException("User is required.");
        if(question == null) throw new ValidationException("Quiz question is required");
        if(option == null) throw new ValidationException("Quiz answer option is required");
        if(answeredAt == null) throw new ValidationException("Answered date is required");
        if(!option.belongsTo(question)) throw new ValidationException("Selected option does not belong to the given question");
    }

    public void changeAnswer(QuizAnswerOption newOption, LocalDateTime now) {
        ensureNotDeleted();

        if(newOption == null) throw new ValidationException("Answer option is required");
        if(!newOption.belongsTo(this.quizQuestion)) throw new ValidationException("Option does not belong to this question.");
        if(!quizQuestion.getQuiz().isActive()) throw new ValidationException("Quiz have to be active");

        this.quizAnswerOption = newOption;
        this.correct = newOption.isCorrect();
        this.answeredAt = Objects.requireNonNull(now, "Answered date is required");
    }

    public Long getId() { return id; }
    public User getUser() { return user; }
    public QuizQuestion getQuizQuestion() { return quizQuestion; }
    public QuizAnswerOption getQuizAnswerOption() { return quizAnswerOption; }
    public LocalDateTime getAnsweredAt() { return answeredAt; }
    public boolean isCorrect() { return correct; }
    public String getObservations() { return observations; }

    @Override
    public String toString() {
        return "UserQuizAnswer{" +
                "id=" + id +
                ", userId=" + (user != null ? user.getId() : null) +
                ", quizQuestionId=" + (quizQuestion != null ? quizQuestion.getId() : null) +
                ", quizAnswerOptionId=" + (quizAnswerOption != null ? quizAnswerOption.getId() : null) +
                ", correct=" + correct +
                ", answeredAt=" + answeredAt +
                ", active=" + isActive() +
                ", deleted=" + isDeleted() +
                '}';
    }
}