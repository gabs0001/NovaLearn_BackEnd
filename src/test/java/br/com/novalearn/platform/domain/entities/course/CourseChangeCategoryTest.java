package br.com.novalearn.platform.domain.entities.course;

import br.com.novalearn.platform.core.exception.business.ValidationException;
import br.com.novalearn.platform.domain.entities.category.Category;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static br.com.novalearn.platform.factories.entities.course.CreateCourseFactory.createInitializedCourse;
import static org.junit.jupiter.api.Assertions.*;

public class CourseChangeCategoryTest {
    private Course course;
    private Category category;

    @BeforeEach
    void setUp() {
        course = createInitializedCourse();
        category = new Category();
    }

    private void createCategory(String name) {
        Category.create(
                name,
                "BCK",
                "Some Description",
                "Observations"
        );
    }

    private Category createActiveCategory() {
        createCategory("Cloud");
        category.activate();
        return category;
    }

    private Category createInactiveCategory() {
        createCategory("DevOps");
        category.deactivate();
        return category;
    }

    private Category createDeletedCategory() {
        createCategory("Security");
        category.markAsDeleted();
        return category;
    }

    @Test
    void should_change_category_when_category_is_active() {
        //given
        Category newCategory = createActiveCategory();

        //when
        course.changeCategory(newCategory);

        //then
        assertEquals(newCategory, course.getCategory());
    }

    @Test
    void should_change_category_when_category_is_inactive() {
        //given
        Category inactiveCategory = createInactiveCategory();

        //when / then
        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> course.changeCategory(inactiveCategory)
        );

        assertEquals("Category must be active.", exception.getMessage());
    }

    @Test
    void should_throw_exception_when_category_is_deleted() {
        //given
        Category deletedCategory = createDeletedCategory();

        //when / then
        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> course.changeCategory(deletedCategory)
        );

        assertEquals("Category must be active.", exception.getMessage());
    }

    @Test
    void should_throw_validation_exception_when_category_is_null() {
        ValidationException ex = assertThrows(
                ValidationException.class,
                () -> course.changeCategory(null)
        );

        assertEquals("Category is required.", ex.getMessage());
    }
}