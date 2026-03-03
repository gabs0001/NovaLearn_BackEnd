package br.com.novalearn.platform.domain.entities.course;

import br.com.novalearn.platform.core.exception.business.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static br.com.novalearn.platform.factories.entities.course.CreateCourseFactory.createInitializedCourse;
import static org.junit.jupiter.api.Assertions.*;

public class CourseDefinePricingTest {
    private Course course;
    private BigDecimal price;

    @BeforeEach
    void setUp() {
        course = createInitializedCourse();
        price = new BigDecimal("199.90");
    }

    @Test
    void should_define_pricing_for_paid_course_with_valid_price() {
        //when
        course.definePricing(true, price);

        //then
        assertTrue(course.isPaid());
        assertEquals(price, course.getPrice());
    }

    @Test
    void should_define_pricing_for_free_course_and_set_price_to_zero() {
        //when
        course.definePricing(false, price);

        //then
        assertFalse(course.isPaid());
        assertEquals(BigDecimal.ZERO, course.getPrice());
    }

    @Test
    void should_throw_exception_when_paid_course_has_null_price() {
        //when / then
        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> course.definePricing(true, null)
        );

        assertEquals("Paid courses must have a price.", exception.getMessage());
    }

    @Test
    void should_throw_exception_when_paid_course_has_zero_price() {
        // when / then
        ValidationException ex = assertThrows(
                ValidationException.class,
                () -> course.definePricing(true, BigDecimal.ZERO)
        );

        assertEquals("Paid courses must have a price.", ex.getMessage());
    }

    @Test
    void should_throw_exception_when_paid_course_has_negative_price() {
        // when / then
        ValidationException ex = assertThrows(
                ValidationException.class,
                () -> course.definePricing(true, new BigDecimal("-10"))
        );

        assertEquals("Paid courses must have a price.", ex.getMessage());
    }
}