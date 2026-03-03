package br.com.novalearn.platform.e2e;

import br.com.novalearn.platform.domain.entities.category.Category;
import br.com.novalearn.platform.domain.entities.course.Course;
import br.com.novalearn.platform.domain.entities.quiz.Quiz;
import br.com.novalearn.platform.domain.entities.quiz.QuizAnswerOption;
import br.com.novalearn.platform.domain.entities.quiz.QuizQuestion;
import br.com.novalearn.platform.domain.entities.user.User;
import br.com.novalearn.platform.domain.entities.module.Module;
import br.com.novalearn.platform.domain.enums.CourseStatus;
import br.com.novalearn.platform.domain.enums.UserRole;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static br.com.novalearn.platform.factories.entities.category.CreateCategoryFactory.createInitializedCategory;
import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
public class AdminAcademicStructureFlowE2ETest {
    @Test
    void should_create_full_academic_structure_and_publish_course_successfully() {
        //arrange
        LocalDateTime now = LocalDateTime.now();

        //create category
        Category category = createInitializedCategory();

        assertTrue(category.isActive());
        assertFalse(category.isDeleted());

        //register instructor
        User instructor = User.register(
                "John",
                "Doe",
                "john.doe@email.com",
                "12345678901",
                "en-US",
                "11999999999",
                null
        );

        instructor.initializeNewUser("encoded-password", now);

        assertTrue(instructor.isActive());
        assertEquals(UserRole.STUDENT, instructor.getRole());

        instructor.changeRole(UserRole.INSTRUCTOR);

        //create course
        Course course = Course.create(
                "Clean Architecture Mastery",
                category,
                instructor
        );

        assertEquals(CourseStatus.DRAFT, course.getStatus());
        assertTrue(course.isActive());

        //--------

        //update course info + slug
        course.updateInfo(
                "Clean Architecture Mastery",
                "Learn clean architecture principles",
                "Complete guide to building scalable systems",
                "Internal notes"
        );

        course.defineSlug("clean-architecture-mastery");
        course.registerLesson();
        course.definePricing(true, new BigDecimal("199.90"));

        assertTrue(course.isPaid());
        assertEquals(new BigDecimal("199.90"), course.getPrice());

        //create module
        Module module = Module.create(
                course,
                "Architecture Fundamentals",
                "Core architectural principles",
                1,
                null
        );

        assertTrue(module.isActive());
        assertEquals(1, module.getSequence());

        //create quiz
        Quiz quiz = Quiz.create(
                module,
                "Fundamentals Quiz",
                1,
                new BigDecimal("7.0"),
                3,
                false
        );

        assertTrue(quiz.isActive());

        //create question
        QuizQuestion question = QuizQuestion.create(
                quiz,
                1,
                "What is the Dependency Rule?",
                10
        );

        assertEquals(1, question.getSequence());

        //create answer options (with audit)
        QuizAnswerOption option1 = QuizAnswerOption.create(
                question,
                1,
                "Dependencies must point inward",
                true,
                null,
                1L,
                now
        );

        QuizAnswerOption option2 = QuizAnswerOption.create(
                question,
                2,
                "Dependencies must point outward",
                false,
                null,
                1L,
                now
        );

        assertTrue(option1.isActive());
        assertFalse(option1.isDeleted());
        assertEquals(1L, option1.getCreatedBy());

        //publish course
        course.publish(now);

        // Assert final state
        assertEquals(CourseStatus.PUBLISHED, course.getStatus());
        assertEquals(now, course.getPublishedAt());

        // Structural Integrity Assertions
        assertNotNull(course.getCategory());
        assertNotNull(course.getInstructor());
        assertEquals("clean-architecture-mastery", course.getSlug());
        assertEquals(1, module.getSequence());
        assertEquals(1, question.getSequence());
        assertTrue(option1.isCorrect());
        assertFalse(option2.isCorrect());
    }
}
