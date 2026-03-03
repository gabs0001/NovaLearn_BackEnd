package br.com.novalearn.platform.domain.entities.quiz.question;

import br.com.novalearn.platform.core.exception.business.ValidationException;
import br.com.novalearn.platform.domain.entities.quiz.Quiz;
import br.com.novalearn.platform.domain.entities.quiz.QuizQuestion;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static br.com.novalearn.platform.factories.entities.quiz.CreateQuizFactory.createInitializedQuiz;
import static org.junit.jupiter.api.Assertions.*;

public class QuizQuestionCreateTest {
    private Quiz quiz;

    @BeforeEach
    void setUp() {
        quiz = createInitializedQuiz();
    }

    @Test
    void should_create_question_successfully() {
        QuizQuestion question = QuizQuestion.create(
                quiz,
                1,
                "What is Java?",
                10
        );

        assertNotNull(question);

        assertEquals(quiz, question.getQuiz());
        assertEquals(1, question.getSequence());
        assertEquals("What is Java?", question.getDescription());
        assertEquals(10, question.getPoints());
    }

    @Test
    void should_throw_when_quiz_is_null() {
        ValidationException ex = assertThrows(
                ValidationException.class,
                () -> QuizQuestion.create(
                        null,
                        1,
                        "Question",
                        5
                )
        );

        assertEquals("Quiz is required.", ex.getMessage());
    }

    @Test
    void should_throw_when_sequence_invalid() {

        ValidationException ex = assertThrows(
                ValidationException.class,
                () -> QuizQuestion.create(
                        quiz,
                        0,
                        "Question",
                        5
                )
        );

        assertEquals("Question sequence must be at least 1.", ex.getMessage());
    }

    @Test
    void should_throw_when_description_blank() {
        ValidationException ex = assertThrows(
                ValidationException.class,
                () -> QuizQuestion.create(
                        quiz,
                        1,
                        "   ",
                        5
                )
        );

        assertEquals("Question description is required.", ex.getMessage());
    }

    @Test
    void should_throw_when_points_invalid() {
        ValidationException ex = assertThrows(
                ValidationException.class,
                () -> QuizQuestion.create(
                        quiz,
                        1,
                        "Question",
                        0
                )
        );

        assertEquals("Question points must be at least 1.", ex.getMessage());
    }
}