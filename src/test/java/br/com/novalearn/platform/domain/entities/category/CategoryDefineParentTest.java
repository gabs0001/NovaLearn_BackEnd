package br.com.novalearn.platform.domain.entities.category;

import br.com.novalearn.platform.core.exception.business.ValidationException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class CategoryDefineParentTest {
    private Category createCategory(String name) {
        return Category.create(
                name,
                "BCK",
                "Some Description",
                "Observations"
        );
    }

    @Test
    void should_define_parent_category() {
        //given
        Category parent = createCategory("Backend");
        Category child = createCategory("Java");

        //when
        child.defineParent(parent);

        //then
        assertEquals(parent, child.getParentCategory());
        assertFalse(child.isRoot());
    }

    @Test
    void should_remove_parent_category_when_null_is_passed() {
        //given
        Category parent = createCategory("Backend");
        Category child = createCategory("Java");
        child.defineParent(parent);

        //when
        child.defineParent(null);

        //then
        assertNull(child.getParentCategory());
        assertTrue(child.isRoot());
    }

    @Test
    void should_throw_validation_exception_when_category_is_parent_of_itself() {
        //given
        Category category = createCategory("Backend");

        //when / then
        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> category.defineParent(category)
        );

        assertEquals(
                "Category cannot be parent of itself.",
                exception.getMessage()
        );
    }

    @Test
    void should_throw_validation_exception_when_parent_is_deleted() {
        //given
        Category parent = createCategory("Backend");
        parent.delete();

        Category child = createCategory("Java");

        //when / then
        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> child.defineParent(parent)
        );

        assertEquals(
                "Parent category must be active.",
                exception.getMessage()
        );
    }

    @Test
    void should_throw_validation_exception_when_parent_is_inactive() {
        //given
        Category parent = createCategory("Backend");
        parent.deactivate();

        Category child = createCategory("Java");

        //when / then
        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> child.defineParent(parent)
        );

        assertEquals(
                "Parent category must be active.",
                exception.getMessage()
        );
    }
}