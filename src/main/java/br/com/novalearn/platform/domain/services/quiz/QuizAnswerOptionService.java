package br.com.novalearn.platform.domain.services.quiz;

import br.com.novalearn.platform.api.dtos.quiz.answeroption.QuizAnswerOptionCreateRequestDTO;
import br.com.novalearn.platform.api.dtos.quiz.answeroption.QuizAnswerOptionListResponseDTO;
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
import br.com.novalearn.platform.domain.services.BaseCrudService;
import br.com.novalearn.platform.infra.time.provider.TimeProvider;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class QuizAnswerOptionService extends BaseCrudService<QuizAnswerOption> {
    private final QuizAnswerOptionRepository quizAnswerOptionRepository;
    private final QuizQuestionRepository quizQuestionRepository;
    private final QuizAnswerOptionMapper quizAnswerOptionMapper;

    public QuizAnswerOptionService(
            QuizAnswerOptionRepository quizAnswerOptionRepository,
            QuizQuestionRepository quizQuestionRepository,
            QuizAnswerOptionMapper quizAnswerOptionMapper,
            TimeProvider timeProvider
    ) {
        super(quizAnswerOptionRepository, "Quiz Answer Option", timeProvider);
        this.quizAnswerOptionRepository = quizAnswerOptionRepository;
        this.quizQuestionRepository = quizQuestionRepository;
        this.quizAnswerOptionMapper = quizAnswerOptionMapper;
    }

    @Transactional
    public QuizAnswerOptionResponseDTO create(Long userId, QuizAnswerOptionCreateRequestDTO dto) {
        QuizQuestion question = findQuizQuestionOrThrow(dto.getQuestionId());

        if (quizAnswerOptionRepository.existsByQuizQuestionIdAndSequenceAndDeletedFalse(
                question.getId(), dto.getSequence())) {
            throw new ValidationException("Sequence already exists for this question.");
        }

        boolean alreadyHasCorrect =
                quizAnswerOptionRepository.existsByQuizQuestionIdAndCorrectTrueAndDeletedFalse(question.getId());

        if(dto.getCorrect() && alreadyHasCorrect) throw new InvalidStateException("This question already has a correct option.");

        if(!dto.getCorrect() && !alreadyHasCorrect) throw new InvalidStateException("Question must have at least one correct option.");

        QuizAnswerOption entity = QuizAnswerOption.create(
                question,
                dto.getSequence(),
                dto.getOption(),
                dto.getCorrect(),
                dto.getObservations(),
                userId,
                timeProvider.now()
        );

        applyAuditCreate(entity, userId);

        return quizAnswerOptionMapper.toResponseDTO(quizAnswerOptionRepository.save(entity));
    }

    @Transactional
    public QuizAnswerOptionResponseDTO update(Long id, QuizAnswerOptionUpdateRequestDTO dto, Long userId) {
        QuizAnswerOption entity = findEntityOrThrow(id);
        entity.ensureNotDeleted();

        Long questionId = entity.getQuizQuestion().getId();

        if (dto.getSequence() != null &&
                quizAnswerOptionRepository.existsByQuizQuestionIdAndIdNotAndSequenceAndDeletedFalse(
                        questionId, entity.getId(), dto.getSequence())) {
            throw new ValidationException("Sequence already used in this question.");
        }

        if (dto.getCorrect() != null) {
            boolean removingCorrect = entity.isCorrect() && !dto.getCorrect();
            boolean addingCorrect = !entity.isCorrect() && dto.getCorrect();

            if (removingCorrect &&
                    !quizAnswerOptionRepository.existsByQuizQuestionIdAndIdNotAndCorrectTrueAndDeletedFalse(
                            questionId, entity.getId())) {
                throw new InvalidStateException("Question must have at least one correct option.");
            }

            if (addingCorrect &&
                    quizAnswerOptionRepository.existsByQuizQuestionIdAndCorrectTrueAndDeletedFalse(questionId)) {
                throw new InvalidStateException("This question already has a correct option.");
            }
        }

        if(dto.getSequence() != null) entity.changeSequence(dto.getSequence());
        if(dto.getOption() != null) entity.changeText(dto.getOption());
        if(dto.getCorrect() != null) entity.changeCorrect(dto.getCorrect());
        if(dto.getObservations() != null) entity.changeObservations(dto.getObservations());

        entity.auditUpdate(userId, timeProvider.now());

        return quizAnswerOptionMapper.toResponseDTO(quizAnswerOptionRepository.save(entity));
    }

    @Transactional
    public QuizAnswerOptionResponseDTO findById(Long id) {
        QuizAnswerOption entity = findEntityOrThrow(id);

        entity.ensureNotDeleted();

        return quizAnswerOptionMapper.toResponseDTO(entity);
    }

    @Transactional
    public List<QuizAnswerOptionResponseDTO> listByQuestion(Long questionId) {
        quizQuestionRepository.findById(questionId)
                .orElseThrow(() -> new ResourceNotFoundException("Quiz question not found."));

        return quizAnswerOptionRepository.findAllByQuizQuestionIdAndDeletedFalseOrderBySequenceAsc(questionId)
                .stream()
                .map(quizAnswerOptionMapper::toResponseDTO)
                .toList();
    }

    @Transactional
    public void markAsCorrect(Long id, Long userId) {
        QuizAnswerOption entity = findEntityOrThrow(id);

        entity.ensureNotDeleted();

        Long questionId = entity.getQuizQuestion().getId();

        boolean alreadyHasCorrect = quizAnswerOptionRepository.existsByQuizQuestionIdAndCorrectTrueAndDeletedFalse(questionId);

        if(alreadyHasCorrect && !entity.isCorrect()) {
            throw new InvalidStateException("This question already has a correct option.");
        }

        entity.changeCorrect(true);
        applyAuditUpdate(entity, userId);

        quizAnswerOptionRepository.save(entity);
    }

    @Transactional
    public void reorder(List<Long> orderedIds, Long userId) {
        if(orderedIds == null || orderedIds.isEmpty()) {
            throw new InvalidStateException("Ordered ids list cannot be empty.");
        }

        List<QuizAnswerOption> options = quizAnswerOptionRepository.findAllById(orderedIds);

        if(options.size() != orderedIds.size()) {
            throw new ResourceNotFoundException("Some answer options were not found.");
        }

        int sequence = 1;

        for(Long id : orderedIds) {
            QuizAnswerOption option = options.stream()
                    .filter(o -> o.getId().equals(id))
                    .findFirst()
                    .orElseThrow();

            option.changeSequence(sequence++);
            applyAuditUpdate(option, userId);
        }

        quizAnswerOptionRepository.saveAll(options);
    }

    private QuizQuestion findQuizQuestionOrThrow(Long id) {
        return quizQuestionRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Quiz question not found")
        );
    }
}