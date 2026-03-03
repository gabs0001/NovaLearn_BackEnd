package br.com.novalearn.platform.api.mappers.course;

import br.com.novalearn.platform.api.dtos.course.CourseListResponseDTO;
import br.com.novalearn.platform.api.dtos.course.CourseResponseDTO;
import br.com.novalearn.platform.domain.entities.category.Category;
import br.com.novalearn.platform.domain.entities.course.Course;
import br.com.novalearn.platform.domain.entities.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;

import static br.com.novalearn.platform.factories.entities.category.CreateCategoryFactory.createInitializedCategory;
import static br.com.novalearn.platform.factories.entities.user.CreateUserFactory.createInitializedUser;
import static org.assertj.core.api.Assertions.assertThat;

public class CourseMapperTest {
    private CourseMapper mapper;
    private User instructor;
    private Category category;
    private Course course;
    private LocalDateTime now;

    @BeforeEach
    void setUp() {
        mapper = new CourseMapper();
        instructor = createInitializedUser();
        category = createInitializedCategory();
        course = createInitializedCourse(category, instructor);
        now = LocalDateTime.now();
    }

    private Course createInitializedCourse(Category category, User instructor) {
        return Course.create("Java fundamentals", category, instructor);
    }

    @Test
    void should_map_course_to_course_response_dto() {
        course.auditCreate(10L, now);
        course.auditUpdate(20L, now);

        CourseResponseDTO dto = mapper.toResponseDTO(course);

        assertThat(dto.getId()).isEqualTo(course.getId());
        assertThat(dto.getActive()).isEqualTo(course.isActive());
        assertThat(dto.getDeleted()).isEqualTo(course.isDeleted());

        assertThat(dto.getCreatedAt()).isEqualTo(course.getCreatedAt());
        assertThat(dto.getUpdatedAt()).isEqualTo(course.getUpdatedAt());

        assertThat(dto.getObservations()).isEqualTo(course.getObservations());

        assertThat(dto.getUserId()).isEqualTo(instructor.getId());
        assertThat(dto.getCategoryId()).isEqualTo(category.getId());

        assertThat(dto.getName()).isEqualTo(course.getName());
        assertThat(dto.getShortDescription()).isEqualTo(course.getShortDescription());
        assertThat(dto.getLongDescription()).isEqualTo(course.getLongDescription());

        assertThat(dto.getPrice()).isEqualTo(course.getPrice());
        assertThat(dto.getPaid()).isEqualTo(course.isPaid());

        assertThat(dto.getThumbnailUrl()).isEqualTo(course.getThumbnailUrl());

        assertThat(dto.getDuration()).isEqualTo(course.getDuration());
        assertThat(dto.getNumLessons()).isEqualTo(course.getNumLessons());

        assertThat(dto.getNumStudents()).isEqualTo(course.getNumStudents());
        assertThat(dto.getNumRatingTotal()).isEqualTo(course.getNumRatingTotal());
        assertThat(dto.getNumRatingCount()).isEqualTo(course.getNumRatingCount());

        assertThat(dto.getPublishedAt()).isEqualTo(course.getPublishedAt());
        assertThat(dto.getSlug()).isEqualTo(course.getSlug());
        assertThat(dto.getStatus()).isEqualTo(course.getStatus());

        assertThat(dto.getCreatedBy()).isEqualTo(course.getCreatedBy());
        assertThat(dto.getUpdatedBy()).isEqualTo(course.getUpdatedBy());
    }

    @Test
    void should_map_course_to_course_response_dto_with_null_relations() {
        Course course = createInitializedCourse(
                createInitializedCategory(), createInitializedUser()
        );

        ReflectionTestUtils.setField(course, "category", null);
        ReflectionTestUtils.setField(course, "instructor", null);

        CourseResponseDTO dto = mapper.toResponseDTO(course);

        assertThat(dto.getUserId()).isNull();
        assertThat(dto.getCategoryId()).isNull();
    }

    @Test
    void should_map_course_to_course_list_response_dto() {
        CourseListResponseDTO dto = mapper.toListResponseDTO(course);

        assertThat(dto.getId()).isEqualTo(course.getId());

        assertThat(dto.getUserId()).isEqualTo(instructor.getId());
        assertThat(dto.getCategoryId()).isEqualTo(category.getId());

        assertThat(dto.getName()).isEqualTo(course.getName());
        assertThat(dto.getShortDescription()).isEqualTo(course.getShortDescription());

        assertThat(dto.getStatus()).isEqualTo(course.getStatus());

        assertThat(dto.getThumbnailUrl()).isEqualTo(course.getThumbnailUrl());

        assertThat(dto.getPrice()).isEqualTo(course.getPrice());
        assertThat(dto.getPaid()).isEqualTo(course.isPaid());

        assertThat(dto.getNumStudents()).isEqualTo(course.getNumStudents());

        assertThat(dto.getNumRatingTotal()).isEqualTo(course.getNumRatingTotal());
        assertThat(dto.getNumRatingCount()).isEqualTo(course.getNumRatingCount());

        assertThat(dto.getSlug()).isEqualTo(course.getSlug());

        assertThat(dto.getActive()).isEqualTo(course.isActive());
        assertThat(dto.getDeleted()).isEqualTo(course.isDeleted());
    }

    @Test
    void should_map_course_to_course_list_response_dto_with_null_relations() {
        Course course = createInitializedCourse(
                createInitializedCategory(), createInitializedUser()
        );

        ReflectionTestUtils.setField(course, "category", null);
        ReflectionTestUtils.setField(course, "instructor", null);

        CourseListResponseDTO dto = mapper.toListResponseDTO(course);

        assertThat(dto.getUserId()).isNull();
        assertThat(dto.getCategoryId()).isNull();
    }
}