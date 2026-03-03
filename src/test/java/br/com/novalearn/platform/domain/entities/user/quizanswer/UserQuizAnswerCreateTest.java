package br.com.novalearn.platform.domain.entities.user.quizanswer;

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

public class UserQuizAnswerCreateTest {
    private LocalDateTime now;
    private User user;
    private Quiz quiz;
    private QuizQuestion question;

    @BeforeEach
    void setUp() {
        now = LocalDateTime.now();
        user = createInitializedUser();
        ReflectionTestUtils.setField(user, "id", 5L);
        quiz = createInitializedQuiz();
        question = createQuestionWithOptions();
    }

    private QuizQuestion createQuestionWithOptions() {
        QuizQuestion question = QuizQuestion.create(
                quiz,
                1,
                "Question",
                5
        );

        QuizAnswerOption optA = QuizAnswerOption.create(
                question,
                1,
                "A",
                false,
                null,
                5L,
                now
        );

        QuizAnswerOption optB = QuizAnswerOption.create(
                question,
                2,
                "B",
                true,
                null,
                5L,
                now
        );

        ReflectionTestUtils.setField(optA, "quizQuestion", null);
        ReflectionTestUtils.setField(optB, "quizQuestion", null);

        question.addOption(optA);
        question.addOption(optB);

        return question;
    }

    @Test
    void should_create_answer_successfully() {
        QuizAnswerOption correctOption =
                question.getOptions()
                        .stream()
                        .filter(QuizAnswerOption::isCorrect)
                        .findFirst()
                        .orElseThrow();

        LocalDateTime answeredAt = now;
        Long actor = 5L;

        UserQuizAnswer answer = UserQuizAnswer.answer(
                user,
                question,
                correctOption,
                answeredAt,
                now
        );

        assertNotNull(answer);

        assertEquals(user, answer.getUser());
        assertEquals(question, answer.getQuizQuestion());
        assertEquals(correctOption, answer.getQuizAnswerOption());

        assertEquals(answeredAt, answer.getAnsweredAt());
        assertTrue(answer.isCorrect());

        assertEquals(actor, answer.getCreatedBy());
        assertEquals(now, answer.getCreatedAt());
    }

    @Test
    void should_mark_as_incorrect_when_option_is_wrong() {
        QuizAnswerOption wrongOption =
                question.getOptions()
                        .stream()
                        .filter(o -> !o.isCorrect())
                        .findFirst()
                        .orElseThrow();

        UserQuizAnswer answer = UserQuizAnswer.answer(
                user,
                question,
                wrongOption,
                now,
                now
        );

        assertFalse(answer.isCorrect());
    }
}