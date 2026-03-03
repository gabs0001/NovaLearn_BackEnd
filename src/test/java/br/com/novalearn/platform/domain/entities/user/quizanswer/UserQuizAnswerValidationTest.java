package br.com.novalearn.platform.domain.entities.user.quizanswer;

import br.com.novalearn.platform.core.exception.business.ValidationException;
import br.com.novalearn.platform.domain.entities.quiz.Quiz;
import br.com.novalearn.platform.domain.entities.quiz.QuizAnswerOption;
import br.com.novalearn.platform.domain.entities.quiz.QuizQuestion;
import br.com.novalearn.platform.domain.entities.user.User;
import br.com.novalearn.platform.domain.entities.user.UserQuizAnswer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;

import static br.com.novalearn.platform.factories.entities.quiz.CreateQuizFactory.createInitializedQuiz;
import static br.com.novalearn.platform.factories.entities.user.CreateUserFactory.createInitializedUser;
import static org.junit.jupiter.api.Assertions.*;

public class UserQuizAnswerValidationTest {
    private LocalDateTime now;
    private User user;
    private Quiz quiz;
    private QuizQuestion question;

    @BeforeEach
    void setUp() {
        now = LocalDateTime.now();
        user = createInitializedUser();
        quiz = createInitializedQuiz();
        question = createInitializedQuestion();
    }

    private QuizQuestion createInitializedQuestion() {
        QuizQuestion question = QuizQuestion.create(
                quiz,
                1,
                "Question",
                5
        );

        QuizAnswerOption option = QuizAnswerOption.create(
                question,
                1,
                "A",
                true,
                null,
                5L,
                now
        );

        ReflectionTestUtils.setField(option, "quizQuestion", null);

        question.addOption(option);

        return question;
    }

    @Test
    void should_throw_when_user_null() {
        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> UserQuizAnswer.answer(
                        null,
                        question,
                        question.getOptions().getFirst(),
                        now,
                        now
                )
        );

        assertEquals("User is required.", exception.getMessage());
    }

    @Test
    void should_throw_when_question_null() {
        ValidationException ex = assertThrows(
                ValidationException.class,
                () -> UserQuizAnswer.answer(
                        user,
                        null,
                        question.getOptions().getFirst(),
                        now,
                        now
                )
        );

        assertEquals("Quiz question is required", ex.getMessage());
    }

    @Test
    void should_throw_when_option_null() {
        ValidationException ex = assertThrows(
                ValidationException.class,
                () -> UserQuizAnswer.answer(
                        user,
                        question,
                        null,
                        now,
                        now
                )
        );

        assertEquals("Quiz answer option is required", ex.getMessage());
    }

    @Test
    void should_throw_when_answeredAt_null() {
        ValidationException ex = assertThrows(
                ValidationException.class,
                () -> UserQuizAnswer.answer(
                        user,
                        question,
                        question.getOptions().getFirst(),
                        null,
                        now
                )
        );

        assertEquals("Answered date is required", ex.getMessage());
    }

    @Test
    void should_throw_when_option_does_not_belong_to_question() {
        QuizQuestion q1 = QuizQuestion.create(quiz, 1, "Q1", 5);
        QuizQuestion q2 = QuizQuestion.create(quiz, 2, "Q2", 5);

        QuizAnswerOption option = QuizAnswerOption.create(
                q2,
                1,
                "A",
                true,
                null,
                5L,
                now
        );

        ValidationException ex = assertThrows(
                ValidationException.class,
                () -> UserQuizAnswer.answer(
                        user,
                        q1,
                        option,
                        now,
                        now
                )
        );

        assertEquals("Selected option does not belong to the given question", ex.getMessage());
    }
}