package br.com.novalearn.platform.domain.entities.quiz;

import br.com.novalearn.platform.domain.entities.module.Module;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static br.com.novalearn.platform.factories.entities.course.CreateCourseFactory.createInitializedCourse;
import static br.com.novalearn.platform.factories.entities.module.CreateModuleFactory.createInitializedModule;
import static org.junit.jupiter.api.Assertions.*;

public class QuizAuditUpdateTest {
    private Module module;

    @BeforeEach
    void setUp() {
        module = createInitializedModule(createInitializedCourse());
    }

    @Test
    void should_update_audit_fields() {
        Quiz quiz = Quiz.create(
                module,
                "Quiz",
                5,
                BigDecimal.TEN,
                3,
                false
        );

        quiz.activate();
        quiz.markAsNotDeleted();

        Long actorId = 10L;
        LocalDateTime now = LocalDateTime.now();

        quiz.auditUpdate(actorId, now);

        assertEquals(actorId, quiz.getUpdatedBy());
        assertEquals(now, quiz.getUpdatedAt());
    }
}
