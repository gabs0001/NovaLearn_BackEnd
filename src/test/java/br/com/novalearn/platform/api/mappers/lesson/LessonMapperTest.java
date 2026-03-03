package br.com.novalearn.platform.api.mappers.lesson;

import br.com.novalearn.platform.api.dtos.lesson.LessonCreateRequestDTO;
import br.com.novalearn.platform.api.dtos.lesson.LessonListResponseDTO;
import br.com.novalearn.platform.api.dtos.lesson.LessonResponseDTO;
import br.com.novalearn.platform.domain.entities.lesson.Lesson;
import br.com.novalearn.platform.domain.entities.module.Module;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static br.com.novalearn.platform.factories.dtos.lesson.LessonTestFactory.lessonCreateRequest;
import static br.com.novalearn.platform.factories.entities.course.CreateCourseFactory.createInitializedCourse;
import static br.com.novalearn.platform.factories.entities.lesson.CreateLessonFactory.createInitializedLesson;
import static br.com.novalearn.platform.factories.entities.module.CreateModuleFactory.createInitializedModule;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class LessonMapperTest {
    private LessonMapper mapper;
    private Module module;
    private Lesson lesson;

    @BeforeEach
    void setUp() {
        mapper = new LessonMapper();
        module = createInitializedModule(createInitializedCourse());
        lesson = createInitializedLesson();
    }

    @Test
    void should_map_create_dto_to_entity() {
        LessonCreateRequestDTO dto = lessonCreateRequest();

        Lesson lesson = mapper.toEntity(dto, module);

        assertThat(lesson).isNotNull();

        assertThat(lesson.getModule()).isEqualTo(module);
        assertThat(lesson.getName()).isEqualTo(dto.getName());
        assertThat(lesson.getDescription()).isEqualTo(dto.getDescription());
        assertThat(lesson.getSequence()).isEqualTo(dto.getSequence());
        assertThat(lesson.getDurationSeconds()).isEqualTo(dto.getDurationSeconds());
        assertThat(lesson.isRequireCompletion()).isEqualTo(dto.getRequireCompletion());
        assertThat(lesson.isVisible()).isEqualTo(dto.getVisible());
        assertThat(lesson.getPreviewUrl()).isEqualTo(dto.getPreviewUrl());
        assertThat(lesson.getNotes()).isEqualTo(dto.getNotes());
        assertThat(lesson.getObservations()).isEqualTo(dto.getObservations());
    }

    @Test
    void should_map_entity_to_response_dto() {
        LessonResponseDTO dto = mapper.toResponseDTO(lesson);

        assertThat(dto).isNotNull();

        assertThat(dto.getId()).isEqualTo(lesson.getId());
        assertThat(dto.getActive()).isEqualTo(lesson.isActive());
        assertThat(dto.getDeleted()).isEqualTo(lesson.isDeleted());
        assertThat(dto.getCreatedAt()).isEqualTo(lesson.getCreatedAt());
        assertThat(dto.getUpdatedAt()).isEqualTo(lesson.getUpdatedAt());
        assertThat(dto.getObservations()).isEqualTo(lesson.getObservations());

        assertThat(dto.getModuleId()).isEqualTo(module.getId());

        assertThat(dto.getName()).isEqualTo(lesson.getName());
        assertThat(dto.getDescription()).isEqualTo(lesson.getDescription());
        assertThat(dto.getSequence()).isEqualTo(lesson.getSequence());
        assertThat(dto.getDurationSeconds()).isEqualTo(lesson.getDurationSeconds());
        assertThat(dto.getRequireCompletion()).isEqualTo(lesson.isRequireCompletion());
        assertThat(dto.getVisible()).isEqualTo(lesson.isVisible());
        assertThat(dto.getPreviewUrl()).isEqualTo(lesson.getPreviewUrl());
        assertThat(dto.getNotes()).isEqualTo(lesson.getNotes());

        assertThat(dto.getCreatedBy()).isEqualTo(lesson.getCreatedBy());
        assertThat(dto.getUpdatedBy()).isEqualTo(lesson.getUpdatedBy());
    }

    @Test
    void should_map_entity_to_list_response_dto() {
        LessonListResponseDTO dto = mapper.toListResponseDTO(lesson);

        assertThat(dto).isNotNull();

        assertThat(dto.getId()).isEqualTo(lesson.getId());
        assertThat(dto.getModuleId()).isEqualTo(module.getId());
        assertThat(dto.getName()).isEqualTo(lesson.getName());
        assertThat(dto.getSequence()).isEqualTo(lesson.getSequence());
        assertThat(dto.getDurationSeconds()).isEqualTo(lesson.getDurationSeconds());
        assertThat(dto.getVisible()).isEqualTo(lesson.isVisible());
        assertThat(dto.getActive()).isEqualTo(lesson.isActive());
    }
}