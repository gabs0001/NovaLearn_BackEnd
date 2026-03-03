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

public class UserQuizAnswerChangeTest {
    private static class TestData {
        User user;
        QuizQuestion question;
        QuizAnswerOption correct;
        QuizAnswerOption wrong;
        UserQuizAnswer answer;
    }

    private LocalDateTime now;
    private TestData d;
    private Quiz quiz;

    @BeforeEach
    void setUp() {
        now = LocalDateTime.now();
        quiz = createInitializedQuiz();
        d = setup();
    }

    private TestData setup() {
        TestData d = new TestData();

        d.user = createInitializedUser();

        ReflectionTestUtils.setField(d.user, "id", 5L);

        d.question = QuizQuestion.create(quiz, 1, "Q", 5);

        d.correct = QuizAnswerOption.create(
                d.question,
                1,
                "A",
                true,
                null,
                5L,
                now
        );

        d.wrong = QuizAnswerOption.create(
                d.question,
                2,
                "B",
                false,
                null,
                5L,
                now
        );

        d.answer = UserQuizAnswer.answer(
                d.user,
                d.question,
                d.wrong,
                now,
                now
        );

        return d;
    }

    @Test
    void should_change_answer_to_correct_option() {
        d.answer.changeAnswer(d.correct, now);

        assertEquals(d.correct, d.answer.getQuizAnswerOption());
        assertTrue(d.answer.isCorrect());
        assertEquals(now, d.answer.getAnsweredAt());
    }

    @Test
    void should_throw_when_new_option_null() {
        ValidationException ex = assertThrows(
                ValidationException.class,
                () -> d.answer.changeAnswer(null, now)
        );

        assertEquals("Answer option is required", ex.getMessage());
    }

    @Test
    void should_throw_when_option_from_other_question() {
        QuizQuestion other = QuizQuestion.create(quiz,2, "Q2", 5);

        QuizAnswerOption foreign = QuizAnswerOption.create(
                other,
                1,
                "X",
                true,
                null,
                5L,
                now
        );

        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> d.answer.changeAnswer(foreign, now)
        );

        assertEquals("Option does not belong to this question.", exception.getMessage());
    }

    @Test
    void should_throw_when_now_null() {
        NullPointerException exception = assertThrows(
                NullPointerException.class,
                () -> d.answer.changeAnswer(d.correct, null)
        );

        assertEquals("Answered date is required", exception.getMessage());
    }
}