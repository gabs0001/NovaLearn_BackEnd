package br.com.novalearn.platform.api.mappers.module;

import br.com.novalearn.platform.api.dtos.module.ModuleCreateRequestDTO;
import br.com.novalearn.platform.api.dtos.module.ModuleListResponseDTO;
import br.com.novalearn.platform.api.dtos.module.ModuleResponseDTO;
import br.com.novalearn.platform.domain.entities.course.Course;
import br.com.novalearn.platform.domain.entities.module.Module;
import org.springframework.stereotype.Component;

@Component
public class ModuleMapper {
    public Module toEntity(ModuleCreateRequestDTO dto, Course course) {
        return Module.create(
                course,
                dto.getName(),
                dto.getDescription(),
                dto.getSequence(),
                dto.getObservations()
        );
    }

    public ModuleResponseDTO toResponseDTO(Module entity) {
        return new ModuleResponseDTO(
                entity.getId(),
                entity.isActive(),
                entity.isDeleted(),
                entity.getCreatedAt(),
                entity.getUpdatedAt(),
                entity.getObservations(),
                entity.getCourse() != null ? entity.getCourse().getId() : null,
                entity.getName(),
                entity.getDescription(),
                entity.getSequence(),
                entity.getCreatedBy(),
                entity.getUpdatedBy()
        );
    }

    public ModuleListResponseDTO toListResponseDTO(Module entity) {
        return new ModuleListResponseDTO(
                entity.getId(),
                entity.getCourse() != null ? entity.getCourse().getId() : null,
                entity.getName(),
                entity.getSequence(),
                entity.isActive(),
                entity.isDeleted()
        );
    }
}