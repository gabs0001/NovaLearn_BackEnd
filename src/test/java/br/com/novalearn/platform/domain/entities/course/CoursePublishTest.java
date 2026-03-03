package br.com.novalearn.platform.domain.entities.course;

import br.com.novalearn.platform.core.exception.business.ValidationException;
import br.com.novalearn.platform.domain.entities.category.Category;
import br.com.novalearn.platform.domain.entities.user.User;
import br.com.novalearn.platform.domain.enums.CourseStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.lang.reflect.Field;
import java.time.LocalDateTime;

import static br.com.novalearn.platform.factories.entities.category.CreateCategoryFactory.createInitializedCategory;
import static br.com.novalearn.platform.factories.entities.user.CreateUserFactory.createInitializedUser;
import static org.junit.jupiter.api.Assertions.*;

public class CoursePublishTest {
    private Course course;
    private LocalDateTime now;
    private String categoryName;

    @BeforeEach
    void setUp() {
        now = LocalDateTime.now();
        categoryName = "Java Fundamentals";
        course = createInitializedCourse();
        setPrivateField(course, "numLessons", 10);
        setPrivateField(course, "slug", "spring-boot-essentials");
    }

    private Course createInitializedCourse() {
        User user = createInitializedUser();
        Category category = createInitializedCategory();

        Course newCourse = Course.create(categoryName, category, user);

        newCourse.registerLesson();
        newCourse.defineSlug("some-slug");
        newCourse.publish(now);

        newCourse.updateInfo(
                "Spring Boot Essentials",
                "Complete Spring Course",
                "Spring Advanced Content",
                "Observations"
        );

        return newCourse;
    }

    @Test
    void should_publish_course_when_all_requirements_are_met() {
        //then
        assertEquals(CourseStatus.PUBLISHED, course.getStatus());
        assertEquals(now, course.getPublishedAt());
    }

    @Test
    void should_throw_exception_when_course_is_already_published() {
        //when / then
        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> course.publish(now)
        );

        assertEquals("Course is already published.", exception.getMessage());
    }

    @Test
    void should_throw_exception_when_course_has_no_slug() {
        // given
        setPrivateField(course, "slug", null);

        // when / then
        ValidationException ex = assertThrows(
                ValidationException.class,
                () -> course.defineSlug("")
        );

        assertEquals("Course must have a slug to be published.", ex.getMessage());
    }

    private void setPrivateField(Object target, String fieldName, Object value) {
        try {
            Field field = target.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(target, value);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}