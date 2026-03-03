package br.com.novalearn.platform.e2e;

import br.com.novalearn.platform.core.security.JWTUtil;
import br.com.novalearn.platform.domain.entities.category.Category;
import br.com.novalearn.platform.domain.entities.course.Course;
import br.com.novalearn.platform.domain.entities.review.Review;
import br.com.novalearn.platform.domain.entities.user.User;
import br.com.novalearn.platform.domain.entities.user.UserCourse;
import br.com.novalearn.platform.domain.enums.ReviewStatus;
import br.com.novalearn.platform.domain.enums.UserRole;
import br.com.novalearn.platform.domain.repositories.category.CategoryRepository;
import br.com.novalearn.platform.domain.repositories.course.CourseRepository;
import br.com.novalearn.platform.domain.repositories.review.ReviewRepository;
import br.com.novalearn.platform.domain.repositories.user.UserCourseRepository;
import br.com.novalearn.platform.domain.repositories.user.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import org.junit.jupiter.api.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static br.com.novalearn.platform.factories.entities.category.CreateCategoryFactory.createInitializedCategory;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class SecurityAccessControlE2ETest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JWTUtil jwtUtil;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private UserCourseRepository userCourseRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private String studentToken;
    private String adminToken;

    private User student;
    private User admin;
    private Course course;
    private Review review;

    @BeforeEach
    void setup() {
        LocalDateTime now = LocalDateTime.now();

        // Category
        Category category = createInitializedCategory();
        category.auditCreate(1L, now);
        categoryRepository.save(category);

        // Student
        student = User.register(
                "John",
                "Student",
                "student@test.com",
                null,
                "en-US",
                null,
                null
        );
        student.initializeNewUser(passwordEncoder.encode("123456"), now);
        student.auditCreate(1L, now);
        userRepository.save(student);

        // Admin
        admin = User.register(
                "Jane",
                "Admin",
                "admin@test.com",
                null,
                "en-US",
                null,
                null
        );
        admin.initializeNewUser(passwordEncoder.encode("123456"), now);
        admin.changeRole(UserRole.ADMIN);
        admin.auditCreate(1L, now);
        userRepository.save(admin);

        // Course
        course = Course.create("Security Course", category, admin);
        course.updateInfo("Security Course", "Short", "Long", null);
        course.definePricing(false, null);
        course.registerLesson();
        course.publish(now);
        course.auditCreate(admin.getId(), now);
        courseRepository.save(course);

        // Enrollment
        UserCourse enrollment = UserCourse.enroll(student, course, now);
        enrollment.auditCreate(student.getId(), now);
        userCourseRepository.save(enrollment);

        // Review
        review = Review.create(
                student,
                course,
                5,
                "Great course!",
                false,
                null,
                now
        );
        review.auditCreate(student.getId(), now);
        reviewRepository.save(review);

        studentToken = jwtUtil.generateAccessToken(student);
        adminToken = jwtUtil.generateAccessToken(admin);
    }

    @Test
    void should_return_401_when_access_protected_endpoint_without_token() throws Exception {
        mockMvc.perform(post("/api/reviews")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                            "courseId": %d,
                            "rating": 5,
                            "comment": "Excellent"
                        }
                        """.formatted(course.getId())))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void should_return_401_when_token_is_invalid() throws Exception {
        mockMvc.perform(get("/api/reviews")
                .header(HttpHeaders.AUTHORIZATION, "Bearer invalid-token"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void student_should_not_approve_review() throws Exception {
        mockMvc.perform(patch("/api/reviews/{id}/approve", review.getId())
                        .header("Authorization", "Bearer " + studentToken))
                .andExpect(status().isForbidden());
    }

    @Test
    void admin_should_approveReview() throws Exception {
        mockMvc.perform(patch("/api/reviews/{id}/approve", review.getId())
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isNoContent());

        Review updated = reviewRepository.findById(review.getId()).orElseThrow();

        assertEquals(ReviewStatus.APPROVED, updated.getStatus());
        assertNotNull(updated.getPublishedAt());

        // validate registerRating impact
        Course updatedCourse = courseRepository.findById(course.getId()).orElseThrow();
        assertEquals(1, updatedCourse.getNumRatingCount());
        assertEquals(5, updatedCourse.getNumRatingTotal());
    }

    @Test
    void should_allow_public_get_courses_without_token() throws Exception {
        mockMvc.perform(get("/api/courses"))
                .andExpect(status().isOk());
    }

    @Test
    void should_not_authenticate_inactive_user() throws Exception {
        student.deactivate();
        userRepository.save(student);

        String token = jwtUtil.generateAccessToken(student);

        mockMvc.perform(get("/api/reviews")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isUnauthorized());
    }
}