package br.com.novalearn.platform.domain.services.quiz;

import br.com.novalearn.platform.api.dtos.quiz.answeroption.QuizAnswerOptionResponseDTO;
import br.com.novalearn.platform.api.dtos.quiz.question.QuizQuestionCreateRequestDTO;
import br.com.novalearn.platform.api.dtos.quiz.question.QuizQuestionResponseDTO;
import br.com.novalearn.platform.api.dtos.quiz.question.QuizQuestionUpdateRequestDTO;
import br.com.novalearn.platform.api.mappers.quiz.QuizAnswerOptionMapper;
import br.com.novalearn.platform.api.mappers.quiz.QuizQuestionMapper;
import br.com.novalearn.platform.core.exception.business.InvalidStateException;
import br.com.novalearn.platform.core.exception.business.ResourceNotFoundException;
import br.com.novalearn.platform.core.exception.business.ValidationException;
import br.com.novalearn.platform.domain.entities.quiz.Quiz;
import br.com.novalearn.platform.domain.entities.quiz.QuizQuestion;
import br.com.novalearn.platform.domain.repositories.quiz.QuizAnswerOptionRepository;
import br.com.novalearn.platform.domain.repositories.quiz.QuizQuestionRepository;
import br.com.novalearn.platform.domain.repositories.quiz.QuizRepository;
import br.com.novalearn.platform.infra.time.provider.TimeProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class QuizQuestionServiceTest {
    @Mock
    private QuizRepository quizRepository;

    @Mock
    private QuizQuestionRepository quizQuestionRepository;

    @Mock
    private QuizAnswerOptionRepository quizAnswerOptionRepository;

    @Mock
    private QuizQuestionMapper quizQuestionMapper;

    @Mock
    private QuizAnswerOptionMapper quizAnswerOptionMapper;

    @Mock
    private TimeProvider timeProvider;

    @InjectMocks
    private QuizQuestionService service;

    private Quiz quiz;
    private QuizQuestionCreateRequestDTO dto;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.clearContext();
        quiz = mock(Quiz.class);
        lenient().when(quiz.getId()).thenReturn(9L);
        dto = new QuizQuestionCreateRequestDTO();
    }

    @Test
    void should_create_quiz_question_successfully() {
        Long userId = 5L;

        when(quiz.isActive()).thenReturn(true);
        when(quiz.isDeleted()).thenReturn(false);

        dto.setQuizId(9L);
        dto.setSequence(1);
        dto.setDescription("Pergunta");
        dto.setPoints(10);

        QuizQuestion saved = mock(QuizQuestion.class);
        QuizQuestionResponseDTO response = new QuizQuestionResponseDTO();

        when(quizRepository.findById(9L)).thenReturn(Optional.of(quiz));
        when(quizQuestionRepository.existsByQuizIdAndSequenceAndDeletedFalse(
                9L, 1)).thenReturn(false);

        when(quizQuestionRepository.save(any())).thenReturn(saved);
        when(quizQuestionMapper.toResponseDTO(saved)).thenReturn(response);

        QuizQuestionResponseDTO result = service.create(userId, dto);

        assertNotNull(result);

        verify(quizQuestionRepository).save(any());
    }

    @Test
    void should_throw_when_quiz_inactive() {
        when(quiz.isActive()).thenReturn(false);

        dto.setQuizId(9L);

        when(quizRepository.findById(9L))
                .thenReturn(Optional.of(quiz));

        assertThrows(
                InvalidStateException.class,
                () -> service.create(5L, dto)
        );
    }

    @Test
    void should_throw_when_sequence_exists() {
        when(quiz.isActive()).thenReturn(true);
        when(quiz.isDeleted()).thenReturn(false);

        dto.setQuizId(9L);
        dto.setSequence(1);

        when(quizRepository.findById(9L)).thenReturn(Optional.of(quiz));

        when(quizQuestionRepository
                .existsByQuizIdAndSequenceAndDeletedFalse(9L, 1))
                .thenReturn(true);

        assertThrows(
                ValidationException.class,
                () -> service.create(5L, dto)
        );
    }

    @Test
    void should_update_quiz_question() {
        Long id = 20L;
        Long userId = 5L;

        QuizQuestion entity = mock(QuizQuestion.class);

        QuizQuestionUpdateRequestDTO dto = new QuizQuestionUpdateRequestDTO();
        dto.setDescription("Nova descrição");
        dto.setPoints(20);

        when(quizQuestionRepository.findById(id)).thenReturn(Optional.of(entity));

        when(quizQuestionMapper.toResponseDTO(entity)).thenReturn(new QuizQuestionResponseDTO());

        QuizQuestionResponseDTO result = service.update(id, dto, userId);

        verify(entity).changeDescription("Nova descrição");
        verify(entity).changePoints(20);

        assertNotNull(result);
    }

    @Test
    void should_throw_when_update_with_duplicated_sequence() {
        QuizQuestion entity = mock(QuizQuestion.class);

        when(entity.getQuiz()).thenReturn(quiz);
        when(entity.getId()).thenReturn(20L);

        QuizQuestionUpdateRequestDTO dto = new QuizQuestionUpdateRequestDTO();
        dto.setSequence(2);

        when(quizQuestionRepository.findById(20L)).thenReturn(Optional.of(entity));

        when(quizQuestionRepository
                .existsByQuizIdAndIdNotAndSequenceAndDeletedFalse(any(), any(), any()))
                .thenReturn(true);

        assertThrows(
                ValidationException.class,
                () -> service.update(20L, dto, 5L)
        );
    }

    @Test
    void should_find_by_id() {
        QuizQuestion entity = mock(QuizQuestion.class);
        when(entity.isDeleted()).thenReturn(false);

        when(quizQuestionRepository.findById(20L)).thenReturn(Optional.of(entity));

        when(quizQuestionMapper.toResponseDTO(entity)).thenReturn(new QuizQuestionResponseDTO());

        QuizQuestionResponseDTO result = service.findById(20L);

        assertNotNull(result);
    }

    @Test
    void should_throw_when_deleted() {
        QuizQuestion entity = mock(QuizQuestion.class);
        when(entity.isDeleted()).thenReturn(true);

        when(quizQuestionRepository.findById(20L)).thenReturn(Optional.of(entity));

        assertThrows(
                ResourceNotFoundException.class,
                () -> service.findById(20L)
        );
    }

    @Test
    void should_list_by_quiz() {
        when(quizRepository.findById(9L)).thenReturn(Optional.of(mock(Quiz.class)));

        when(quizQuestionRepository
                .findAllByQuizIdAndDeletedFalseAndActiveTrueOrderBySequenceAsc(9L))
                .thenReturn(List.of(mock(QuizQuestion.class)));

        when(quizQuestionMapper.toResponseDTO(any())).thenReturn(new QuizQuestionResponseDTO());

        List<QuizQuestionResponseDTO> result = service.listByQuiz(9L);

        assertFalse(result.isEmpty());
    }
}