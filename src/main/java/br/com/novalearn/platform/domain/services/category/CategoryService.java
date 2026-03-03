package br.com.novalearn.platform.domain.services.category;

import br.com.novalearn.platform.api.dtos.category.*;
import br.com.novalearn.platform.api.mappers.category.CategoryMapper;
import br.com.novalearn.platform.core.exception.business.*;
import br.com.novalearn.platform.domain.entities.category.Category;
import br.com.novalearn.platform.domain.repositories.category.CategoryRepository;
import br.com.novalearn.platform.domain.services.BaseCrudService;
import br.com.novalearn.platform.infra.time.provider.TimeProvider;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService extends BaseCrudService<Category> {
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    public CategoryService(
            CategoryRepository categoryRepository,
            CategoryMapper categoryMapper,
            TimeProvider timeProvider
    ) {
        super(categoryRepository, "Category", timeProvider);
        this.categoryRepository = categoryRepository;
        this.categoryMapper = categoryMapper;
    }

    @Transactional
    public CategoryResponseDTO create(Long userId, CategoryCreateRequestDTO dto) {
        if(categoryRepository.existsByNameIgnoreCaseAndDeletedFalse(dto.getName().trim())) {
            throw new ConflictException("Category with this name already exists.");
        }

        Category entity = categoryMapper.toEntity(dto, userId);

        applyAuditCreate(entity, userId);

        return categoryMapper.toResponseDTO(categoryRepository.save(entity));
    }

    @Transactional
    public CategoryResponseDTO update(Long id, CategoryUpdateRequestDTO dto, Long userId) {
        Category entity = findEntityOrThrow(id);
        entity.ensureNotDeleted();

        if(dto.getName() != null &&
                !dto.getName().equalsIgnoreCase(entity.getName()) &&
                categoryRepository.existsByNameIgnoreCaseAndDeletedFalse(dto.getName().trim())
        ) {
            throw new ConflictException("Another category with this name already exists.");
        }

        if(dto.getName() != null) entity.setName(dto.getName().trim());
        if(dto.getDescription() != null) entity.setDescription(dto.getDescription());
        if(dto.getAbbreviation() != null) entity.setAbbreviation(dto.getAbbreviation());
        if(dto.getObservations() != null) entity.setObservations(dto.getObservations());

        if(dto.getParentCategoryId() != null) {
            Category parent = categoryRepository.findByIdAndDeletedFalse(dto.getParentCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Parent category not found."));
            entity.defineParent(parent);
        }

        applyAuditUpdate(entity, userId);

        return categoryMapper.toResponseDTO(categoryRepository.save(entity));
    }

    @Transactional
    public CategoryResponseDTO findById(Long id) {
        Category entity = findEntityOrThrow(id);
        entity.ensureNotDeleted();

        return categoryMapper.toResponseDTO(entity);
    }

    @Transactional
    public List<CategoryListResponseDTO> listAllActive() {
        return categoryRepository.findAllByDeletedFalse()
                .stream()
                .map(categoryMapper::toListResponseDTO)
                .toList();
    }

    @Transactional
    public List<CategoryListResponseDTO> listAll() {
        return categoryRepository.findAll()
                .stream()
                .map(categoryMapper::toListResponseDTO)
                .toList();
    }

    @Transactional
    public boolean existsByName(String name) {
        if(name == null || name.isBlank()) return false;
        return categoryRepository.existsByNameIgnoreCaseAndDeletedFalse(name.trim());
    }

    @Transactional
    public List<CategoryListResponseDTO> listForSelect() {
        return categoryRepository.findAllByActiveTrueAndDeletedFalse()
                .stream()
                .map(categoryMapper::toListResponseDTO)
                .toList();
    }
}