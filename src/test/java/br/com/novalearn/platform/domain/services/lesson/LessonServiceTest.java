package br.com.novalearn.platform.domain.services.lesson;

import br.com.novalearn.platform.api.dtos.lesson.LessonCreateRequestDTO;
import br.com.novalearn.platform.api.dtos.lesson.LessonListResponseDTO;
import br.com.novalearn.platform.api.dtos.lesson.LessonResponseDTO;
import br.com.novalearn.platform.api.dtos.lesson.LessonUpdateRequestDTO;
import br.com.novalearn.platform.api.mappers.lesson.LessonMapper;
import br.com.novalearn.platform.core.exception.business.ResourceNotFoundException;
import br.com.novalearn.platform.core.exception.business.ValidationException;
import br.com.novalearn.platform.domain.entities.lesson.Lesson;
import br.com.novalearn.platform.domain.entities.module.Module;
import br.com.novalearn.platform.domain.repositories.lesson.LessonRepository;
import br.com.novalearn.platform.domain.repositories.module.ModuleRepository;
import br.com.novalearn.platform.infra.time.provider.TimeProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.*;

import static org.assertj.core.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class LessonServiceTest {
    @Mock
    private LessonRepository lessonRepository;

    @Mock
    private ModuleRepository moduleRepository;

    @Mock
    private LessonMapper lessonMapper;

    @Mock
    private TimeProvider timeProvider;

    @InjectMocks
    private LessonService lessonService;

    private Lesson lesson;
    private Module module;
    private LessonCreateRequestDTO dto;

    @BeforeEach
    void setup() {
        SecurityContextHolder.clearContext();
        lesson = mock(Lesson.class);
        module = mock(Module.class);
        dto = new LessonCreateRequestDTO();

        lenient().when(module.getId()).thenReturn(6L);
        lenient().when(timeProvider.now()).thenReturn(LocalDateTime.now());
    }

    @Test
    void should_create_lesson() {
        dto.setModuleId(6L);
        dto.setName("Intro");
        dto.setSequence(1);

        when(moduleRepository.findById(6L)).thenReturn(Optional.of(module));

        when(lessonRepository
                .existsByModuleIdAndSequence(6L, 1))
                .thenReturn(false);

        when(lessonRepository
                .existsByModuleIdAndNameIgnoreCase(6L, "Intro"))
                .thenReturn(false);

        when(lessonMapper.toEntity(dto, module)).thenReturn(lesson);

        when(lessonRepository.save(lesson)).thenReturn(lesson);

        LessonResponseDTO response = mock(LessonResponseDTO.class);

        when(lessonMapper.toResponseDTO(lesson)).thenReturn(response);

        LessonResponseDTO result = lessonService.create(5L, dto);

        assertThat(result).isEqualTo(response);
    }

    @Test
    void should_throw_when_module_not_found_on_create() {
        dto.setModuleId(10L);

        when(moduleRepository.findById(10L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> lessonService.create(5L, dto))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void should_throw_when_sequence_exists_on_create() {
        dto.setModuleId(6L);
        dto.setSequence(1);

        when(moduleRepository.findById(6L)).thenReturn(Optional.of(module));

        when(lessonRepository
                .existsByModuleIdAndSequence(6L, 1))
                .thenReturn(true);

        assertThatThrownBy(() -> lessonService.create(5L, dto))
                .isInstanceOf(ValidationException.class);
    }

    @Test
    void should_update_lesson() {
        LessonUpdateRequestDTO dto = new LessonUpdateRequestDTO();

        dto.setName("New Name");
        dto.setSequence(2);
        dto.setVisible(true);

        when(lessonRepository.findById(3L)).thenReturn(Optional.of(lesson));

        when(lesson.getName()).thenReturn("Old");
        when(lesson.getSequence()).thenReturn(1);
        when(lesson.getModule()).thenReturn(module);

        when(lessonRepository
                .existsByModuleIdAndNameIgnoreCase(6L, "New Name"))
                .thenReturn(false);

        when(lessonRepository
                .existsByModuleIdAndSequence(6L, 2))
                .thenReturn(false);

        LessonResponseDTO response = mock(LessonResponseDTO.class);

        when(lessonMapper.toResponseDTO(lesson)).thenReturn(response);

        LessonResponseDTO result = lessonService.update(3L, dto, 1L);

        assertThat(result).isEqualTo(response);

        verify(lesson).changeName("New Name");
        verify(lesson).changeSequence(2);
        verify(lesson).changeVisibility(true);
    }

    @Test
    void should_throw_when_name_exists_on_update() {
        LessonUpdateRequestDTO dto = new LessonUpdateRequestDTO();

        dto.setName("Intro");

        when(lessonRepository.findById(3L)).thenReturn(Optional.of(lesson));

        when(lesson.getName()).thenReturn("Old");
        when(lesson.getModule()).thenReturn(module);

        when(lessonRepository
                .existsByModuleIdAndNameIgnoreCase(6L, "Intro"))
                .thenReturn(true);

        assertThatThrownBy(() -> lessonService.update(3L, dto, 5L))
                .isInstanceOf(ValidationException.class);
    }

    @Test
    void should_list_all_active() {
        when(lessonRepository.findAllByDeletedFalse()).thenReturn(List.of(lesson));

        when(lessonMapper.toListResponseDTO(lesson)).thenReturn(mock(LessonListResponseDTO.class));

        List<LessonListResponseDTO> result = lessonService.listAllActive();

        assertThat(result).hasSize(1);
    }

    @Test
    void should_list_by_module() {
        when(moduleRepository.existsById(6L)).thenReturn(true);

        when(lessonRepository
                .findAllByModule_IdAndDeletedFalseOrderBySequenceAsc(6L))
                .thenReturn(List.of(lesson));

        when(lessonMapper.toListResponseDTO(lesson)).thenReturn(mock(LessonListResponseDTO.class));

        List<LessonListResponseDTO> result = lessonService.listByModule(6L);

        assertThat(result).hasSize(1);
    }

    @Test
    void should_throw_when_module_not_found_on_list() {
        when(moduleRepository.existsById(6L)).thenReturn(false);

        assertThatThrownBy(() -> lessonService.listByModule(6L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void should_reorder_lessons() {
        Lesson l1 = mock(Lesson.class);
        Lesson l2 = mock(Lesson.class);

        when(l1.getModule()).thenReturn(module);
        when(l2.getModule()).thenReturn(module);

        when(lessonRepository.findById(3L)).thenReturn(Optional.of(l1));
        when(lessonRepository.findById(4L)).thenReturn(Optional.of(l2));

        lessonService.reorder(6L, List.of(3L, 4L), 5L);

        verify(l1).changeSequence(1);
        verify(l2).changeSequence(2);
    }

    @Test
    void should_check_sequence_exists() {
        when(lessonRepository
                .existsByModuleIdAndSequence(6L, 1))
                .thenReturn(true);

        boolean result = lessonService.existsSequence(6L, 1);

        assertThat(result).isTrue();
    }
}