package br.com.novalearn.platform.domain.services.user;

import br.com.novalearn.platform.api.dtos.user.lessonprogress.UserLessonProgressListResponseDTO;
import br.com.novalearn.platform.api.dtos.user.lessonprogress.UserLessonProgressResponseDTO;
import br.com.novalearn.platform.api.mappers.user.UserLessonProgressMapper;
import br.com.novalearn.platform.core.exception.business.InvalidStateException;
import br.com.novalearn.platform.core.exception.business.ResourceNotFoundException;
import br.com.novalearn.platform.core.exception.business.ValidationException;
import br.com.novalearn.platform.domain.entities.user.UserLessonProgress;
import br.com.novalearn.platform.domain.repositories.user.UserLessonProgressRepository;
import br.com.novalearn.platform.infra.time.provider.TimeProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserLessonProgressServiceTest {
    @Mock
    private UserLessonProgressRepository repository;

    @Mock
    private UserLessonProgressMapper mapper;

    @Mock
    private TimeProvider timeProvider;

    @InjectMocks
    private UserLessonProgressService service;

    @InjectMocks
    private MyLessonProgressService myLessonProgressService;

    private UserLessonProgress progress;

    @BeforeEach
    void setup() {
        progress = mock(UserLessonProgress.class);
        lenient().when(timeProvider.now()).thenReturn(LocalDateTime.now());
    }

    @Test
    void should_find_by_id() {
        when(repository.findById(1L)).thenReturn(Optional.of(progress));

        UserLessonProgressResponseDTO response = mock(UserLessonProgressResponseDTO.class);

        when(mapper.toResponseDTO(progress)).thenReturn(response);

        UserLessonProgressResponseDTO result = service.findById(1L);

        assertThat(result).isEqualTo(response);

        verify(progress).ensureNotDeleted();
    }

    @Test
    void should_throw_when_not_found() {
        when(repository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.findById(1L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void should_throw_when_deleted() {
        when(repository.findById(1L)).thenReturn(Optional.of(progress));

        doThrow(new ValidationException("deleted")).when(progress).ensureNotDeleted();

        assertThatThrownBy(() -> service.findById(1L))
                .isInstanceOf(ValidationException.class);
    }

    @Test
    void should_list_all_active() {
        List<UserLessonProgress> list = List.of(progress);

        when(repository.findAllByDeletedFalse()).thenReturn(list);

        UserLessonProgressListResponseDTO dto = mock(UserLessonProgressListResponseDTO.class);

        when(mapper.toListResponseDTO(progress)).thenReturn(dto);

        List<UserLessonProgressListResponseDTO> result = service.listAllActive();

        assertThat(result).hasSize(1);
        assertThat(result.getFirst()).isEqualTo(dto);
    }

    @Test
    void should_list_my_progress() {
        Long userId = 5L;

        when(repository.findAllByUserIdAndDeletedFalse(userId)).thenReturn(List.of(progress));

        UserLessonProgressListResponseDTO dto = mock(UserLessonProgressListResponseDTO.class);

        when(mapper.toListResponseDTO(progress)).thenReturn(dto);

        List<UserLessonProgressListResponseDTO> result = myLessonProgressService.listMyProgress(userId);

        assertThat(result).hasSize(1);
        assertThat(result.getFirst()).isEqualTo(dto);
    }

    @Test
    void should_complete_lesson() {
        Long userId = 5L;
        Long lessonId = 3L;

        when(repository.findByUserIdAndLessonIdAndDeletedFalse(userId,lessonId))
                .thenReturn(Optional.of(progress));

        when(progress.isCompleted()).thenReturn(false);

        UserLessonProgressResponseDTO dto = mock(UserLessonProgressResponseDTO.class);

        when(mapper.toResponseDTO(progress)).thenReturn(dto);

        UserLessonProgressResponseDTO result = myLessonProgressService.completeLesson(lessonId, userId);

        verify(progress).updateProgress(eq(100), any());

        verify(progress).auditUpdate(eq(userId), any());

        assertThat(result).isEqualTo(dto);
    }

    @Test
    void should_not_update_when_already_completed() {
        Long userId = 5L;
        Long lessonId = 3L;

        when(repository.findByUserIdAndLessonIdAndDeletedFalse(userId, lessonId))
                .thenReturn(Optional.of(progress));

        when(progress.isCompleted()).thenReturn(true);

        UserLessonProgressResponseDTO dto = mock(UserLessonProgressResponseDTO.class);

        when(mapper.toResponseDTO(progress)).thenReturn(dto);

        UserLessonProgressResponseDTO result = myLessonProgressService.completeLesson(lessonId, userId);

        verify(progress, never()).updateProgress(anyInt(), any());
        verify(progress, never()).auditUpdate(any(), any());

        assertThat(result).isEqualTo(dto);
    }

    @Test
    void should_throw_when_not_started() {
        when(repository.findByUserIdAndLessonIdAndDeletedFalse(5L, 3L))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> myLessonProgressService.completeLesson(3L, 5L))
                .isInstanceOf(InvalidStateException.class);
    }
}