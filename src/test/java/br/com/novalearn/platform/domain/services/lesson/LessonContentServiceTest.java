package br.com.novalearn.platform.domain.services.lesson;

import br.com.novalearn.platform.api.dtos.lesson.content.LessonContentCreateRequestDTO;
import br.com.novalearn.platform.api.dtos.lesson.content.LessonContentResponseDTO;
import br.com.novalearn.platform.api.mappers.lesson.LessonContentMapper;
import br.com.novalearn.platform.core.exception.business.ResourceNotFoundException;
import br.com.novalearn.platform.core.exception.business.ValidationException;
import br.com.novalearn.platform.domain.entities.lesson.Lesson;
import br.com.novalearn.platform.domain.entities.lesson.LessonContent;
import br.com.novalearn.platform.domain.repositories.lesson.LessonContentRepository;
import br.com.novalearn.platform.domain.repositories.lesson.LessonRepository;
import br.com.novalearn.platform.infra.time.provider.TimeProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.*;

import static org.assertj.core.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class LessonContentServiceTest {
    @Mock
    private LessonContentRepository lessonContentRepository;

    @Mock
    private LessonRepository lessonRepository;

    @Mock
    private LessonContentMapper lessonContentMapper;

    @Mock
    private TimeProvider timeProvider;

    @InjectMocks
    private LessonContentService lessonContentService;

    private Lesson lesson;
    private LessonContent lessonContent;

    @BeforeEach
    void setup() {
        lesson = mock(Lesson.class);
        lessonContent = mock(LessonContent.class);

        lenient().when(timeProvider.now()).thenReturn(LocalDateTime.now());
    }

    @Test
    void should_create_lesson_content() {
        Long lessonId = 3L;
        Long userId = 5L;

        LessonContentCreateRequestDTO dto = new LessonContentCreateRequestDTO();

        dto.setVideoUrl("video");
        dto.setMainContent(true);
        dto.setHasQuiz(true);

        when(lessonRepository.findById(lessonId)).thenReturn(Optional.of(lesson));

        when(lessonContentRepository
                .existsByLessonIdAndMainContentTrue(lessonId))
                .thenReturn(false);

        try (MockedStatic<LessonContent> mocked = mockStatic(LessonContent.class)) {
            mocked.when(() ->
                    LessonContent.create(
                            eq(lesson),
                            any(),
                            any(),
                            any(),
                            any(),
                            anyBoolean(),
                            anyBoolean(),
                            any(),
                            eq(userId),
                            any()
                    )
            ).thenReturn(lessonContent);

            when(lessonContentRepository.save(lessonContent)).thenReturn(lessonContent);

            LessonContentResponseDTO response = mock(LessonContentResponseDTO.class);

            when(lessonContentMapper.toResponseDTO(lessonContent))
                    .thenReturn(response);

            LessonContentResponseDTO result = lessonContentService.create(lessonId, dto, userId);

            assertThat(result).isEqualTo(response);

            verify(lesson).ensureNotDeleted();
        }
    }

    @Test
    void should_throw_when_lesson_not_found_on_create() {
        when(lessonRepository.findById(3L)).thenReturn(Optional.empty());

        LessonContentCreateRequestDTO dto = new LessonContentCreateRequestDTO();

        assertThatThrownBy(() -> lessonContentService.create(3L, dto, 5L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void should_throw_when_lesson_deleted_on_create() {
        when(lessonRepository.findById(3L)).thenReturn(Optional.of(lesson));

        doThrow(new ValidationException("deleted")).when(lesson).ensureNotDeleted();

        LessonContentCreateRequestDTO dto = new LessonContentCreateRequestDTO();

        assertThatThrownBy(() -> lessonContentService.create(3L, dto, 5L))
                .isInstanceOf(ValidationException.class);
    }

    @Test
    void should_throw_when_main_content_exists() {
        Long lessonId = 3L;

        LessonContentCreateRequestDTO dto = new LessonContentCreateRequestDTO();

        dto.setMainContent(true);

        when(lessonRepository.findById(lessonId)).thenReturn(Optional.of(lesson));

        when(lessonContentRepository.existsByLessonIdAndMainContentTrue(lessonId)).thenReturn(true);

        assertThatThrownBy(() -> lessonContentService.create(lessonId, dto, 5L))
                .isInstanceOf(ValidationException.class);
    }

    @Test
    void should_find_content_by_lesson() {
        Long lessonId = 3L;

        when(lessonRepository.findById(lessonId)).thenReturn(Optional.of(lesson));

        when(lessonContentRepository.findByLessonIdAndDeletedFalse(lessonId))
                .thenReturn(Optional.of(lessonContent));

        LessonContentResponseDTO response = mock(LessonContentResponseDTO.class);

        when(lessonContentMapper.toResponseDTO(lessonContent)).thenReturn(response);

        LessonContentResponseDTO result = lessonContentService.findByLesson(lessonId);

        assertThat(result).isEqualTo(response);

        verify(lesson).ensureNotDeleted();
    }

    @Test
    void should_throw_when_lesson_not_found_on_find() {
        when(lessonRepository.findById(3L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> lessonContentService.findByLesson(3L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void should_throw_when_content_not_found() {
        when(lessonRepository.findById(3L)).thenReturn(Optional.of(lesson));

        when(lessonContentRepository.findByLessonIdAndDeletedFalse(3L))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> lessonContentService.findByLesson(3L))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}