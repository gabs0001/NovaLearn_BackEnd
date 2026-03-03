package br.com.novalearn.platform.domain.entities.quiz;

import br.com.novalearn.platform.core.exception.business.ValidationException;
import br.com.novalearn.platform.domain.entities.module.Module;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static br.com.novalearn.platform.factories.entities.course.CreateCourseFactory.createInitializedCourse;
import static br.com.novalearn.platform.factories.entities.module.CreateModuleFactory.createInitializedModule;
import static org.junit.jupiter.api.Assertions.*;

public class QuizCreateTest {
    private Module module;

    @BeforeEach
    void setUp() {
        module = createInitializedModule(createInitializedCourse());
    }

    @Test
    void should_create_quiz_successfully() {
        Quiz quiz = Quiz.create(
                module,
                "Java Basics Quiz",
                10,
                new BigDecimal("70"),
                3,
                true
        );

        assertNotNull(quiz);

        assertEquals(module, quiz.getModule());
        assertEquals("Java Basics Quiz", quiz.getName());
        assertEquals(10, quiz.getQtdQuestions());
        assertEquals(new BigDecimal("70"), quiz.getMinScore());
        assertEquals(3, quiz.getMaxAttempts());
        assertTrue(quiz.isRandomOrder());
    }

    @Test
    void should_throw_when_module_is_null() {
        ValidationException ex = assertThrows(
                ValidationException.class,
                () -> Quiz.create(
                        null,
                        "Quiz",
                        5,
                        BigDecimal.ZERO,
                        3,
                        false
                )
        );

        assertEquals("Module is required.", ex.getMessage());
    }

    @Test
    void should_throw_when_name_is_blank() {
        ValidationException ex = assertThrows(
                ValidationException.class,
                () -> Quiz.create(
                        module,
                        "   ",
                        5,
                        BigDecimal.ZERO,
                        3,
                        false
                )
        );

        assertEquals("Quiz name is required.", ex.getMessage());
    }

    @Test
    void should_throw_when_qtd_questions_invalid() {
        ValidationException ex = assertThrows(
                ValidationException.class,
                () -> Quiz.create(
                        module,
                        "Quiz",
                        0,
                        BigDecimal.ZERO,
                        3,
                        false
                )
        );

        assertEquals("Quiz must have at least one question.", ex.getMessage());
    }

    @Test
    void should_throw_when_min_score_negative() {
        ValidationException ex = assertThrows(
                ValidationException.class,
                () -> Quiz.create(
                        module,
                        "Quiz",
                        5,
                        new BigDecimal("-1"),
                        3,
                        false
                )
        );

        assertEquals(
                "Minimum score cannot be negative.",
                ex.getMessage()
        );
    }

    @Test
    void should_throw_when_max_attempts_invalid() {
        ValidationException ex = assertThrows(
                ValidationException.class,
                () -> Quiz.create(
                        module,
                        "Quiz",
                        5,
                        BigDecimal.ZERO,
                        0,
                        false
                )
        );

        assertEquals("Max attempts must be at least 1.", ex.getMessage());
    }
}