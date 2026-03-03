package br.com.novalearn.platform.domain.entities.category;

import br.com.novalearn.platform.core.exception.business.ValidationException;
import org.junit.jupiter.api.Test;

import static br.com.novalearn.platform.factories.entities.category.CreateCategoryFactory.createInitializedCategory;
import static org.junit.jupiter.api.Assertions.*;

public class CategoryCreateTest {
    @Test
    void should_create_category_with_valid_name() {
        //when
        Category category = createInitializedCategory();

        //then
        assertNotNull(category);
        assertEquals("Backend", category.getName());
        assertTrue(category.isActive());
        assertFalse(category.isDeleted());
        assertTrue(category.isRoot());
    }

    @Test
    void should_throw_validation_exception_when_name_is_null() {
        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> Category.create(null, null, null, null)
        );

        assertEquals("Category name is required.", exception.getMessage());
    }

    @Test
    void should_throw_validation_exception_when_name_is_blank() {
        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> Category.create("   ", "   ", "   ", "   ")
        );

        assertEquals("Category name is required.", exception.getMessage());
    }
}