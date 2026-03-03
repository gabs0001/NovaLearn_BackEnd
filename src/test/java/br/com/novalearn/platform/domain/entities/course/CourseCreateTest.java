package br.com.novalearn.platform.domain.entities.course;

import br.com.novalearn.platform.core.exception.business.ValidationException;
import br.com.novalearn.platform.domain.entities.category.Category;
import br.com.novalearn.platform.domain.entities.user.User;
import br.com.novalearn.platform.domain.enums.CourseStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static br.com.novalearn.platform.factories.entities.category.CreateCategoryFactory.createInitializedCategory;
import static br.com.novalearn.platform.factories.entities.course.CreateCourseFactory.createInitializedCourse;
import static br.com.novalearn.platform.factories.entities.user.CreateUserFactory.createInitializedUser;
import static org.junit.jupiter.api.Assertions.*;

public class CourseCreateTest {
    private User instructor;
    private Category category;
    private String categoryName;

    @BeforeEach
    void setUp() {
        instructor = createInitializedUser();
        category = createInitializedCategory();
        categoryName = "Java Fundamentals";
    }

    @Test
    void should_create_course_with_valid_initial_state() {
        Course course = createInitializedCourse();

        //then
        assertNotNull(course);
        assertEquals(categoryName, course.getName());
        assertEquals(category.getName(), course.getCategory().getName());
        assertEquals(instructor.getRole(), course.getInstructor().getRole());

        assertEquals(CourseStatus.DRAFT, course.getStatus());
        assertTrue(course.isActive());
        assertFalse(course.isDeleted());

        assertNull(course.getPublishedAt());
        assertNull(course.getNumStudents());
    }

    @Test
    void should_throw_exception_when_name_is_null() {
        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> Course.create(null, category, instructor)
        );

        assertEquals("Course name is required.", exception.getMessage());
    }

    @Test
    void should_throw_exception_when_name_is_blank() {
        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> Course.create("  ", category, instructor)
        );

        assertEquals("Course name is required.", exception.getMessage());
    }

    @Test
    void should_throw_exception_when_category_is_null() {
        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> Course.create(categoryName, null, instructor)
        );

        assertEquals("Category is required.", exception.getMessage());
    }

    @Test
    void should_throw_exception_when_instructor_is_null() {
        ValidationException ex = assertThrows(
                ValidationException.class,
                () -> Course.create(categoryName, category, null)
        );

        assertEquals("Instructor is required.", ex.getMessage());
    }
}