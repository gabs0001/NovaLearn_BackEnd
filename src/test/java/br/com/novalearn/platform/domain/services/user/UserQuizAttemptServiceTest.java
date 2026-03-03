package br.com.novalearn.platform.domain.services.user;

import br.com.novalearn.platform.api.dtos.user.quizattempt.UserQuizAttemptCreateRequestDTO;
import br.com.novalearn.platform.api.dtos.user.quizattempt.UserQuizAttemptListResponseDTO;
import br.com.novalearn.platform.api.dtos.user.quizattempt.UserQuizAttemptResponseDTO;
import br.com.novalearn.platform.api.dtos.user.quizattempt.UserQuizAttemptUpdateRequestDTO;
import br.com.novalearn.platform.api.mappers.user.UserQuizAttemptMapper;
import br.com.novalearn.platform.core.exception.business.ResourceNotFoundException;
import br.com.novalearn.platform.core.exception.business.ValidationException;
import br.com.novalearn.platform.domain.entities.quiz.Quiz;
import br.com.novalearn.platform.domain.entities.user.User;
import br.com.novalearn.platform.domain.entities.user.UserQuizAttempt;
import br.com.novalearn.platform.domain.enums.QuizAttemptStatus;
import br.com.novalearn.platform.domain.repositories.quiz.QuizRepository;
import br.com.novalearn.platform.domain.repositories.user.UserQuizAttemptRepository;
import br.com.novalearn.platform.domain.services.auth.AuthService;
import br.com.novalearn.platform.infra.time.provider.TimeProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserQuizAttemptServiceTest {
    @Mock
    private AuthService authService;

    @Mock
    private UserQuizAttemptRepository userQuizAttemptRepository;

    @Mock
    private QuizRepository quizRepository;

    @Mock
    private UserQuizAttemptMapper userQuizAttemptMapper;

    @Mock
    private TimeProvider timeProvider;

    @InjectMocks
    private UserQuizAttemptService service;

    private UserQuizAttemptCreateRequestDTO dto;
    private Quiz quiz;
    private User user;
    private UserQuizAttempt attempt;

    private final Long userId = 5L;
    private final Long quizId = 9L;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.clearContext();
        dto = new UserQuizAttemptCreateRequestDTO();
        quiz = mock(Quiz.class);
        user = mock(User.class);
        attempt = mock(UserQuizAttempt.class);
    }

    @Test
    void start_attempt_should_create_attempt() {
        dto.setQuizId(quizId);

        when(authService.getAuthenticatedUserEntity()).thenReturn(user);

        when(quizRepository.findById(quizId)).thenReturn(Optional.of(quiz));

        when(quiz.getId()).thenReturn(quizId);
        when(quiz.getMaxAttempts()).thenReturn(null);

        when(userQuizAttemptRepository.existsByUserIdAndQuizIdAndStatusAndDeletedFalse(userId, quizId, QuizAttemptStatus.STARTED))
                .thenReturn(false);

        when(userQuizAttemptRepository.countByUserIdAndQuizIdAndDeletedFalse(userId, quizId)).thenReturn(0L);

        when(timeProvider.now()).thenReturn(LocalDateTime.now());

        when(userQuizAttemptRepository.save(any())).thenReturn(attempt);
        when(userQuizAttemptMapper.toResponseDTO(attempt)).thenReturn(new UserQuizAttemptResponseDTO());

        UserQuizAttemptResponseDTO result = service.startAttempt(dto, userId);

        assertNotNull(result);
    }

    @Test
    void start_attempt_should_throw_when_active_attempt_exists() {
        dto.setQuizId(quizId);

        when(authService.getAuthenticatedUserEntity()).thenReturn(user);
        when(quizRepository.findById(quizId)).thenReturn(Optional.of(quiz));

        when(quiz.getId()).thenReturn(quizId);
        when(quiz.getMaxAttempts()).thenReturn(2);

        when(userQuizAttemptRepository.existsByUserIdAndQuizIdAndStatusAndDeletedFalse(userId, quizId, QuizAttemptStatus.STARTED))
                .thenReturn(false);

        when(userQuizAttemptRepository.countByUserIdAndQuizIdAndDeletedFalse(userId, quizId)).thenReturn(2L);

        assertThrows(
                ValidationException.class,
                () -> service.startAttempt(dto, userId)
        );
    }

    @Test
    void finish_attempt_should_update_attempt() {
        UserQuizAttemptUpdateRequestDTO dto = new UserQuizAttemptUpdateRequestDTO();

        dto.setScore(BigDecimal.valueOf(80));
        dto.setMaxScore(BigDecimal.valueOf(100));

        when(userQuizAttemptRepository.findByIdAndUserIdAndDeletedFalse(7L, userId)).thenReturn(Optional.of(attempt));

        when(timeProvider.now()).thenReturn(LocalDateTime.now());

        when(userQuizAttemptRepository.save(attempt)).thenReturn(attempt);

        when(userQuizAttemptMapper.toResponseDTO(attempt)).thenReturn(new UserQuizAttemptResponseDTO());

        UserQuizAttemptResponseDTO result = service.finishAttempt(7L, dto, userId);

        verify(attempt).finish(
                eq(dto.getScore()),
                eq(dto.getMaxScore()),
                any(),
                eq(userId),
                any()
        );

        assertNotNull(result);
    }

    @Test
    void finish_attempt_should_throw_when_not_found() {
        when(userQuizAttemptRepository.findByIdAndUserIdAndDeletedFalse(
                7L, userId
        )).thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> service.finishAttempt(
                        7L,
                        new UserQuizAttemptUpdateRequestDTO(),
                        userId
                )
        );
    }

    @Test
    void list_my_attempts_should_return_list() {
        when(userQuizAttemptRepository
                .findAllByUserIdAndDeletedFalseOrderByStartedAtDesc(userId))
                .thenReturn(List.of(mock(UserQuizAttempt.class)));

        when(userQuizAttemptMapper.toListResponseDTO(any())).thenReturn(new UserQuizAttemptListResponseDTO());

        List<UserQuizAttemptListResponseDTO> result = service.listMyAttempts(userId);

        assertEquals(1, result.size());
    }

    @Test
    void find_my_attempt_by_id_should_return() {
        when(userQuizAttemptRepository
                .findByIdAndUserIdAndDeletedFalse(7L, userId))
                .thenReturn(Optional.of(attempt));

        when(userQuizAttemptMapper.toResponseDTO(attempt)).thenReturn(new UserQuizAttemptResponseDTO());

        UserQuizAttemptResponseDTO result = service.findMyAttemptById(7L, userId);

        assertNotNull(result);
    }

    @Test
    void delete_my_attempt_should_delete() {
        when(userQuizAttemptRepository.findByIdAndUserIdAndDeletedFalse(7L, userId)).thenReturn(Optional.of(attempt));

        service.deleteMyAttempt(7L, userId);

        verify(attempt).delete();
    }

    @Test
    void restore_my_attempt_should_restore() {
        when(userQuizAttemptRepository
                .findByIdAndUserId(7L, userId))
                .thenReturn(Optional.of(attempt));

        service.restoreMyAttempt(7L, userId);

        verify(attempt).restore();
    }

    @Test
    void list_my_attempts_by_quiz_should_return_list() {
        when(userQuizAttemptRepository
                .findAllByUserIdAndQuizIdAndDeletedFalse(userId, quizId))
                .thenReturn(List.of(mock(UserQuizAttempt.class)));

        when(userQuizAttemptMapper.toListResponseDTO(any())).thenReturn(new UserQuizAttemptListResponseDTO());

        List<UserQuizAttemptListResponseDTO> result = service.listMyAttemptsByQuiz(quizId, userId);

        assertEquals(1, result.size());
    }
}