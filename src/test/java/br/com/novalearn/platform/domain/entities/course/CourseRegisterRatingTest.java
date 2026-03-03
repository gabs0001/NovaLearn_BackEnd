package br.com.novalearn.platform.domain.entities.course;

import br.com.novalearn.platform.core.exception.business.ValidationException;
import br.com.novalearn.platform.domain.entities.category.Category;
import br.com.novalearn.platform.domain.entities.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static br.com.novalearn.platform.factories.entities.course.CreateCourseFactory.createInitializedCourse;
import static org.junit.jupiter.api.Assertions.*;

public class CourseRegisterRatingTest {
    private Course course;

    @BeforeEach
    void setUp() {
        course = createInitializedCourse();
    }

    @Test
    void should_initialize_rating_counters_on_first_rating() {
        //given
        assertNull(course.getNumRatingTotal());
        assertNull(course.getNumRatingCount());

        //when
        course.registerRating(5);

        //then
        assertEquals(5, course.getNumRatingTotal());
        assertEquals(1, course.getNumRatingCount());
    }

    @Test
    void should_accumulate_multiple_ratings_correctly() {
        //when
        course.registerRating(4);
        course.registerRating(5);
        course.registerRating(3);

        //then
        assertEquals(12, course.getNumRatingTotal());
        assertEquals(3, course.getNumRatingCount());
    }

    @Test
    void should_throw_validation_exception_when_rating_is_less_than_one() {
        //when / then
        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> course.registerRating(0)
        );

        assertEquals("Rating must be between 1 and 5.", exception.getMessage());
    }

    @Test
    void should_throw_validation_exception_when_rating_is_greater_than_five() {
        //when / then
        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> course.registerRating(6)
        );

        assertEquals("Rating must be between 1 and 5.", exception.getMessage());
    }
}