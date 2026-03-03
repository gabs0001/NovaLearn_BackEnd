package br.com.novalearn.platform.domain.entities.course;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static br.com.novalearn.platform.factories.entities.course.CreateCourseFactory.createInitializedCourse;
import static org.junit.jupiter.api.Assertions.*;

public class CourseRegisterEnrollmentTest {
    private Course course;

    @BeforeEach
    void setUp() {
        course = createInitializedCourse();
    }

    @Test
    void should_initialize_students_counter_when_first_enrollment() {
        //given
        assertNull(course.getNumStudents());

        //when
        course.registerEnrollment();

        //then
        assertEquals(1, course.getNumStudents());
    }

    @Test
    void should_increment_students_counter_when_already_initialized() {
        //given
        course.registerEnrollment();

        //when
        course.registerEnrollment();

        //then
        assertEquals(2, course.getNumStudents());
    }

    @Test
    void should_accumulate_multiple_enrollments() {
        //when
        course.registerEnrollment();
        course.registerEnrollment();
        course.registerEnrollment();

        //then
        assertEquals(3, course.getNumStudents());
    }
}