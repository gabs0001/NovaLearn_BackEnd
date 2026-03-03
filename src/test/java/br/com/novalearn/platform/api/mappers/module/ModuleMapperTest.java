package br.com.novalearn.platform.api.mappers.module;

import br.com.novalearn.platform.api.dtos.module.ModuleCreateRequestDTO;
import br.com.novalearn.platform.api.dtos.module.ModuleListResponseDTO;
import br.com.novalearn.platform.api.dtos.module.ModuleResponseDTO;
import br.com.novalearn.platform.domain.entities.course.Course;
import br.com.novalearn.platform.domain.entities.module.Module;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static br.com.novalearn.platform.factories.dtos.module.ModuleTestFactory.moduleCreateRequest;
import static br.com.novalearn.platform.factories.entities.course.CreateCourseFactory.createInitializedCourse;
import static br.com.novalearn.platform.factories.entities.module.CreateModuleFactory.createInitializedModule;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class ModuleMapperTest {
    private LocalDateTime now;
    private ModuleMapper mapper;
    private Course course;
    private Module module;

    @BeforeEach
    void setUp() {
        now = LocalDateTime.now();
        mapper = new ModuleMapper();
        course = createInitializedCourse();
        module = createInitializedModule(course);
    }

    @Test
    void should_map_create_dto_to_entity() {
        ModuleCreateRequestDTO dto = moduleCreateRequest();

        Module module = mapper.toEntity(dto, course);

        assertThat(module).isNotNull();

        assertThat(module.getCourse()).isEqualTo(course);

        assertThat(module.getName()).isEqualTo(dto.getName());
        assertThat(module.getDescription()).isEqualTo(dto.getDescription());
        assertThat(module.getSequence()).isEqualTo(dto.getSequence());
        assertThat(module.getObservations()).isEqualTo(dto.getObservations());

        assertThat(module.isActive()).isTrue();
        assertThat(module.isDeleted()).isFalse();
    }

    @Test
    void should_map_entity_to_response_dto() {
        module.auditCreate(1L, now.minusDays(1));
        module.auditUpdate(2L, now);

        ModuleResponseDTO dto = mapper.toResponseDTO(module);

        assertThat(dto).isNotNull();

        assertThat(dto.getId()).isEqualTo(module.getId());

        assertThat(dto.getActive()).isEqualTo(module.isActive());
        assertThat(dto.getDeleted()).isEqualTo(module.isDeleted());

        assertThat(dto.getCreatedAt()).isEqualTo(module.getCreatedAt());
        assertThat(dto.getUpdatedAt()).isEqualTo(module.getUpdatedAt());

        assertThat(dto.getObservations()).isEqualTo(module.getObservations());

        assertThat(dto.getCourseId()).isEqualTo(course.getId());

        assertThat(dto.getName()).isEqualTo(module.getName());
        assertThat(dto.getDescription()).isEqualTo(module.getDescription());
        assertThat(dto.getSequence()).isEqualTo(module.getSequence());

        assertThat(dto.getCreatedBy()).isEqualTo(module.getCreatedBy());
        assertThat(dto.getUpdatedBy()).isEqualTo(module.getUpdatedBy());
    }

    @Test
    void should_map_entity_to_list_response_dto() {
        ModuleListResponseDTO dto = mapper.toListResponseDTO(module);

        assertThat(dto).isNotNull();

        assertThat(dto.getId()).isEqualTo(module.getId());

        assertThat(dto.getCourseId()).isEqualTo(course.getId());

        assertThat(dto.getName()).isEqualTo(module.getName());
        assertThat(dto.getSequence()).isEqualTo(module.getSequence());

        assertThat(dto.getActive()).isEqualTo(module.isActive());
        assertThat(dto.getDeleted()).isEqualTo(module.isDeleted());
    }
}