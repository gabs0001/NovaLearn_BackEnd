package br.com.novalearn.platform.domain.entities.quiz.question;

import br.com.novalearn.platform.core.exception.business.ForbiddenOperationException;
import br.com.novalearn.platform.domain.entities.quiz.QuizQuestion;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static br.com.novalearn.platform.factories.entities.quiz.CreateQuestionFactory.createInitializedQuestion;
import static br.com.novalearn.platform.factories.entities.quiz.CreateQuizFactory.createInitializedQuiz;
import static org.junit.jupiter.api.Assertions.*;

public class QuizQuestionProtectionTest {
    private QuizQuestion question;

    @BeforeEach
    void setUp() {
        question = createInitializedQuestion();
    }

    @Test
    void should_throw_when_deleted() {
        question.markAsDeleted();

        ForbiddenOperationException exception = assertThrows(
                ForbiddenOperationException.class,
                () -> question.changePoints(10)
        );

        assertEquals("Deleted question cannot be modified.", exception.getMessage());
    }
}