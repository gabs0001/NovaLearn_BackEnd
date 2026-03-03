package br.com.novalearn.platform.domain.entities.user.quizanswer;

import br.com.novalearn.platform.domain.entities.quiz.QuizAnswerOption;
import br.com.novalearn.platform.domain.entities.quiz.QuizQuestion;
import br.com.novalearn.platform.domain.entities.user.User;
import br.com.novalearn.platform.domain.entities.user.UserQuizAnswer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;

import static br.com.novalearn.platform.factories.entities.quiz.CreateOptionFactory.createInitializedOption;
import static br.com.novalearn.platform.factories.entities.quiz.CreateQuestionFactory.createInitializedQuestion;
import static br.com.novalearn.platform.factories.entities.user.CreateUserFactory.createInitializedUser;
import static org.junit.jupiter.api.Assertions.*;

public class UserQuizAnswerAuditTest {
    private LocalDateTime now;
    private User user;
    private QuizQuestion question;
    private QuizAnswerOption option;

    @BeforeEach
    void setUp() {
        now = LocalDateTime.now();
        user = createInitializedUser();
        ReflectionTestUtils.setField(user, "id", 5L);
        question = createInitializedQuestion();
        option = createInitializedOption(question, now);
    }

    @Test
    void should_update_audit_fields() {
        Long actor = 5L;

        UserQuizAnswer answer = UserQuizAnswer.answer(
                user,
                question,
                option,
                now,
                now
        );

        answer.auditUpdate(actor, now);

        assertEquals(actor, answer.getUpdatedBy());
        assertEquals(now, answer.getUpdatedAt());
    }
}