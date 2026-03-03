package br.com.novalearn.platform.domain.entities.course;

import br.com.novalearn.platform.core.exception.business.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static br.com.novalearn.platform.factories.entities.course.CreateCourseFactory.createInitializedCourse;
import static org.junit.jupiter.api.Assertions.*;

public class CourseUpdateInfoTest {
    private Course course;

    @BeforeEach
    void setUp() {
        course = createInitializedCourse();
    }

    @Test
    void should_update_all_fields_and_sanitize_values() {
        //when
        course.updateInfo(
                "Java Advanced",
                "Short desc",
                "Long description here",
                "Some observations"
        );

        //then
        assertEquals("Java Advanced", course.getName());
        assertEquals("Short desc", course.getShortDescription());
        assertEquals("Long description here", course.getLongDescription());
        assertEquals("Some observations", course.getObservations());
    }

    @Test
    void should_update_only_non_null_fields() {
        //given
        course.updateInfo(
                "Java updated",
                "Short",
                "Long",
                "Obs"
        );

        //when
        course.updateInfo(
                null,
                null,
                "Updated long description",
                null
        );

        //then
        assertEquals("Java updated", course.getName());
        assertEquals("Short", course.getShortDescription());
        assertEquals("Updated long description", course.getLongDescription());
        assertEquals("Obs", course.getObservations());
    }

    @Test
    void should_keep_course_valid_after_update() {
        //when
        course.updateInfo(
                "Valid name",
                null,
                null,
                null
        );

        //then
        assertTrue(course.isActive());
        assertFalse(course.isDeleted());
    }
}