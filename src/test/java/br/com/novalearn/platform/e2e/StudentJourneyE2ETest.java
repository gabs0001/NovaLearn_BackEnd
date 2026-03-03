package br.com.novalearn.platform.e2e;

import br.com.novalearn.platform.domain.entities.category.Category;
import br.com.novalearn.platform.domain.entities.course.Course;
import br.com.novalearn.platform.domain.entities.module.Module;
import br.com.novalearn.platform.domain.entities.quiz.Quiz;
import br.com.novalearn.platform.domain.entities.quiz.QuizAnswerOption;
import br.com.novalearn.platform.domain.entities.quiz.QuizQuestion;
import br.com.novalearn.platform.domain.entities.user.User;
import br.com.novalearn.platform.domain.entities.user.UserCourse;
import br.com.novalearn.platform.domain.entities.user.UserQuizAttempt;
import br.com.novalearn.platform.domain.enums.EnrollmentStatus;
import br.com.novalearn.platform.domain.enums.QuizAttemptStatus;
import br.com.novalearn.platform.domain.enums.UserRole;
import br.com.novalearn.platform.domain.repositories.category.CategoryRepository;
import br.com.novalearn.platform.domain.repositories.course.CourseRepository;
import br.com.novalearn.platform.domain.repositories.module.ModuleRepository;
import br.com.novalearn.platform.domain.repositories.quiz.QuizAnswerOptionRepository;
import br.com.novalearn.platform.domain.repositories.quiz.QuizQuestionRepository;
import br.com.novalearn.platform.domain.repositories.quiz.QuizRepository;
import br.com.novalearn.platform.domain.repositories.user.UserCourseRepository;
import br.com.novalearn.platform.domain.repositories.user.UserQuizAttemptRepository;
import br.com.novalearn.platform.domain.repositories.user.UserRepository;
import br.com.novalearn.platform.domain.valueobjects.Cpf;
import br.com.novalearn.platform.domain.valueobjects.Email;
import br.com.novalearn.platform.infra.time.provider.TimeProvider;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static br.com.novalearn.platform.factories.entities.category.CreateCategoryFactory.createInitializedCategory;
import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class StudentJourneyE2ETest {
    @Autowired
    private TimeProvider timeProvider;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private ModuleRepository moduleRepository;

    @Autowired
    private QuizRepository quizRepository;

    @Autowired
    private QuizQuestionRepository quizQuestionRepository;

    @Autowired
    private QuizAnswerOptionRepository quizAnswerOptionRepository;

    @Autowired
    private UserCourseRepository userCourseRepository;

    @Autowired
    private UserQuizAttemptRepository userQuizAttemptRepository;

    private static final String encodedPassword = "encoded-password";

    @Test
    void should_complete_full_student_journey_successfully() {
        LocalDateTime now = timeProvider.now();

        // ===========================
        // Create Instructor
        // ===========================
        User instructor = new User(
                "John",
                "Instructor",
                LocalDateTime.of(1996, 6, 12, 0 , 0),
                new Cpf("12345678901"),
                "+5511999999999",
                new Email("john.instructor@test.com"),
                UserRole.INSTRUCTOR,
                "en-US",
                null,
                null,
                null
        );

        instructor.initializeNewUser(encodedPassword, now);
        userRepository.save(instructor);

        // ===========================
        // Register Student
        // ===========================
        User student = User.register(
                "Alice",
                "Student",
                "alice.student@test.com",
                "98765432100",
                "en-US",
                "+5511888888888",
                null
        );

        student.initializeNewUser(encodedPassword, now);
        userRepository.save(student);

        // ===========================
        // Create Category
        // ===========================
        Category category = createInitializedCategory();
        category.auditCreate(1L, now);
        categoryRepository.save(category);

        // ===========================
        // Create Course
        // ===========================
        Course course = Course.create(
                "Spring Boot fundamentals",
                category,
                instructor
        );
        course.auditCreate(1L, now);
        courseRepository.save(course);

        // ===========================
        // Create Module
        // ===========================
        Module module = Module.create(
                course,
                "Introduction Module",
                "Getting started with Spring Boot",
                1,
                null
        );
        module.auditCreate(1L, now);
        moduleRepository.save(module);

        // ===========================
        // Create Quiz
        // ===========================
        Quiz quiz = Quiz.create(
                module,
                "Basics Quiz",
                1,
                BigDecimal.valueOf(70),
                3,
                false
        );
        quiz.auditCreate(1L, now);
        quizRepository.save(quiz);

        // ===========================
        // Create Question
        // ===========================
        QuizQuestion question = QuizQuestion.create(
                quiz,
                1,
                "What is Spring Boot?",
                100
        );
        question.auditCreate(1L, now);
        quizQuestionRepository.save(question);

        // ===========================
        // Create Answer Options
        // ===========================
        QuizAnswerOption correctOption =  QuizAnswerOption.create(
                question,
                1,
                "A Java framework for building applications",
                true,
                null,
                instructor.getId(),
                now
        );

        QuizAnswerOption wrongOption =  QuizAnswerOption.create(
                question,
                2,
                "A database engine",
                false,
                null,
                instructor.getId(),
                now
        );

        quizAnswerOptionRepository.save(correctOption);
        quizAnswerOptionRepository.save(wrongOption);

        // ==============================
        // Create Enrollment / UserCourse
        // ==============================
        UserCourse enrollment = UserCourse.enroll(student, course, now);
        enrollment.auditCreate(1L, now);
        userCourseRepository.save(enrollment);

        assertThat(enrollment.getEnrollmentStatus()).isEqualTo(EnrollmentStatus.ENROLLED);

        // ===========================
        // Progress to 50%
        // ===========================
        enrollment.updateProgress(50, now);
        assertThat(enrollment.getEnrollmentStatus()).isEqualTo(EnrollmentStatus.IN_PROGRESS);

        // ===========================
        // Complete Course
        // ===========================
        enrollment.updateProgress(100, now);
        assertThat(enrollment.getEnrollmentStatus()).isEqualTo(EnrollmentStatus.COMPLETED);

        // ===========================
        // Start Quiz Attempt
        // ===========================
        UserQuizAttempt attempt = UserQuizAttempt.start(
                student,
                quiz,
                1,
                student.getId(),
                now
        );
        userQuizAttemptRepository.save(attempt);

        // ===========================
        // Finish Quiz Attempt
        // ===========================
        attempt.finish(
                BigDecimal.valueOf(100),
                BigDecimal.valueOf(100),
                now,
                student.getId(),
                now
        );

        assertThat(attempt.getStatus()).isEqualTo(QuizAttemptStatus.FINISHED);
        assertThat(attempt.isPassed()).isTrue();
    }
}