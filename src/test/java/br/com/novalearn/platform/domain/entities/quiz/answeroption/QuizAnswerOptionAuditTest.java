package br.com.novalearn.platform.domain.entities.quiz.answeroption;

import br.com.novalearn.platform.domain.entities.quiz.Quiz;
import br.com.novalearn.platform.domain.entities.quiz.QuizAnswerOption;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static br.com.novalearn.platform.factories.entities.quiz.CreateOptionFactory.createInitializedOption;
import static br.com.novalearn.platform.factories.entities.quiz.CreateQuestionFactory.createInitializedQuestion;
import static br.com.novalearn.platform.factories.entities.quiz.CreateQuizFactory.createInitializedQuiz;
import static org.junit.jupiter.api.Assertions.*;

public class QuizAnswerOptionAuditTest {
    private Quiz quiz;
    private QuizAnswerOption option;

    @BeforeEach
    void setUp() {
        quiz = createInitializedQuiz();
        option = createInitializedOption(createInitializedQuestion(), LocalDateTime.now());
    }

    @Test
    void should_update_audit_fields() {
        quiz.activate();

        Long actor = 99L;
        LocalDateTime now = LocalDateTime.now();

        option.auditUpdate(actor, now);

        assertEquals(actor, option.getUpdatedBy());
        assertEquals(now, option.getUpdatedAt());
    }
}