package br.com.novalearn.platform.e2e;

import br.com.novalearn.platform.api.dtos.review.ReviewCreateRequestDTO;
import br.com.novalearn.platform.api.dtos.review.ReviewResponseDTO;
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
import br.com.novalearn.platform.domain.valueobjects.Email;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class ReviewE2ETest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private UserCourseRepository userCourseRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private JWTUtil jwtUtil;

    private String studentToken;
    private String adminToken;

    private User student;
    private User admin;
    private Course course;

    @BeforeEach
    void setup() {
        student = User.register(
                "John",
                "Student",
                "john.student@email.com",
                "12345678901",
                "en-US",
                "11999999999",
                null
        );
        student.initializeNewUser("encoded-password", LocalDateTime.now());
        userRepository.save(student);

        admin = new User(
                "Admin",
                "User",
                null,
                null,
                "11888888888",
                new Email("admin@email.com"),
                UserRole.ADMIN,
                "en-US",
                null,
                null,
                null
        );
        admin.initializeNewUser("encoded-password", LocalDateTime.now());
        userRepository.save(admin);

        studentToken = "Bearer " + jwtUtil.generateAccessToken(student);
        adminToken = "Bearer " + jwtUtil.generateAccessToken(admin);

        Category category = new Category();
        category.setName("Programming");
        category.auditCreate(1L, LocalDateTime.now());
        category = categoryRepository.save(category);

        course = Course.create(
                "Spring Boot Advanced",
                category,
                admin
        );
        course.auditCreate(1L, LocalDateTime.now());
        courseRepository.save(course);

        UserCourse enrollment = UserCourse.enroll(student, course, LocalDateTime.now());
        enrollment.updateProgress(100, LocalDateTime.now());
        enrollment.auditCreate(1L, LocalDateTime.now());
        userCourseRepository.save(enrollment);
    }

    @Test
    void should_execute_complete_review_workflow_with_approval_and_rating_impact() throws Exception {
        // ===== STUDENT CREATES REVIEW =====
        ReviewCreateRequestDTO request = new ReviewCreateRequestDTO();
        request.setCourseId(course.getId());
        request.setRating(5);
        request.setComment("Excellent course!");
        request.setAnonymous(false);

        MvcResult result = mockMvc.perform(post("/api/reviews")
                        .header(HttpHeaders.AUTHORIZATION, studentToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andReturn();

        ReviewResponseDTO created =
                objectMapper.readValue(result.getResponse().getContentAsString(), ReviewResponseDTO.class);

        Review review = reviewRepository.findById(created.getId()).orElseThrow();

        assertEquals(ReviewStatus.PENDING, review.getStatus());
        assertEquals(0, course.getNumRatingCount() == null ? 0 : course.getNumRatingCount());

        // ===== ADMIN APPROVES =====
        mockMvc.perform(patch("/api/reviews/" + review.getId() + "/approve")
                        .header(HttpHeaders.AUTHORIZATION, adminToken))
                .andExpect(status().isNoContent());

        Review approved = reviewRepository.findById(review.getId()).orElseThrow();
        Course updatedCourse = courseRepository.findById(course.getId()).orElseThrow();

        assertEquals(ReviewStatus.APPROVED, approved.getStatus());
        assertNotNull(approved.getPublishedAt());

        assertEquals(1, updatedCourse.getNumRatingCount());
        assertEquals(5, updatedCourse.getNumRatingTotal());
    }

    @Test
    void should_not_allow_duplicate_review_for_same_course() throws Exception {
        Review review = Review.create(
                student,
                course,
                4,
                "Very good",
                false,
                null,
                LocalDateTime.now()
        );
        reviewRepository.save(review);

        ReviewCreateRequestDTO request = new ReviewCreateRequestDTO();
        request.setCourseId(course.getId());
        request.setRating(5);
        request.setComment("Trying duplicate");
        request.setAnonymous(false);

        mockMvc.perform(post("/api/reviews")
                        .header(HttpHeaders.AUTHORIZATION, studentToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());
    }

    @Test
    void should_not_allow_review_without_enrollment() throws Exception {

        User otherStudent = User.register(
                "Mary",
                "NoEnrollment",
                "mary@email.com",
                "98765432100",
                "en-US",
                "11777777777",
                null
        );
        otherStudent.initializeNewUser("encoded-password", LocalDateTime.now());
        userRepository.save(otherStudent);

        String token = "Bearer " + jwtUtil.generateAccessToken(otherStudent);

        ReviewCreateRequestDTO request = new ReviewCreateRequestDTO();
        request.setCourseId(course.getId());
        request.setRating(3);
        request.setComment("Should fail");
        request.setAnonymous(false);

        mockMvc.perform(post("/api/reviews")
                        .header(HttpHeaders.AUTHORIZATION, token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    void should_not_approve_rejected_review() throws Exception {
        Review review = Review.create(
                student,
                course,
                4,
                "Good course",
                false,
                null,
                LocalDateTime.now()
        );
        reviewRepository.save(review);

        mockMvc.perform(patch("/api/reviews/" + review.getId() + "/reject")
                        .header(HttpHeaders.AUTHORIZATION, adminToken))
                .andExpect(status().isNoContent());

        mockMvc.perform(patch("/api/reviews/" + review.getId() + "/approve")
                        .header(HttpHeaders.AUTHORIZATION, adminToken))
                .andExpect(status().isBadRequest());
    }

    @Test
    void rejected_review_should_not_affect_course_rating() throws Exception {
        Review review = Review.create(
                student,
                course,
                2,
                "Bad course",
                false,
                null,
                LocalDateTime.now()
        );
        reviewRepository.save(review);

        mockMvc.perform(patch("/api/reviews/" + review.getId() + "/reject")
                        .header(HttpHeaders.AUTHORIZATION, adminToken))
                .andExpect(status().isNoContent());

        Course updatedCourse = courseRepository.findById(course.getId()).orElseThrow();

        assertNull(updatedCourse.getNumRatingCount());
        assertNull(updatedCourse.getNumRatingTotal());
    }
}