package br.com.novalearn.platform.api.mappers.lesson;

import br.com.novalearn.platform.api.dtos.lesson.content.LessonContentListResponseDTO;
import br.com.novalearn.platform.api.dtos.lesson.content.LessonContentResponseDTO;
import br.com.novalearn.platform.domain.entities.lesson.Lesson;
import br.com.novalearn.platform.domain.entities.lesson.LessonContent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static br.com.novalearn.platform.factories.entities.lesson.CreateContentFactory.createInitializedContent;
import static br.com.novalearn.platform.factories.entities.lesson.CreateLessonFactory.createInitializedLesson;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class LessonContentMapperTest {
    private LessonContentMapper mapper;
    private Lesson lesson;
    private LessonContent content;

    @BeforeEach
    void setUp() {
        mapper = new LessonContentMapper();
        lesson = createInitializedLesson();
        content = createInitializedContent(lesson, LocalDateTime.now());
    }

    @Test
    void should_map_entity_to_response_dto() {
        LessonContentResponseDTO dto = mapper.toResponseDTO(content);

        assertThat(dto).isNotNull();

        assertThat(dto.getId()).isEqualTo(content.getId());
        assertThat(dto.getActive()).isEqualTo(content.isActive());
        assertThat(dto.getDeleted()).isEqualTo(content.isDeleted());

        assertThat(dto.getCreatedAt()).isEqualTo(content.getCreatedAt());
        assertThat(dto.getUpdatedAt()).isEqualTo(content.getUpdatedAt());
        assertThat(dto.getObservations()).isEqualTo(content.getObservations());

        assertThat(dto.getLessonId()).isEqualTo(lesson.getId());

        assertThat(dto.getVideoUrl()).isEqualTo(content.getVideoUrl());
        assertThat(dto.getTranscriptUrl()).isEqualTo(content.getTranscriptUrl());
        assertThat(dto.getMaterialUrl()).isEqualTo(content.getMaterialUrl());
        assertThat(dto.getContent()).isEqualTo(content.getContent());

        assertThat(dto.getHasQuiz()).isEqualTo(content.isHasQuiz());
        assertThat(dto.getMainContent()).isEqualTo(content.isMainContent());

        assertThat(dto.getCreatedBy()).isEqualTo(content.getCreatedBy());
        assertThat(dto.getUpdatedBy()).isEqualTo(content.getUpdatedBy());
    }

    @Test
    void should_map_entity_to_list_response_dto() {
        LessonContentListResponseDTO dto = mapper.toListResponseDTO(content);

        assertThat(dto).isNotNull();

        assertThat(dto.getId()).isEqualTo(content.getId());
        assertThat(dto.getLessonId()).isEqualTo(lesson.getId());

        assertThat(dto.getHasQuiz()).isEqualTo(content.isHasQuiz());
        assertThat(dto.getMainContent()).isEqualTo(content.isMainContent());

        assertThat(dto.getActive()).isEqualTo(content.isActive());
        assertThat(dto.getDeleted()).isEqualTo(content.isDeleted());
    }
}