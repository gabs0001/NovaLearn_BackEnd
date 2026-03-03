package br.com.novalearn.platform.api.mappers.category;

import br.com.novalearn.platform.api.dtos.category.CategoryCreateRequestDTO;
import br.com.novalearn.platform.api.dtos.category.CategoryListResponseDTO;
import br.com.novalearn.platform.api.dtos.category.CategoryResponseDTO;
import br.com.novalearn.platform.core.exception.business.ResourceNotFoundException;
import br.com.novalearn.platform.domain.entities.category.Category;
import br.com.novalearn.platform.domain.repositories.category.CategoryRepository;
import br.com.novalearn.platform.infra.time.provider.TimeProvider;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.stream.Collectors;

@Component
public class CategoryMapper {
    private final CategoryRepository categoryRepository;
    private final TimeProvider timeProvider;

    public CategoryMapper(CategoryRepository categoryRepository, TimeProvider timeProvider) {
        this.categoryRepository = categoryRepository;
        this.timeProvider = timeProvider;
    }

    //ajustar depois!
    public Category toEntity(CategoryCreateRequestDTO dto, Long userId) {
        Category entity = new Category();

        entity.setName(dto.getName().trim());
        entity.setAbbreviation(dto.getAbbreviation());
        entity.setDescription(dto.getDescription());
        entity.setObservations(dto.getObservations());

        if(dto.getParentCategoryId() != null) {
            Category parent = categoryRepository.findByIdAndDeletedFalse(dto.getParentCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Parent category not found."));
            entity.defineParent(parent);
        }

        entity.setCreatedAt(timeProvider.now());
        entity.setCreatedBy(userId);
        entity.activate();
        entity.markAsNotDeleted();

        return entity;
    }

    public CategoryResponseDTO toResponseDTO(Category entity) {
        return new CategoryResponseDTO(
                entity.getId(),
                entity.isActive(),
                entity.isDeleted(),
                entity.getCreatedAt(),
                entity.getUpdatedAt(),
                entity.getObservations(),
                entity.getName(),
                entity.getDescription(),
                entity.getAbbreviation(),
                entity.getParentCategoryId()
        );
    }

    public CategoryListResponseDTO toListResponseDTO(Category entity) {
        return new CategoryListResponseDTO(
                entity.getId(),
                entity.getParentCategoryId(),
                entity.getName(),
                entity.getAbbreviation(),
                entity.isActive(),
                entity.isDeleted()
        );
    }
}