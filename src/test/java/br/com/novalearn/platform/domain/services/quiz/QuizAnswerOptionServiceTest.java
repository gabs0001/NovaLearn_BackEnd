package br.com.novalearn.platform.domain.services.quiz;

import br.com.novalearn.platform.api.dtos.quiz.answeroption.QuizAnswerOptionCreateRequestDTO;
import br.com.novalearn.platform.api.dtos.quiz.answeroption.QuizAnswerOptionResponseDTO;
import br.com.novalearn.platform.api.dtos.quiz.answeroption.QuizAnswerOptionUpdateRequestDTO;
import br.com.novalearn.platform.api.mappers.quiz.QuizAnswerOptionMapper;
import br.com.novalearn.platform.core.exception.business.InvalidStateException;
import br.com.novalearn.platform.core.exception.business.ResourceNotFoundException;
import br.com.novalearn.platform.core.exception.business.ValidationException;
import br.com.novalearn.platform.domain.entities.quiz.QuizAnswerOption;
import br.com.novalearn.platform.domain.entities.quiz.QuizQuestion;
import br.com.novalearn.platform.domain.repositories.quiz.QuizAnswerOptionRepository;
import br.com.novalearn.platform.domain.repositories.quiz.QuizQuestionRepository;
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
public class QuizAnswerOptionServiceTest {
    @Mock
    private QuizAnswerOptionRepository quizAnswerOptionRepository;

    @Mock
    private QuizQuestionRepository quizQuestionRepository;

    @Mock
    private QuizAnswerOptionMapper quizAnswerOptionMapper;

    @Mock
    private TimeProvider timeProvider;

    @InjectMocks
    private QuizAnswerOptionService service;

