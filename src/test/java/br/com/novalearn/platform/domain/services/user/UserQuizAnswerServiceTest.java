package br.com.novalearn.platform.domain.services.user;

import br.com.novalearn.platform.api.dtos.user.quizanswer.UserQuizAnswerCreateRequestDTO;
import br.com.novalearn.platform.api.dtos.user.quizanswer.UserQuizAnswerListResponseDTO;
import br.com.novalearn.platform.api.dtos.user.quizanswer.UserQuizAnswerResponseDTO;
import br.com.novalearn.platform.api.mappers.user.UserQuizAnswerMapper;
import br.com.novalearn.platform.core.exception.business.ForbiddenOperationException;
import br.com.novalearn.platform.core.exception.business.InvalidStateException;
import br.com.novalearn.platform.core.exception.business.ResourceNotFoundException;
import br.com.novalearn.platform.domain.entities.module.Module;
import br.com.novalearn.platform.domain.entities.course.Course;
import br.com.novalearn.platform.domain.entities.quiz.Quiz;
import br.com.novalearn.platform.domain.entities.quiz.QuizAnswerOption;
import br.com.novalearn.platform.domain.entities.quiz.QuizQuestion;
import br.com.novalearn.platform.domain.entities.user.User;
import br.com.novalearn.platform.domain.entities.user.UserQuizAnswer;
import br.com.novalearn.platform.domain.repositories.quiz.QuizAnswerOptionRepository;
import br.com.novalearn.platform.domain.repositories.quiz.QuizQuestionRepository;
import br.com.novalearn.platform.domain.repositories.user.UserCourseRepository;
import br.com.novalearn.platform.domain.repositories.user.UserQuizAnswerRepository;
import br.com.novalearn.platform.domain.repositories.user.UserRepository;
import br.com.novalearn.platform.domain.services.auth.AuthService;
import br.com.novalearn.platform.infra.time.provider.TimeProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserQuizAnswerServiceTest {
    @Mock
    private AuthService authService;

    @Mock
    private UserQuizAnswerRepository userQuizAnswerRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserCourseRepository userCourseRepository;

    @Mock
    private QuizQuestionRepository quizQuestionRepository;

    @Mock
    private QuizAnswerOptionRepository quizAnswerOptionRepository;

    @Mock
    private UserQuizAnswerMapper userQuizAnswerMapper;

    @Mock
    private TimeProvider timeProvider;

    @InjectMocks
    private UserQuizAnswerService service;

    private UserQuizAnswerCreateRequestDTO dto;
    private User user;
    private QuizQuestion question;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.clearContext();
        dto = new UserQuizAnswerCreateRequestDTO();
        user = mock(User.class);
        lenient().when(user.getId()).thenReturn(5L);
        question = mock(QuizQuestion.class);
        lenient().when(question.getId()).thenReturn(20L);
    }

    @Test
    void answer_question_should_create_answer() {
        Long userId = 5L;
        Long questionId = 20L;

        dto.setUserId(userId);
        dto.setQuizQuestionId(questionId);
        dto.setQuizAnswerOptionId(15L);

        when(question.getId()).thenReturn(questionId);

        Quiz quiz = mock(Quiz.class);
        Module module = mock(Module.class);
        Course course = mock(Course.class);

        when(question.getQuiz()).thenReturn(quiz);
        when(quiz.getModule()).thenReturn(module);
        when(module.getCourse()).thenReturn(course);
        when(course.getId()).thenReturn(1L);

        QuizAnswerOption option = mock(QuizAnswerOption.class);

        when(option.belongsTo(question)).thenReturn(true);

        lenient().when(option.getQuizQuestion()).thenReturn(question);

        lenient().when(question.getId()).thenReturn(questionId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(quizQuestionRepository.findById(questionId)).thenReturn(Optional.of(question));
        when(quizAnswerOptionRepository.findById(15L)).thenReturn(Optional.of(option));

        when(userCourseRepository
                .existsByUserIdAndCourseIdAndDeletedFalse(userId, 1L))
                .thenReturn(true);

        when(userQuizAnswerRepository
                .findByUserIdAndQuizQuestionIdAndDeletedFalse(userId, questionId))
                .thenReturn(Optional.empty());

        UserQuizAnswer answerEntity = mock(UserQuizAnswer.class);
        when(timeProvider.now()).thenReturn(LocalDateTime.now());
        when(userQuizAnswerRepository.save(any())).thenReturn(answerEntity);
        when(userQuizAnswerMapper.toResponseDTO(any())).thenReturn(new UserQuizAnswerResponseDTO());

        UserQuizAnswerResponseDTO result = service.answerQuestion(dto, userId);

        assertNotNull(result);
    }

    @Test
    void answer_question_should_throw_when_user_mismatch() {
        dto.setUserId(99L);

        assertThrows(
                ForbiddenOperationException.class,
                () -> service.answerQuestion(dto, 5L)
        );
    }

    @Test
    void answer_question_should_throw_when_not_enrolled() {
        Long userId = 5L;

        dto.setUserId(userId);
        dto.setQuizQuestionId(20L);
        dto.setQuizAnswerOptionId(15L);

        Quiz quiz = mock(Quiz.class);
        Module module = mock(Module.class);
        Course course = mock(Course.class);

        when(question.getQuiz()).thenReturn(quiz);
        when(quiz.getModule()).thenReturn(module);
        when(module.getCourse()).thenReturn(course);
        when(course.getId()).thenReturn(1L);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        QuizAnswerOption option = mock(QuizAnswerOption.class);
        when(quizAnswerOptionRepository.findById(15L)).thenReturn(Optional.of(option));

        when(quizQuestionRepository.findById(20L)).thenReturn(Optional.of(question));

        when(userCourseRepository
                .existsByUserIdAndCourseIdAndDeletedFalse(userId, 1L))
                .thenReturn(false);

        assertThrows(
                ForbiddenOperationException.class,
                () -> service.answerQuestion(dto, userId)
        );
    }

    @Test
    void answer_question_should_throw_when_already_answered() {
        Long userId = 5L;
        dto.setUserId(userId);
        dto.setQuizQuestionId(20L);
        dto.setQuizAnswerOptionId(15L);

        Quiz quiz = mock(Quiz.class);
        Module module = mock(Module.class);
        Course course = mock(Course.class);
        QuizAnswerOption option = mock(QuizAnswerOption.class);

        when(question.getQuiz()).thenReturn(quiz);
        when(quiz.getModule()).thenReturn(module);
        when(module.getCourse()).thenReturn(course);
        when(course.getId()).thenReturn(1L);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(quizQuestionRepository.findById(20L)).thenReturn(Optional.of(question));
        when(quizAnswerOptionRepository.findById(15L)).thenReturn(Optional.of(option));

        when(userCourseRepository.existsByUserIdAndCourseIdAndDeletedFalse(userId, 1L))
                .thenReturn(true);

        when(userQuizAnswerRepository.findByUserIdAndQuizQuestionIdAndDeletedFalse(
                userId, 20L
        )).thenReturn(Optional.of(mock(UserQuizAnswer.class)));

        assertThrows(InvalidStateException.class, () -> service.answerQuestion(dto, userId));
    }

    @Test
    void change_answer_should_update_answer() {
        Long userId = 5L;
        when(authService.getAuthenticatedUserId()).thenReturn(userId);

        UserQuizAnswer answer = mock(UserQuizAnswer.class);
        QuizAnswerOption option = mock(QuizAnswerOption.class);

        when(userQuizAnswerRepository.findByIdAndUserIdAndDeletedFalse(12L, userId))
                .thenReturn(Optional.of(answer));

        when(quizAnswerOptionRepository.findById(15L)).thenReturn(Optional.of(option));

        when(timeProvider.now()).thenReturn(LocalDateTime.now());

        when(userQuizAnswerRepository.save(answer)).thenReturn(answer);

        when(userQuizAnswerMapper.toResponseDTO(answer))
                .thenReturn(new UserQuizAnswerResponseDTO());

        UserQuizAnswerResponseDTO result = service.changeAnswer(12L, 15L);

        verify(answer).changeAnswer(eq(option), any());
        verify(answer).setUpdatedBy(5L);
        verify(answer).setUpdatedAt(any());

        assertNotNull(result);
    }

    @Test
    void change_answer_should_throw_when_not_found() {
        when(authService.getAuthenticatedUserId()).thenReturn(5L);

        when(userQuizAnswerRepository
                .findByIdAndUserIdAndDeletedFalse(12L, 5L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> service.changeAnswer(12L, 15L)
        );
    }

    @Test
    void list_my_answers_by_quiz_should_return_list() {
        when(authService.getAuthenticatedUserId()).thenReturn(5L);

        when(userQuizAnswerRepository
                .findAllByUserIdAndQuizQuestionQuizIdAndDeletedFalse(5L, 9L))
                .thenReturn(List.of(mock(UserQuizAnswer.class)));

        when(userQuizAnswerMapper.toListResponseDTO(any()))
                .thenReturn(new UserQuizAnswerListResponseDTO());

        List<UserQuizAnswerListResponseDTO> result = service.listMyAnswersByQuiz(9L);

        assertEquals(1, result.size());
    }

    @Test
    void find_my_answer_by_question_should_return() {
        when(authService.getAuthenticatedUserId()).thenReturn(5L);

        Quiz quiz = mock(Quiz.class);
        Module module = mock(Module.class);
        Course course = mock(Course.class);

        when(question.getQuiz()).thenReturn(quiz);
        when(quiz.getModule()).thenReturn(module);
        when(module.getCourse()).thenReturn(course);
        when(course.getId()).thenReturn(1L);

        when(quizQuestionRepository.findById(20L)).thenReturn(Optional.of(question));

        when(userCourseRepository
                .existsByUserIdAndCourseIdAndDeletedFalse(5L, 1L))
                .thenReturn(true);

        UserQuizAnswer answer = mock(UserQuizAnswer.class);

        when(userQuizAnswerRepository
                .findByUserIdAndQuizQuestionIdAndDeletedFalse(5L, 20L))
                .thenReturn(Optional.of(answer));

        when(userQuizAnswerMapper.toResponseDTO(answer)).thenReturn(new UserQuizAnswerResponseDTO());

        UserQuizAnswerResponseDTO result = service.findMyAnswerByQuestion(20L);

        assertNotNull(result);
    }

    @Test
    void find_my_answer_by_question_should_throw_when_not_found() {
        when(authService.getAuthenticatedUserId()).thenReturn(5L);

        Quiz quiz = mock(Quiz.class);
        Module module = mock(Module.class);
        Course course = mock(Course.class);

        when(question.getQuiz()).thenReturn(quiz);
        when(quiz.getModule()).thenReturn(module);
        when(module.getCourse()).thenReturn(course);
        when(course.getId()).thenReturn(1L);

        when(quizQuestionRepository.findById(20L)).thenReturn(Optional.of(question));
        when(userCourseRepository.existsByUserIdAndCourseIdAndDeletedFalse(
                5L, 1L)).thenReturn(true);

        when(userQuizAnswerRepository.findByUserIdAndQuizQuestionIdAndDeletedFalse(
                5L, 20L
        )).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.findMyAnswerByQuestion(
                20L));
    }

    @Test
    void find_my_answer_by_id_should_return() {
        UserQuizAnswer answer = mock(UserQuizAnswer.class);

        when(userQuizAnswerRepository
                .findByIdAndUserIdAndDeletedFalse(12L, 5L))
                .thenReturn(Optional.of(answer));

        when(userQuizAnswerMapper.toResponseDTO(answer)).thenReturn(new UserQuizAnswerResponseDTO());

        UserQuizAnswerResponseDTO result = service.findMyAnswerById(12L, 5L);

        assertNotNull(result);
    }

    @Test
    void list_all_active_should_return_list() {
        when(userQuizAnswerRepository.findAllByDeletedFalse())
                .thenReturn(List.of(mock(UserQuizAnswer.class)));

        when(userQuizAnswerMapper.toListResponseDTO(any())).thenReturn(new UserQuizAnswerListResponseDTO());

        List<UserQuizAnswerListResponseDTO> result = service.listAllActive();

        assertEquals(1, result.size());
    }
}