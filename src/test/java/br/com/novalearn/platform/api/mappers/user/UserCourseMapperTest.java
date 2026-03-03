package br.com.novalearn.platform.api.mappers.user;

import br.com.novalearn.platform.api.dtos.user.course.UserCourseListResponseDTO;
import br.com.novalearn.platform.api.dtos.user.course.UserCourseResponseDTO;
import br.com.novalearn.platform.domain.entities.category.Category;
import br.com.novalearn.platform.domain.entities.course.Course;
import br.com.novalearn.platform.domain.entities.user.User;
import br.com.novalearn.platform.domain.entities.user.UserCourse;
import br.com.novalearn.platform.domain.enums.EnrollmentStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import static br.com.novalearn.platform.factories.entities.category.CreateCategoryFactory.createInitializedCategory;
import static br.com.novalearn.platform.factories.entities.user.CreateUserFactory.createInitializedUser;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class UserCourseMapperTest {
    private UserCourseMapper mapper;
    private User user;
    private Category category;
    private UserCourse userCourse;
    private LocalDateTime now;

    @BeforeEach
    void setUp() {
        now = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
        mapper = new UserCourseMapper();
        user = createInitializedUser();
        category = createInitializedCategory();
        userCourse = createIntializedEnrollment(
                createInitializedCourse(category, user)
        );
    }

    private Course createInitializedCourse(Category category, User user) {
        return Course.create("Java fundamentals", category, user);
    }

    private UserCourse createIntializedEnrollment(Course course) {
        return UserCourse.enroll(user, course, now);
    }

    public static UserCourse completedAndPaid(
            User user,
            Course course,
            BigDecimal amount,
            String method,
            LocalDateTime now
    ) {
        UserCourse uc = UserCourse.enroll(user, course, now);

        uc.registerPayment(amount, method, now);
        uc.updateProgress(80, now);
        uc.complete(now);
        uc.issueCertificate();

        return uc;
    }

    @Test
    void should_map_to_response_dto() {
        Course course = createInitializedCourse(category, user);

        LocalDateTime enrolledAt = now.minusDays(10);
        LocalDateTime completedAt = now.minusDays(2);

        UserCourse userCourse = completedAndPaid(
                user,
                course,
                BigDecimal.valueOf(199.90),
                "PIX",
                now
        );

        ReflectionTestUtils.setField(userCourse, "enrolledAt", enrolledAt);
        ReflectionTestUtils.setField(userCourse, "completedAt", completedAt);

        UserCourseResponseDTO dto = mapper.toResponseDTO(userCourse);

        assertThat(dto.getEnrolledAt()).isEqualTo(enrolledAt);
        assertThat(dto.getCompletedAt()).isEqualTo(completedAt);
    }

    @Test
    void should_map_with_null_user_and_course() {
        UserCourse userCourse = createIntializedEnrollment(createInitializedCourse(category, user));

        ReflectionTestUtils.setField(userCourse, "course", null);

        UserCourseResponseDTO dto = mapper.toResponseDTO(userCourse);

        assertThat(dto.getUserId()).isNull();
        assertThat(dto.getCourseId()).isNull();
    }

    @Test
    void should_map_to_list_response_dto() {
        Course course = createInitializedCourse(category, user);

        userCourse.updateProgress(40, now);

        UserCourseListResponseDTO dto = mapper.toListResponseDTO(userCourse);

        assertThat(dto.getId()).isEqualTo(userCourse.getId());

        assertThat(dto.getUserId()).isEqualTo(user.getId());
        assertThat(dto.getCourseId()).isEqualTo(course.getId());

        assertThat(dto.getEnrollmentStatus())
                .isEqualTo(userCourse.getEnrollmentStatus());

        assertThat(dto.getProgressPercent())
                .isEqualTo(userCourse.getProgressPercent());

        assertThat(dto.getActive()).isEqualTo(userCourse.isActive());
        assertThat(dto.getDeleted()).isEqualTo(userCourse.isDeleted());
    }

    @Test
    void should_map_list_with_null_relations() {
        UserCourse userCourse = createIntializedEnrollment(createInitializedCourse(category, user));

        ReflectionTestUtils.setField(userCourse, "course", null);

        UserCourseListResponseDTO dto = mapper.toListResponseDTO(userCourse);

        assertThat(dto.getUserId()).isNull();
        assertThat(dto.getCourseId()).isNull();
    }
}