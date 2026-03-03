package br.com.novalearn.platform.domain.entities.quiz.question;

import br.com.novalearn.platform.core.exception.business.ValidationException;
import br.com.novalearn.platform.domain.entities.quiz.QuizAnswerOption;
import br.com.novalearn.platform.domain.entities.quiz.QuizQuestion;
import br.com.novalearn.platform.domain.entities.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;

import static br.com.novalearn.platform.factories.entities.quiz.CreateOptionFactory.createInitializedOption;
import static br.com.novalearn.platform.factories.entities.quiz.CreateQuestionFactory.createInitializedQuestion;
import static br.com.novalearn.platform.factories.entities.user.CreateUserFactory.createInitializedUser;
import static org.junit.jupiter.api.Assertions.*;

public class QuizQuestionValidationTest {
    private QuizQuestion question;

    @BeforeEach
    void setUp() {
        question = createInitializedQuestion();
    }

    private QuizAnswerOption option(boolean correct) {
        return QuizAnswerOption.create(
                question,
                1,
                "Option",
                correct,
                "Obs",
                5L,
                LocalDateTime.now()
        );
    }

    @Test
    void should_validate_when_has_correct_option() {
        QuizAnswerOption opt1 = option(false);
        QuizAnswerOption opt2 = option(true);

        ReflectionTestUtils.setField(opt1, "quizQuestion", null);
        ReflectionTestUtils.setField(opt2, "quizQuestion", null);

        question.addOption(opt1);
        question.addOption(opt2);

        assertDoesNotThrow(question::validateHasCorrectOption);
    }

    @Test
    void should_throw_when_no_correct_option() {
        QuizAnswerOption opt = option(false);

        ReflectionTestUtils.setField(opt, "quizQuestion", null);

        question.addOption(opt);

        ValidationException exception = assertThrows(
                ValidationException.class,
                question::validateHasCorrectOption
        );

        assertEquals("Question must have at least one correct answer.", exception.getMessage());
    }
}