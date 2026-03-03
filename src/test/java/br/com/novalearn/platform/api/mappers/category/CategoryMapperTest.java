package br.com.novalearn.platform.api.mappers.category;

import br.com.novalearn.platform.api.dtos.category.CategoryCreateRequestDTO;
import br.com.novalearn.platform.api.dtos.category.CategoryListResponseDTO;
import br.com.novalearn.platform.api.dtos.category.CategoryResponseDTO;
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

import java.time.LocalDateTime;
import java.util.Optional;

import static br.com.novalearn.platform.factories.dtos.category.CategoryTestFactory.categoryCreateRequest;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CategoryMapperTest {
    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private TimeProvider timeProvider;

    @InjectMocks
    private CategoryMapper mapper;

    private CategoryCreateRequestDTO createDTO;

    @BeforeEach
    void setUp() { createDTO = categoryCreateRequest(); }

    @Test
    void should_create_category_without_parent() {
        Long userId = 5L;
        createDTO.setParentCategoryId(null);

        LocalDateTime now = LocalDateTime.now();
        when(timeProvider.now()).thenReturn(now);

        Category category = mapper.toEntity(createDTO, userId);
        assertThat(category).isNotNull();

        assertThat(category.getName()).isEqualTo("Backend");
        assertThat(category.getAbbreviation()).isEqualTo("BCK");
        assertThat(category.getDescription()).isEqualTo("Backend Development");
        assertThat(category.getObservations()).isEqualTo("Observations");

        assertThat(category.getParentCategory()).isNull();

        assertThat(category.getCreatedAt()).isEqualTo(now);
        assertThat(category.getCreatedBy()).isEqualTo(userId);

        assertThat(category.isActive()).isTrue();
        assertThat(category.isDeleted()).isFalse();
    }

    @Test
    void should_create_category_with_parent() {
        Long userId = 5L;
        Long parentId = 10L;

        createDTO.setParentCategoryId(parentId);

        Category parent = new Category();
        parent.setId(parentId);

        LocalDateTime now = LocalDateTime.now();

        when(categoryRepository
                .findByIdAndDeletedFalse(parentId))
                .thenReturn(Optional.of(parent));

        when(timeProvider.now()).thenReturn(now);

        Category category = mapper.toEntity(createDTO, userId);

        assertThat(category).isNotNull();

        assertThat(category.getParentCategory()).isEqualTo(parent);
        assertThat(category.getParentCategoryId()).isEqualTo(parentId);

        assertThat(category.getCreatedBy()).isEqualTo(userId);
        assertThat(category.getCreatedAt()).isEqualTo(now);

        verify(categoryRepository).findByIdAndDeletedFalse(parentId);
    }

    @Test
    void should_throw_exception_when_parent_not_found() {
        Long parentId = 10L;

        createDTO.setParentCategoryId(parentId);

        when(categoryRepository
                .findByIdAndDeletedFalse(parentId))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                mapper.toEntity(createDTO, 5L)
        ).isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Parent category not found.");

        verify(categoryRepository).findByIdAndDeletedFalse(parentId);
    }

    @Test
    void should_map_to_response_dto() {
        Category parent = new Category();
        Category category = new Category();

        parent.setId(10L);
        category.setId(10L);

        category.setName("Backend");
        category.setDescription("Backend dev");
        category.setAbbreviation("BE");

        category.defineParent(parent);

        category.activate();
        category.markAsNotDeleted();

        category.auditCreate(5L, LocalDateTime.now().minusDays(2));

        CategoryResponseDTO dto = mapper.toResponseDTO(category);

        assertThat(dto).isNotNull();

        assertThat(dto.getId()).isEqualTo(10L);

        assertThat(dto.getActive()).isTrue();
        assertThat(dto.getDeleted()).isFalse();

        assertThat(dto.getName()).isEqualTo("Backend");
        assertThat(dto.getDescription()).isEqualTo("Backend dev");
        assertThat(dto.getAbbreviation()).isEqualTo("BE");

        assertThat(dto.getParentCategoryId()).isEqualTo(10L);
    }

    @Test
    void should_map_to_list_response_dto() {
        Category parent = new Category();
        Category category = new Category();

        category.setId(10L);
        parent.setId(10L);

        category.setName("Frontend");
        category.setAbbreviation("FE");

        category.defineParent(parent);

        category.activate();
        category.markAsNotDeleted();

        CategoryListResponseDTO dto = mapper.toListResponseDTO(category);

        assertThat(dto).isNotNull();

        assertThat(dto.getId()).isEqualTo(10L);
        assertThat(dto.getParentCategoryId()).isEqualTo(10L);

        assertThat(dto.getName()).isEqualTo("Frontend");
        assertThat(dto.getAbbreviation()).isEqualTo("FE");

        assertThat(dto.getActive()).isTrue();
        assertThat(dto.getDeleted()).isFalse();
    }
}