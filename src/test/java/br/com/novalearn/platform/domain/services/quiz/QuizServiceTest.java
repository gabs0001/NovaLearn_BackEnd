package br.com.novalearn.platform.domain.services.quiz;

import br.com.novalearn.platform.api.dtos.quiz.QuizCreateRequestDTO;
import br.com.novalearn.platform.api.dtos.quiz.QuizListResponseDTO;
import br.com.novalearn.platform.api.dtos.quiz.QuizResponseDTO;
import br.com.novalearn.platform.api.dtos.quiz.QuizUpdateRequestDTO;
import br.com.novalearn.platform.api.dtos.quiz.summary.QuizSummaryResponseDTO;
import br.com.novalearn.platform.api.dtos.user.quizattempt.UserQuizAttemptListResponseDTO;
import br.com.novalearn.platform.api.mappers.quiz.QuizMapper;
import br.com.novalearn.platform.api.mappers.user.UserQuizAttemptMapper;
import br.com.novalearn.platform.core.exception.business.InvalidStateException;
import br.com.novalearn.platform.core.exception.business.ResourceNotFoundException;
import br.com.novalearn.platform.core.exception.business.ValidationException;
import br.com.novalearn.platform.domain.entities.quiz.Quiz;
import br.com.novalearn.platform.domain.entities.module.Module;
import br.com.novalearn.platform.domain.entities.user.User;
import br.com.novalearn.platform.domain.entities.user.UserQuizAttempt;
import br.com.novalearn.platform.domain.repositories.module.ModuleRepository;
import br.com.novalearn.platform.domain.repositories.quiz.QuizQuestionRepository;
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
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class QuizServiceTest {
    @Mock
    private AuthService authService;

    @Mock
    private QuizRepository quizRepository;

    @Mock
    private QuizQuestionRepository quizQuestionRepository;

    @Mock
    private ModuleRepository moduleRepository;

    @Mock
    private UserQuizAttemptRepository userQuizAttemptRepository;

    @Mock
    private UserQuizAttemptMapper userQuizAttemptMapper;

    @Mock
    private QuizMapper quizMapper;

    @Mock
    private TimeProvider timeProvider;

    @InjectMocks
    private QuizService service;

    private Quiz quiz;
    private Module module;
    private QuizCreateRequestDTO dto;

    @BeforeEach
    void setup() {
        SecurityContextHolder.clearContext();
        quiz = mock(Quiz.class);
        module = mock(Module.class);
        lenient().when(module.getId()).thenReturn(6L);
        dto = mock(QuizCreateRequestDTO.class);
    }

    @Test
    void should_create_quiz() {
        when(dto.getModuleId()).thenReturn(6L);
        when(dto.getName()).thenReturn("Quiz 1");
        when(dto.getQtdQuestions()).thenReturn(1);
        when(dto.getMaxAttempts()).thenReturn(3);

        when(moduleRepository.findById(6L)).thenReturn(Optional.of(module));

        when(module.isDeleted()).thenReturn(false);
        when(module.isActive()).thenReturn(true);

        when(quizRepository
                .existsByModuleIdAndNameIgnoreCaseAndDeletedFalse(6L, "Quiz 1"))
                .thenReturn(false);

        when(quizMapper.toEntity(dto, module)).thenReturn(quiz);

        when(quizRepository.save(quiz)).thenReturn(quiz);

        QuizResponseDTO response = mock(QuizResponseDTO.class);

        when(quizMapper.toResponseDTO(quiz)).thenReturn(response);

        QuizResponseDTO result = service.create(dto);

        assertThat(result).isEqualTo(response);
    }

    @Test
    void should_not_create_in_inactive_module() {
        when(dto.getModuleId()).thenReturn(6L);

        when(moduleRepository.findById(6L)).thenReturn(Optional.of(module));

        when(module.isDeleted()).thenReturn(true);

        assertThatThrownBy(() -> service.create(dto))
                .isInstanceOf(InvalidStateException.class);
    }

    @Test
    void should_not_create_with_duplicate_name() {
        when(dto.getModuleId()).thenReturn(6L);
        when(dto.getName()).thenReturn("Quiz");

        when(moduleRepository.findById(6L)).thenReturn(Optional.of(module));

        when(module.isDeleted()).thenReturn(false);
        when(module.isActive()).thenReturn(true);

        when(quizRepository
                .existsByModuleIdAndNameIgnoreCaseAndDeletedFalse(6L, "Quiz"))
                .thenReturn(true);

        assertThatThrownBy(() -> service.create(dto))
                .isInstanceOf(ValidationException.class);
    }

    @Test
    void should_update_quiz() {
        QuizUpdateRequestDTO dto = new QuizUpdateRequestDTO();
        dto.setName("New name");

        when(quizRepository.findById(9L)).thenReturn(Optional.of(quiz));

        lenient().when(quiz.getModule()).thenReturn(module);
        lenient().when(module.getId()).thenReturn(6L);
        lenient().when(quiz.getId()).thenReturn(9L);

        when(quizRepository
                .existsByModuleIdAndNameIgnoreCaseAndIdNotAndDeletedFalse(6L, "New name", 9L))
                .thenReturn(false);

        when(quizRepository.save(quiz)).thenReturn(quiz);

        QuizResponseDTO response = mock(QuizResponseDTO.class);

        when(quizMapper.toResponseDTO(quiz)).thenReturn(response);

        QuizResponseDTO result = service.update(9L, dto, 5L);

        verify(quiz).ensureNotDeleted();
        verify(quiz).update(any(), any(), any(), any(), any(), any(), any(), any());

        assertThat(result).isEqualTo(response);
    }

    @Test
    void should_not_update_with_duplicate_name() {
        QuizUpdateRequestDTO dto = mock(QuizUpdateRequestDTO.class);

        when(dto.getName()).thenReturn("Quiz");

        when(quizRepository.findById(9L)).thenReturn(Optional.of(quiz));

        when(quiz.getModule()).thenReturn(module);
        when(module.getId()).thenReturn(6L);
        when(quiz.getId()).thenReturn(9L);

        when(quizRepository
                .existsByModuleIdAndNameIgnoreCaseAndIdNotAndDeletedFalse(6L, "Quiz", 9L))
                .thenReturn(true);

        assertThatThrownBy(() -> service.update(9L, dto, 5L))
                .isInstanceOf(ValidationException.class);
    }

    @Test
    void should_find_by_id() {
        when(quizRepository.findById(9L)).thenReturn(Optional.of(quiz));

        when(quiz.isDeleted()).thenReturn(false);

        QuizResponseDTO dto = mock(QuizResponseDTO.class);

        when(quizMapper.toResponseDTO(quiz)).thenReturn(dto);

        QuizResponseDTO result = service.findById(9L);

        assertThat(result).isEqualTo(dto);
    }

    @Test
    void should_throw_when_deleted() {
        when(quizRepository.findById(9L)).thenReturn(Optional.of(quiz));

        when(quiz.isDeleted()).thenReturn(true);

        assertThatThrownBy(() -> service.findById(9L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void should_list_all_active() {
        when(quizRepository.findAllByDeletedFalse()).thenReturn(List.of(quiz));

        QuizListResponseDTO dto = mock(QuizListResponseDTO.class);

        when(quizMapper.toListResponseDTO(quiz)).thenReturn(dto);

        List<QuizListResponseDTO> result = service.listAllActive();

        assertThat(result).hasSize(1);
    }

    @Test
    void should_get_summary() {
        Long quizId = 9L;
        Long userId = 5L;

        UserQuizAttempt attempt = mock(UserQuizAttempt.class);

        when(quizRepository.findById(quizId)).thenReturn(Optional.of(quiz));

        when(authService.getAuthenticatedUserId()).thenReturn(userId);

        when(quizQuestionRepository
                .countByQuizIdAndDeletedFalse(quizId))
                .thenReturn(9L);

        when(userQuizAttemptRepository
                .findAllByQuizIdAndUserIdAndDeletedFalseOrderByCreatedAtAsc(quizId, userId))
                .thenReturn(List.of(attempt));

        lenient().when(attempt.getId()).thenReturn(7L);
        when(attempt.getScore()).thenReturn(new BigDecimal("80"));

        when(userQuizAttemptRepository
                .countAnswersByUserAndQuiz(userId, quizId))
                .thenReturn(5L);

        when(userQuizAttemptRepository
                .countCorrectAnswersByUserAndQuiz(userId, quizId))
                .thenReturn(4L);

        when(quiz.getMinScore()).thenReturn(new BigDecimal("70"));

        QuizSummaryResponseDTO result = service.getSummary(quizId);

        assertThat(result.isPassed()).isTrue();
        assertThat(result.getAnsweredQuestions()).isEqualTo(5);
        assertThat(result.getCorrectAnswers()).isEqualTo(4);
    }

    @Test
    void should_get_summary_without_attempts() {
        when(quizRepository.findById(9L)).thenReturn(Optional.of(quiz));

        when(authService.getAuthenticatedUserId()).thenReturn(5L);

        when(quizQuestionRepository
                .countByQuizIdAndDeletedFalse(9L))
                .thenReturn(5L);

        when(userQuizAttemptRepository
                .findAllByQuizIdAndUserIdAndDeletedFalseOrderByCreatedAtAsc(9L, 5L))
                .thenReturn(List.of());

        QuizSummaryResponseDTO result = service.getSummary(9L);

        assertThat(result.getAttemptsUsed()).isEqualTo(0);
        assertThat(result.isPassed()).isFalse();
    }

    @Test
    void should_list_my_attempts() {
        User user = mock(User.class);

        when(authService.getAuthenticatedUserEntity()).thenReturn(user);

        when(user.getId()).thenReturn(5L);

        when(quizRepository.findById(9L)).thenReturn(Optional.of(quiz));

        UserQuizAttempt attempt = mock(UserQuizAttempt.class);

        when(userQuizAttemptRepository
                .findAllByUserIdAndQuizIdAndDeletedFalse(5L, 9L))
                .thenReturn(List.of(attempt));

        UserQuizAttemptListResponseDTO dto = mock(UserQuizAttemptListResponseDTO.class);

        when(userQuizAttemptMapper.toListResponseDTO(attempt)).thenReturn(dto);

        List<UserQuizAttemptListResponseDTO> result = service.listMyAttempts(9L);

        assertThat(result).hasSize(1);
    }
}