    private QuizQuestion question;
    private QuizAnswerOption entity;
    private QuizAnswerOptionCreateRequestDTO dto;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.clearContext();
        question = mock(QuizQuestion.class);
        entity = mock(QuizAnswerOption.class);
        dto = new QuizAnswerOptionCreateRequestDTO();
    }

    @Test
    void should_create_first_correct_option() {
        Long userId = 5L;
        dto.setQuestionId(20L);

        when(question.getId()).thenReturn(20L);

        dto.setSequence(1);
        dto.setOption("A");
        dto.setCorrect(true);

        when(quizQuestionRepository.findById(20L)).thenReturn(Optional.of(question));

        when(quizAnswerOptionRepository
                .existsByQuizQuestionIdAndSequenceAndDeletedFalse(20L, 1))
                .thenReturn(false);

        when(quizAnswerOptionRepository
                .existsByQuizQuestionIdAndCorrectTrueAndDeletedFalse(20L))
                .thenReturn(false);

        when(timeProvider.now()).thenReturn(LocalDateTime.now());

        when(quizAnswerOptionRepository.save(any())).thenReturn(entity);

        when(quizAnswerOptionMapper.toResponseDTO(entity)).thenReturn(new QuizAnswerOptionResponseDTO());

        QuizAnswerOptionResponseDTO result = service.create(userId, dto);

        assertNotNull(result);
    }

    @Test
    void should_throw_when_creating_second_correct() {
        when(question.getId()).thenReturn(20L);
        dto.setQuestionId(20L);

        dto.setSequence(1);
        dto.setCorrect(true);

        when(quizQuestionRepository.findById(20L)).thenReturn(Optional.of(question));

        when(quizAnswerOptionRepository
                .existsByQuizQuestionIdAndCorrectTrueAndDeletedFalse(20L))
                .thenReturn(true);

        when(quizAnswerOptionRepository
                .existsByQuizQuestionIdAndSequenceAndDeletedFalse(20L, 1))
                .thenReturn(false);

        assertThrows(
                InvalidStateException.class,
                () -> service.create(5L, dto)
        );
    }

    @Test
    void should_throw_when_creating_false_without_correct() {
        when(question.getId()).thenReturn(20L);
        dto.setQuestionId(20L);

        dto.setSequence(1);
        dto.setCorrect(false);

        when(quizQuestionRepository.findById(20L)).thenReturn(Optional.of(question));

        when(quizAnswerOptionRepository
                .existsByQuizQuestionIdAndCorrectTrueAndDeletedFalse(20L))
                .thenReturn(false);

        when(quizAnswerOptionRepository
                .existsByQuizQuestionIdAndSequenceAndDeletedFalse(20L, 1))
                .thenReturn(false);

        assertThrows(
                InvalidStateException.class,
                () -> service.create(5L, dto)
        );
    }

    @Test
    void should_throw_when_duplicate_sequence() {
        when(question.getId()).thenReturn(20L);
        dto.setQuestionId(20L);

        dto.setSequence(1);

        when(quizQuestionRepository.findById(20L)).thenReturn(Optional.of(question));

        when(quizAnswerOptionRepository
                .existsByQuizQuestionIdAndSequenceAndDeletedFalse(20L, 1))
                .thenReturn(true);

        assertThrows(
                ValidationException.class,
                () -> service.create(5L, dto)
        );
    }

    @Test
    void should_update_option() {
        when(question.getId()).thenReturn(20L);

        when(entity.getQuizQuestion()).thenReturn(question);
        when(entity.isCorrect()).thenReturn(false);

        QuizAnswerOptionUpdateRequestDTO dto = new QuizAnswerOptionUpdateRequestDTO();
        dto.setOption("Nova");
        dto.setCorrect(true);

        when(quizAnswerOptionRepository.findById(15L)).thenReturn(Optional.of(entity));

        when(quizAnswerOptionRepository
                .existsByQuizQuestionIdAndCorrectTrueAndDeletedFalse(20L))
                .thenReturn(false);

        when(quizAnswerOptionRepository.save(entity)).thenReturn(entity);

        when(quizAnswerOptionMapper.toResponseDTO(entity))
                .thenReturn(new QuizAnswerOptionResponseDTO());

        QuizAnswerOptionResponseDTO result = service.update(15L, dto, 5L);

        verify(entity).changeText("Nova");
        verify(entity).changeCorrect(true);

        assertNotNull(result);
    }

    @Test
    void should_throw_when_removing_last_correct() {
        when(question.getId()).thenReturn(20L);

        when(entity.getQuizQuestion()).thenReturn(question);
        when(entity.isCorrect()).thenReturn(true);
        when(entity.getId()).thenReturn(15L);

        QuizAnswerOptionUpdateRequestDTO dto = new QuizAnswerOptionUpdateRequestDTO();

        dto.setCorrect(false);

        when(quizAnswerOptionRepository.findById(15L)).thenReturn(Optional.of(entity));

        when(quizAnswerOptionRepository
                .existsByQuizQuestionIdAndIdNotAndCorrectTrueAndDeletedFalse(20L, 15L))
                .thenReturn(false);

        assertThrows(
                InvalidStateException.class,
                () -> service.update(15L, dto, 5L)
        );
    }

    @Test
    void should_throw_when_adding_second_correct() {
        when(question.getId()).thenReturn(20L);

        when(entity.getQuizQuestion()).thenReturn(question);
        when(entity.isCorrect()).thenReturn(false);

        QuizAnswerOptionUpdateRequestDTO dto = new QuizAnswerOptionUpdateRequestDTO();

        dto.setCorrect(true);

        when(quizAnswerOptionRepository.findById(15L)).thenReturn(Optional.of(entity));

        when(quizAnswerOptionRepository
                .existsByQuizQuestionIdAndCorrectTrueAndDeletedFalse(20L))
                .thenReturn(true);

        assertThrows(
                InvalidStateException.class,
                () -> service.update(15L, dto, 5L)
        );
    }

    @Test
    void should_mark_as_correct() {
        when(question.getId()).thenReturn(20L);

        when(entity.getQuizQuestion()).thenReturn(question);

        when(quizAnswerOptionRepository.findById(15L)).thenReturn(Optional.of(entity));

        when(quizAnswerOptionRepository
                .existsByQuizQuestionIdAndCorrectTrueAndDeletedFalse(20L))
                .thenReturn(false);

        service.markAsCorrect(15L, 5L);

        verify(entity).changeCorrect(true);
        verify(quizAnswerOptionRepository).save(entity);
    }

    @Test
    void should_throw_when_marking_second_correct() {
        when(question.getId()).thenReturn(20L);

        when(entity.getQuizQuestion()).thenReturn(question);
        when(entity.isCorrect()).thenReturn(false);

        when(quizAnswerOptionRepository.findById(15L)).thenReturn(Optional.of(entity));

        when(quizAnswerOptionRepository
                .existsByQuizQuestionIdAndCorrectTrueAndDeletedFalse(20L))
                .thenReturn(true);

        assertThrows(
                InvalidStateException.class,
                () -> service.markAsCorrect(15L, 5L)
        );
    }

    @Test
    void shouldReorderOptions() {
        QuizAnswerOption o1 = mock(QuizAnswerOption.class);
        QuizAnswerOption o2 = mock(QuizAnswerOption.class);

        when(o1.getId()).thenReturn(15L);
        when(o2.getId()).thenReturn(25L);

        when(quizAnswerOptionRepository.findAllById(List.of(25L,15L))).thenReturn(List.of(o1,o2));

        service.reorder(List.of(25L, 15L), 5L);

        verify(o2).changeSequence(1);
        verify(o1).changeSequence(2);

        verify(quizAnswerOptionRepository).saveAll(any());
    }

    @Test
    void shouldThrowWhenReorderEmpty() {
        assertThrows(
                InvalidStateException.class,
                () -> service.reorder(List.of(), 5L)
        );
    }

    @Test
    void shouldThrowWhenSomeIdsNotFound() {
        when(quizAnswerOptionRepository.findAllById(List.of(15L,25L))).thenReturn(List.of());

        assertThrows(
                ResourceNotFoundException.class,
                () -> service.reorder(List.of(15L,25L), 5L)
        );
    }
}