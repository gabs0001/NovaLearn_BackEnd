package br.com.novalearn.platform.api.mappers.enrollment;

import br.com.novalearn.platform.api.dtos.enrollment.EnrollmentResponseDTO;
import br.com.novalearn.platform.api.dtos.enrollment.history.EnrollmentHistoryResponseDTO;
import br.com.novalearn.platform.api.mappers.enrollment.history.EnrollmentHistoryMapper;
import br.com.novalearn.platform.domain.entities.course.Course;
import br.com.novalearn.platform.domain.entities.user.UserCourse;
import br.com.novalearn.platform.domain.enums.EnrollmentStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import static br.com.novalearn.platform.factories.entities.course.CreateCourseFactory.createInitializedCourse;
import static br.com.novalearn.platform.factories.entities.enrollment.CreateEnrollmentFactory.createInitializedEnrollment;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class EnrollmentMapperTest {
    private LocalDateTime now;
    private EnrollmentMapper mapper;
    private EnrollmentHistoryMapper historyMapper;
    private UserCourse userCourse;

    @BeforeEach
    void setUp() {
        now = LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS);
        mapper = new EnrollmentMapper();
        historyMapper = new EnrollmentHistoryMapper();
        userCourse = createInitializedEnrollment();
    }

    @Test
    void should_map_user_course_to_enrollment_response_dto() {
        ReflectionTestUtils.setField(userCourse, "enrolledAt", now);
        userCourse.updateProgress(60, now);

        EnrollmentResponseDTO dto = mapper.toResponseDTO(userCourse);

        assertThat(dto).isNotNull();
        assertThat(dto.getEnrolledAt()).isEqualTo(now);
        assertThat(dto.getProgressPercent()).isEqualTo(60);
        assertThat(dto.getEnrollmentStatus()).isEqualTo(userCourse.getEnrollmentStatus());
    }

    @Test
    void should_map_user_course_to_enrollment_history_response_dto() {
        LocalDateTime enrolledDate = now.minusDays(20);
        LocalDateTime completedDate = now.minusDays(2);

        ReflectionTestUtils.setField(userCourse, "enrolledAt", enrolledDate);
        ReflectionTestUtils.setField(userCourse, "completedAt", completedDate);
        ReflectionTestUtils.setField(userCourse, "enrollmentStatus", EnrollmentStatus.COMPLETED);
        ReflectionTestUtils.setField(userCourse, "progressPercent", 100);
        ReflectionTestUtils.setField(userCourse, "certificateIssued", true);

        EnrollmentHistoryResponseDTO dto = historyMapper.toResponseDTO(userCourse);

        assertThat(dto).isNotNull();

        assertThat(dto.getEnrolledAt()).isEqualTo(enrolledDate);
        assertThat(dto.getCompletedAt()).isEqualTo(completedDate);

        assertThat(dto.getEnrollmentStatus()).isEqualTo(EnrollmentStatus.COMPLETED);
        assertThat(dto.getCertificateIssued()).isTrue();
    }
}