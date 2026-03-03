package br.com.novalearn.platform.domain.services.quiz;

import br.com.novalearn.platform.api.dtos.quiz.answeroption.QuizAnswerOptionResponseDTO;
import br.com.novalearn.platform.api.dtos.quiz.question.QuizQuestionCreateRequestDTO;
import br.com.novalearn.platform.api.dtos.quiz.question.QuizQuestionListResponseDTO;
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
import br.com.novalearn.platform.domain.services.BaseCrudService;
import br.com.novalearn.platform.infra.time.provider.TimeProvider;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class QuizQuestionService extends BaseCrudService<QuizQuestion> {
    private final QuizRepository quizRepository;
    private final QuizQuestionRepository quizQuestionRepository;
    private final QuizAnswerOptionRepository quizAnswerOptionRepository;
    private final QuizQuestionMapper quizQuestionMapper;
    private final QuizAnswerOptionMapper quizAnswerOptionMapper;

    public QuizQuestionService(
            QuizRepository quizRepository,
            QuizQuestionRepository quizQuestionRepository,
            QuizAnswerOptionRepository quizAnswerOptionRepository,
            QuizQuestionMapper quizQuestionMapper,
            QuizAnswerOptionMapper quizAnswerOptionMapper,
            TimeProvider timeProvider
    ) {
        super(quizQuestionRepository, "Quiz Question", timeProvider);
        this.quizRepository = quizRepository;
        this.quizQuestionRepository = quizQuestionRepository;
        this.quizAnswerOptionRepository = quizAnswerOptionRepository;
        this.quizQuestionMapper = quizQuestionMapper;
        this.quizAnswerOptionMapper = quizAnswerOptionMapper;
    }

    @Transactional
    public QuizQuestionResponseDTO create(Long userId, QuizQuestionCreateRequestDTO dto) {
        Quiz quiz = findQuizOrThrow(dto.getQuizId());

        if(!quiz.isActive() || quiz.isDeleted()) {
            throw new InvalidStateException("Cannot add questions to inactive or deleted quiz.");
        }

        if(quizQuestionRepository.existsByQuizIdAndSequenceAndDeletedFalse(
                quiz.getId(), dto.getSequence()
        )) {
            throw new ValidationException("Sequence already exists for this quiz.");
        }

        QuizQuestion question = QuizQuestion.create(
                quiz,
                dto.getSequence(),
                dto.getDescription(),
                dto.getPoints()
        );

        QuizQuestion saved = quizQuestionRepository.save(question);

        applyAuditCreate(saved, userId);

        return quizQuestionMapper.toResponseDTO(saved);
    }

    @Transactional
    public QuizQuestionResponseDTO update(Long id, QuizQuestionUpdateRequestDTO dto, Long userId) {
        QuizQuestion entity = findEntityOrThrow(id);
        entity.ensureNotDeleted();

        if(dto.getSequence() != null &&
                quizQuestionRepository.existsByQuizIdAndIdNotAndSequenceAndDeletedFalse(
                        entity.getQuiz().getId(), entity.getId(), dto.getSequence())) {
            throw new ValidationException("Sequence already used in this quiz.");
        }

        if(dto.getDescription() != null) {
            entity.changeDescription(dto.getDescription());
        }

        if(dto.getPoints() != null) {
            entity.changePoints(dto.getPoints());
        }

        applyAuditUpdate(entity, userId);

        return quizQuestionMapper.toResponseDTO(entity);
    }

    @Transactional
    public QuizQuestionResponseDTO findById(Long id) {
        QuizQuestion entity = findEntityOrThrow(id);

        if(entity.isDeleted()) throw new ResourceNotFoundException("Quiz question not found");

        return quizQuestionMapper.toResponseDTO(entity);
    }

    @Transactional
    public List<QuizQuestionListResponseDTO> listAllActive() {
        return quizQuestionRepository.findAllByDeletedFalse()
                .stream()
                .map(quizQuestionMapper::toListResponseDTO)
                .toList();
    }

    @Transactional
    public List<QuizQuestionResponseDTO> listByQuiz(Long quizId) {
        findQuizOrThrow(quizId);

        return quizQuestionRepository
                .findAllByQuizIdAndDeletedFalseAndActiveTrueOrderBySequenceAsc(quizId)
                .stream()
                .map(quizQuestionMapper::toResponseDTO)
                .toList();
    }

    private Quiz findQuizOrThrow(Long id) {
        return quizRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Quiz not found")
        );
    }
}