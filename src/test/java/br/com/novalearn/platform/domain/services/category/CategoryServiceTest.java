package br.com.novalearn.platform.domain.services.category;

import br.com.novalearn.platform.api.dtos.category.CategoryCreateRequestDTO;
import br.com.novalearn.platform.api.dtos.category.CategoryListResponseDTO;
import br.com.novalearn.platform.api.dtos.category.CategoryResponseDTO;
import br.com.novalearn.platform.api.dtos.category.CategoryUpdateRequestDTO;
import br.com.novalearn.platform.api.mappers.category.CategoryMapper;
import br.com.novalearn.platform.core.exception.business.ConflictException;
import br.com.novalearn.platform.core.exception.business.ResourceNotFoundException;
import br.com.novalearn.platform.domain.entities.category.Category;
import br.com.novalearn.platform.domain.repositories.category.CategoryRepository;
import br.com.novalearn.platform.infra.time.provider.TimeProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CategoryServiceTest {
    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private CategoryMapper categoryMapper;

    @Mock
    private TimeProvider timeProvider;

    @InjectMocks
    private CategoryService service;

    private Category entity;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.clearContext();
        entity = mock(Category.class);
    }

    @Test
    void create_should_create_category() {
        CategoryCreateRequestDTO dto = new CategoryCreateRequestDTO();
        dto.setName("Backend");

        when(categoryRepository.existsByNameIgnoreCaseAndDeletedFalse("Backend"))
                .thenReturn(false);

        Category saved = mock(Category.class);

        when(categoryMapper.toEntity(dto, 5L)).thenReturn(entity);

        when(categoryRepository.save(entity)).thenReturn(saved);

        CategoryResponseDTO expected = new CategoryResponseDTO();

        when(categoryMapper.toResponseDTO(saved)).thenReturn(expected);

        CategoryResponseDTO result = service.create(5L, dto);

        assertEquals(expected, result);
    }

    @Test
    void create_should_throw_if_name_exists() {
        CategoryCreateRequestDTO dto = new CategoryCreateRequestDTO();
        dto.setName("Backend");

        when(categoryRepository.existsByNameIgnoreCaseAndDeletedFalse("Backend"))
                .thenReturn(true);

        assertThrows(
                ConflictException.class,
                () -> service.create(5L, dto)
        );
    }

    @Test
    void update_should_update_fields() {
        CategoryUpdateRequestDTO dto = new CategoryUpdateRequestDTO();

        dto.setName("Java");
        dto.setDescription("Desc");
        dto.setAbbreviation("JV");
        dto.setObservations("Obs");

        when(categoryRepository.findById(10L)).thenReturn(Optional.of(entity));

        when(entity.getName()).thenReturn("Backend");

        when(categoryRepository.existsByNameIgnoreCaseAndDeletedFalse("Java"))
                .thenReturn(false);

        Category saved = mock(Category.class);

        when(categoryRepository.save(entity)).thenReturn(saved);

        CategoryResponseDTO expected = new CategoryResponseDTO();

        when(categoryMapper.toResponseDTO(saved)).thenReturn(expected);

        CategoryResponseDTO result = service.update(10L, dto, 5L);

        verify(entity).setName("Java");
        verify(entity).setDescription("Desc");
        verify(entity).setAbbreviation("JV");
        verify(entity).setObservations("Obs");

        assertEquals(expected, result);
    }

    @Test
    void update_should_throw_if_name_exists() {
        CategoryUpdateRequestDTO dto = new CategoryUpdateRequestDTO();

        dto.setName("Java");

        when(categoryRepository.findById(10L)).thenReturn(Optional.of(entity));

        when(entity.getName()).thenReturn("Backend");

        when(categoryRepository.existsByNameIgnoreCaseAndDeletedFalse("Java"))
                .thenReturn(true);

        assertThrows(
                ConflictException.class,
                () -> service.update(10L, dto, 5L)
        );
    }

    @Test
    void update_should_throw_if_parent_not_found() {
        CategoryUpdateRequestDTO dto = new CategoryUpdateRequestDTO();

        dto.setParentCategoryId(10L);

        when(categoryRepository.findById(10L)).thenReturn(Optional.of(entity));

        when(categoryRepository.findByIdAndDeletedFalse(10L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> service.update(10L, dto, 5L)
        );
    }

    @Test
    void update_should_set_parent() {
        CategoryUpdateRequestDTO dto = new CategoryUpdateRequestDTO();

        dto.setParentCategoryId(2L);

        Category parent = mock(Category.class);

        when(categoryRepository.findById(10L)).thenReturn(Optional.of(entity));

        when(categoryRepository.findByIdAndDeletedFalse(2L))
                .thenReturn(Optional.of(parent));

        when(categoryRepository.save(entity)).thenReturn(entity);

        when(categoryMapper.toResponseDTO(entity)).thenReturn(new CategoryResponseDTO());

        service.update(10L, dto, 5L);

        verify(entity).defineParent(parent);
    }

    @Test
    void findById_should_return() {
        when(categoryRepository.findById(10L)).thenReturn(Optional.of(entity));

        CategoryResponseDTO dto = new CategoryResponseDTO();

        when(categoryMapper.toResponseDTO(entity)).thenReturn(dto);

        CategoryResponseDTO result = service.findById(10L);

        assertEquals(dto, result);
    }

    @Test
    void listAllActive_should_return_list() {
        Category c1 = mock(Category.class);
        Category c2 = mock(Category.class);

        when(categoryRepository.findAllByDeletedFalse()).thenReturn(List.of(c1, c2));

        when(categoryMapper.toListResponseDTO(any())).thenReturn(new CategoryListResponseDTO());

        List<CategoryListResponseDTO> result = service.listAllActive();

        assertEquals(2, result.size());
    }

    @Test
    void listAll_should_return_list() {
        when(categoryRepository.findAll()).thenReturn(List.of(entity));

        when(categoryMapper.toListResponseDTO(any())).thenReturn(new CategoryListResponseDTO());

        List<CategoryListResponseDTO> result = service.listAll();

        assertEquals(1, result.size());
    }

    @Test
    void listForSelect_should_return_active_only() {
        when(categoryRepository.findAllByActiveTrueAndDeletedFalse())
                .thenReturn(List.of(entity));

        when(categoryMapper.toListResponseDTO(any())).thenReturn(new CategoryListResponseDTO());

        List<CategoryListResponseDTO> result = service.listForSelect();

        assertEquals(1, result.size());
    }

    @Test
    void existsByName_should_return_false_when_null() {
        assertFalse(service.existsByName(null));
    }

    @Test
    void existsByName_should_return_false_when_blank() {
        assertFalse(service.existsByName("   "));
    }

    @Test
    void existsByName_should_return_true_when_exists() {
        when(categoryRepository.existsByNameIgnoreCaseAndDeletedFalse("Java"))
                .thenReturn(true);

        assertTrue(service.existsByName(" Java "));
    }
}