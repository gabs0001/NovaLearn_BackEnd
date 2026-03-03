package br.com.novalearn.platform.e2e;

import br.com.novalearn.platform.api.events.handlers.CertificateIssuedEventHandler;
import br.com.novalearn.platform.api.events.handlers.CourseCompletedEventHandler;
import br.com.novalearn.platform.api.events.handlers.EnrollmentCancelledEventHandler;
import br.com.novalearn.platform.api.events.handlers.UserRegisteredEventHandler;
import br.com.novalearn.platform.core.security.JWTUtil;
import br.com.novalearn.platform.domain.entities.category.Category;
import br.com.novalearn.platform.domain.entities.course.Course;
import br.com.novalearn.platform.domain.entities.user.User;
import br.com.novalearn.platform.domain.entities.user.UserCourse;
import br.com.novalearn.platform.domain.events.certificate.CertificateIssuedEvent;
import br.com.novalearn.platform.domain.events.course.CourseCompletedEvent;
import br.com.novalearn.platform.domain.events.enrollment.EnrollmentCancelledEvent;
import br.com.novalearn.platform.domain.events.user.UserRegisteredEvent;
import br.com.novalearn.platform.domain.repositories.category.CategoryRepository;
import br.com.novalearn.platform.domain.repositories.course.CourseRepository;
import br.com.novalearn.platform.domain.repositories.user.UserCourseRepository;
import br.com.novalearn.platform.domain.repositories.user.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.test.web.servlet.MockMvc;

import org.springframework.http.MediaType;
import org.junit.jupiter.api.*;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.LocalDateTime;

import static br.com.novalearn.platform.factories.entities.category.CreateCategoryFactory.createInitializedCategory;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional(propagation = Propagation.NOT_SUPPORTED)
public class EnrollmentEventConsistencyE2ETest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JWTUtil jwtUtil;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private UserCourseRepository userCourseRepository;

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @Autowired
    private PlatformTransactionManager transactionManager;

    @MockitoSpyBean
    private UserRegisteredEventHandler userRegisteredHandler;

    @MockitoSpyBean
    private CourseCompletedEventHandler courseCompletedHandler;

    @MockitoSpyBean
    private CertificateIssuedEventHandler certificateIssuedHandler;

    @MockitoSpyBean
    private EnrollmentCancelledEventHandler enrollmentCancelledHandler;

    private String studentToken;
    private Course course;

    @BeforeEach
    void setup() {
        Category category = createInitializedCategory();

        category.auditCreate(1L, LocalDateTime.now());
        category = categoryRepository.save(category);

        User student = User.register(
                "John",
                "Doe",
                "john@test.com",
                "12345678900",
                "pt_BR",
                "11999999999",
                null
        );

        student.activate();
        student.initializeNewUser("encoded-password", LocalDateTime.now());

        student = userRepository.save(student);

        course = Course.create(
                "Test Course",
                category,
                student
        );

        course.auditCreate(1L, LocalDateTime.now());
        course = courseRepository.save(course);

        UserCourse enrollment = UserCourse.enroll(student, course, LocalDateTime.now());

        enrollment.auditCreate(1L, LocalDateTime.now());
        userCourseRepository.save(enrollment);

        studentToken = jwtUtil.generateAccessToken(student);
    }

    @AfterEach
    void tearDown() {
        userCourseRepository.deleteAll();
        courseRepository.deleteAll();
        userRepository.deleteAll();
        categoryRepository.deleteAll();
    }

    //=========================
    // User Registered Event
    //=========================
    @Test
    void should_publish_user_registered_even_after_commit() {
        // Arrange
        User newUser = User.register(
                "Jane",
                "Doe",
                "jane@test.com",
                "12345678901",
                "en-US",
                "999999999",
                "someurl.com"
        );

        newUser.initializeNewUser("encoded", LocalDateTime.now());

        // Act
        userRepository.save(newUser);

        eventPublisher.publishEvent(new UserRegisteredEvent(
                newUser.getEmail().toString(),
                LocalDateTime.now()
        ));

        // Assert
        verify(userRegisteredHandler, timeout(3000).atLeastOnce())
                .handle(any(UserRegisteredEvent.class));
    }

    //======================================
    // Course Completed + Certificate Issued
    //======================================
    @Test
    void should_publish_course_completed_and_certificate_events() throws Exception {
        mockMvc.perform(patch("/api/me/enrollments/{courseId}/progress", course.getId())
                .header("Authorization", "Bearer " + studentToken)
                .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                            "progressPercent": 100
                        }
                        """))
                .andExpect(status().isOk());

        User user = User.register(
                "john",
                "carter",
                "johncarter@email.com",
                "12345678901",
                "en-US",
                "123456789",
                "otherurl.com"
        );

        user.activate();
        user.initializeNewUser("encoded-password", LocalDateTime.now());
        user.auditCreate(1L, LocalDateTime.now());
        user = userRepository.save(user);

        User finalUser = user;
        new TransactionTemplate(transactionManager).executeWithoutResult(status -> {
            eventPublisher.publishEvent(new CourseCompletedEvent(
                    finalUser,
                    course,
                    LocalDateTime.now()
            ));

            eventPublisher.publishEvent(new CertificateIssuedEvent(
                    finalUser.getId(),
                    course.getId(),
                    LocalDateTime.now()
            ));
        });

        verify(courseCompletedHandler, timeout(5000).atLeastOnce())
                .handle(any());

        verify(certificateIssuedHandler, timeout(5000).atLeastOnce())
                .handle(any());
    }

    //=========================
    // Enrollment Cancelled
    //=========================
    @Test
    void should_publish_enrollment_cancelled_event() throws Exception {
        mockMvc.perform(delete("/api/me/enrollments/{courseId}", course.getId())
                .header("Authorization", "Bearer " + studentToken))
                .andExpect(status().isOk());

        verify(enrollmentCancelledHandler, timeout(1000).atLeastOnce())
                .handle(any(EnrollmentCancelledEvent.class));
    }

    //==================================
    // Rollback prevents event execution
    //==================================
    @Test
    void should_not_publish_event_when_transaction_rolls_back() throws Exception {
        mockMvc.perform(patch("/api/me/enrollments/{courseId}/progress", course.getId())
                .header("Authorization", "Bearer " + studentToken)
                .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                            "progressPercent": -10
                        }
                        """))
                .andExpect(status().isBadRequest());
    }
}