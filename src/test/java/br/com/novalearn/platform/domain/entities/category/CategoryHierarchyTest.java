package br.com.novalearn.platform.domain.entities.category;

import br.com.novalearn.platform.core.exception.business.ValidationException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class CategoryHierarchyTest {
    private Category createCategory(String name) {
        return Category.create(
                name,
                "BCK",
                "Some Description",
                "Observations"
        );
    }

    @Test
    void should_allow_valid_hierarchy_chain() {
        //given
        Category backend = createCategory("Backend");
        Category java = createCategory("Java");
        Category spring = createCategory("Spring");

        //when
        java.defineParent(backend);
        spring.defineParent(java);

        //then
        assertEquals(backend, java.getParentCategory());
        assertEquals(java, spring.getParentCategory());
        assertTrue(backend.isRoot());
        assertFalse(java.isRoot());
        assertFalse(spring.isRoot());
    }

    @Test
    void should_throw_validation_exception_when_creating_indirect_cycle_depth_2() {
        //given
        Category backend = createCategory("Backend");
        Category java = createCategory("Java");

        java.defineParent(backend);

        //when / then
        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> backend.defineParent(java)
        );

        assertEquals(
                "Category hierarchy cycle detected.",
                exception.getMessage()
        );
    }

    @Test
    void should_throw_validation_exception_when_creating_indirect_cycle_depth_3() {
        //given
        Category backend = createCategory("Backend");
        Category java = createCategory("Java");
        Category spring = createCategory("Spring");

        java.defineParent(backend);
        spring.defineParent(java);

        //when / then
        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> backend.defineParent(spring)
        );

        assertEquals(
                "Category hierarchy cycle detected.",
                exception.getMessage()
        );
    }

    @Test
    void should_throw_validation_exception_when_creating_cycle_in_middle_of_tree() {
        //given
        Category backend = createCategory("Backend");
        Category java = createCategory("Java");
        Category spring = createCategory("Spring");
        Category security = createCategory("Security");

        java.defineParent(backend);
        spring.defineParent(java);
        security.defineParent(spring);

        //when / then
        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> java.defineParent(security)
        );

        assertEquals(
                "Category hierarchy cycle detected.",
                exception.getMessage()
        );
    }
}