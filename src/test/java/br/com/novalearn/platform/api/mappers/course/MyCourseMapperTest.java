package br.com.novalearn.platform.api.mappers.course;

import br.com.novalearn.platform.api.dtos.course.my.MyCourseResponseDTO;
import br.com.novalearn.platform.domain.entities.category.Category;
import br.com.novalearn.platform.domain.entities.course.Course;
import br.com.novalearn.platform.domain.entities.user.User;
import br.com.novalearn.platform.domain.entities.user.UserCourse;
import br.com.novalearn.platform.infra.time.provider.TimeProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.List;

import static br.com.novalearn.platform.factories.entities.category.CreateCategoryFactory.createInitializedCategory;
import static br.com.novalearn.platform.factories.entities.user.CreateUserFactory.createInitializedUser;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class MyCourseMapperTest {
    private MyCourseMapper mapper;
    private User instructor;
    private Category category;
    private Course course;
    private UserCourse userCourse;
    private LocalDateTime now;

    @BeforeEach
    void setUp() {
        now = LocalDateTime.now();
        mapper = new MyCourseMapper();
        instructor = createInitializedUser();
        category = createInitializedCategory();
        course = createInitializedCourse(category, instructor);
        userCourse = createIntializedEnrollment(course);
    }

    private Course createInitializedCourse(Category category, User instructor) {
        return Course.create("Java fundamentals", category, instructor);
    }

    private UserCourse createIntializedEnrollment(Course course) {
        return UserCourse.enroll(instructor, course, now);
    }

    @Test
    void should_map_user_course_to_my_course_response_dto() {
        Course course = createInitializedCourse(
                createInitializedCategory(), createInitializedUser()
        );

        ReflectionTestUtils.setField(course, "category", null);
        ReflectionTestUtils.setField(course, "instructor", null);

        LocalDateTime enrolledAt = now;

        MyCourseResponseDTO dto = mapper.toResponseDTO(userCourse);

        assertThat(dto.getCourseId()).isEqualTo(course.getId());
        assertThat(dto.getName()).isEqualTo(course.getName());
        assertThat(dto.getShortDescription())
                .isEqualTo(course.getShortDescription());

        assertThat(dto.getEnrollmentStatus())
                .isEqualTo(userCourse.getEnrollmentStatus());

        assertThat(dto.getEnrolledAt()).isEqualTo(enrolledAt);
    }

    @Test
    void should_map_user_course_with_null_course() {
        UserCourse userCourse = createIntializedEnrollment(
                createInitializedCourse(
                        createInitializedCategory(), createInitializedUser()
                )
        );

        ReflectionTestUtils.setField(userCourse, "course", null);

        MyCourseResponseDTO dto = mapper.toResponseDTO(userCourse);

        assertThat(dto.getCourseId()).isNull();
        assertThat(dto.getName()).isNull();
        assertThat(dto.getShortDescription()).isNull();
    }

    @Test
    void should_map_list_of_user_courses() {
        Course course1 = createInitializedCourse(
                createInitializedCategory(), createInitializedUser()
        );

        ReflectionTestUtils.setField(course1, "category", null);
        ReflectionTestUtils.setField(course1, "instructor", null);

        Course course2 = createInitializedCourse(
                createInitializedCategory(), createInitializedUser()
        );

        ReflectionTestUtils.setField(course2, "category", null);
        ReflectionTestUtils.setField(course2, "instructor", null);

        UserCourse uc1 = createIntializedEnrollment(course1);
        UserCourse uc2 = createIntializedEnrollment(course2);

        List<UserCourse> list = List.of(uc1, uc2);

        List<MyCourseResponseDTO> result = mapper.toListResponseDTO(list);

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getCourseId()).isEqualTo(course1.getId());
        assertThat(result.get(1).getCourseId()).isEqualTo(course2.getId());
    }

    @Test
    void should_preserve_order_when_mapping_list() {
        Course course1 = createInitializedCourse(
                createInitializedCategory(), createInitializedUser()
        );

        ReflectionTestUtils.setField(course1, "category", null);
        ReflectionTestUtils.setField(course1, "instructor", null);

        Course course2 = createInitializedCourse(
                createInitializedCategory(), createInitializedUser()
        );

        ReflectionTestUtils.setField(course2, "category", null);
        ReflectionTestUtils.setField(course2, "instructor", null);

        Course course3 = createInitializedCourse(
                createInitializedCategory(), createInitializedUser()
        );

        ReflectionTestUtils.setField(course3, "category", null);
        ReflectionTestUtils.setField(course3, "instructor", null);

        UserCourse uc1 = createIntializedEnrollment(course1);
        UserCourse uc2 = createIntializedEnrollment(course2);
        UserCourse uc3 = createIntializedEnrollment(course3);

        List<UserCourse> list = List.of(uc1, uc2, uc3);

        List<MyCourseResponseDTO> result = mapper.toListResponseDTO(list);

        assertThat(result.get(0).getCourseId()).isEqualTo(course1.getId());
        assertThat(result.get(1).getCourseId()).isEqualTo(course2.getId());
        assertThat(result.get(2).getCourseId()).isEqualTo(course3.getId());
    }
}