package br.com.novalearn.platform.domain.entities.quiz.answeroption;

import br.com.novalearn.platform.domain.entities.quiz.Quiz;
import br.com.novalearn.platform.domain.entities.quiz.QuizAnswerOption;
import br.com.novalearn.platform.domain.entities.quiz.QuizQuestion;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static br.com.novalearn.platform.factories.entities.quiz.CreateQuizFactory.createInitializedQuiz;
import static org.junit.jupiter.api.Assertions.*;

public class QuizAnswerOptionBelongsTest {
    private Quiz quiz;

    @BeforeEach
    void setUp() {
        quiz = createInitializedQuiz();
    }

    @Test
    void should_return_true_when_belongs_to_question() {
        quiz.activate();

        QuizQuestion q1 = QuizQuestion.create(quiz,1, "Q1", 5);
        QuizQuestion q2 = QuizQuestion.create(quiz,2, "Q2", 5);

        QuizAnswerOption option = QuizAnswerOption.create(
                q1,
                1,
                "A",
                false,
                null,
                1L,
                LocalDateTime.now()
        );

        assertTrue(option.belongsTo(q1));
        assertFalse(option.belongsTo(q2));
    }

    @Test
    void should_return_false_when_null() {
        quiz.activate();

        QuizQuestion question = QuizQuestion.create(quiz, 1, "Q", 5);

        QuizAnswerOption option = QuizAnswerOption.create(
                question,
                1,
                "A",
                false,
                null,
                1L,
                LocalDateTime.now()
        );

        assertFalse(option.belongsTo(null));
    }
}