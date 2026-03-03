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
import br.com.novalearn.platform.domain.entities.module.Module;
import br.com.novalearn.platform.domain.entities.quiz.Quiz;
import br.com.novalearn.platform.domain.entities.user.User;
import br.com.novalearn.platform.domain.entities.user.UserQuizAttempt;
import br.com.novalearn.platform.domain.repositories.module.ModuleRepository;
import br.com.novalearn.platform.domain.repositories.quiz.QuizQuestionRepository;
import br.com.novalearn.platform.domain.repositories.quiz.QuizRepository;
import br.com.novalearn.platform.domain.repositories.user.UserQuizAttemptRepository;
import br.com.novalearn.platform.domain.services.BaseCrudService;
import br.com.novalearn.platform.domain.services.auth.AuthService;
import br.com.novalearn.platform.infra.time.provider.TimeProvider;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class QuizService extends BaseCrudService<Quiz> {
    private final AuthService authService;
    private final QuizRepository quizRepository;
    private final QuizQuestionRepository quizQuestionRepository;
    private final ModuleRepository moduleRepository;
    private final UserQuizAttemptRepository userQuizAttemptRepository;
    private final UserQuizAttemptMapper userQuizAttemptMapper;
    private final QuizMapper quizMapper;

    public QuizService(
            AuthService authService,
            QuizRepository quizRepository,
            QuizQuestionRepository quizQuestionRepository,
            ModuleRepository moduleRepository,
            UserQuizAttemptRepository userQuizAttemptRepository,
            UserQuizAttemptMapper userQuizAttemptMapper,
            QuizMapper quizMapper,
            TimeProvider timeProvider
    ) {
        super(quizRepository, "Quiz", timeProvider);
        this.authService = authService;
        this.quizRepository = quizRepository;
        this.quizQuestionRepository = quizQuestionRepository;
        this.moduleRepository = moduleRepository;
        this.userQuizAttemptRepository = userQuizAttemptRepository;
        this.userQuizAttemptMapper = userQuizAttemptMapper;
        this.quizMapper = quizMapper;
    }

    @Transactional
    public QuizResponseDTO create(QuizCreateRequestDTO dto) {
        Module module = findModuleOrThrow(dto.getModuleId());

        if(module.isDeleted() || !module.isActive()) {
            throw new InvalidStateException("Quiz cannot be created in an inactive or deleted module.");
        }

        validateCreate(dto, module);

        Quiz entity = quizMapper.toEntity(dto, module);
        return quizMapper.toResponseDTO(quizRepository.save(entity));
    }

    private void validateCreate(QuizCreateRequestDTO dto, Module module) {
        if(quizRepository.existsByModuleIdAndNameIgnoreCaseAndDeletedFalse(module.getId(), dto.getName())) {
            throw new ValidationException("A quiz with this name already exists in this module.");
        }

        if(dto.getQtdQuestions() != null && dto.getQtdQuestions() <= 0) {
            throw new ValidationException("Quantity of questions must be greater than zero.");
        }

        if(dto.getMaxAttempts() != null && dto.getMaxAttempts() <= 0) {
            throw new ValidationException("Max attempts must be greater than zero.");
        }

        if(dto.getMinScore() != null && dto.getMinScore().signum() < 0) {
            throw new ValidationException("Minimum score cannot be negative.");
        }
    }

    @Transactional
    public QuizResponseDTO update(Long id, QuizUpdateRequestDTO dto, Long userId) {
        Quiz entity = findEntityOrThrow(id);
        entity.ensureNotDeleted();

        if(dto.getName() != null && quizRepository.existsByModuleIdAndNameIgnoreCaseAndIdNotAndDeletedFalse(
                        entity.getModule().getId(),
                        dto.getName(),
                        entity.getId())
        ) {
            throw new ValidationException("A quiz with this name already exists in this module.");
        }

        entity.update(
                dto.getName(),
                dto.getDescription(),
                dto.getInstructions(),
                dto.getQtdQuestions(),
                dto.getMinScore(),
                dto.getMaxAttempts(),
                dto.getRandomOrder(),
                dto.getObservations()
        );

        applyAuditUpdate(entity, userId);

        Quiz saved = quizRepository.save(entity);

        return quizMapper.toResponseDTO(saved);
    }

    @Transactional
    public QuizResponseDTO findById(Long id) {
        Quiz quiz = findEntityOrThrow(id);

        if(quiz.isDeleted()) throw new ResourceNotFoundException("Quiz not found.");

        return quizMapper.toResponseDTO(quiz);
    }

    @Transactional
    public List<QuizListResponseDTO> listAllActive() {
        return quizRepository.findAllByDeletedFalse()
                .stream()
                .map(quizMapper::toListResponseDTO)
                .toList();
    }

    @Transactional
    public List<QuizResponseDTO> listByModule(Long moduleId) {
        findModuleOrThrow(moduleId);
        return quizRepository.findAllByModuleIdAndDeletedFalse(moduleId)
                .stream()
                .map(quizMapper::toResponseDTO)
                .toList();
    }

    @Transactional
    public QuizSummaryResponseDTO getSummary(Long quizId) {
        Quiz quiz = findEntityOrThrow(quizId);
        Long userId = authService.getAuthenticatedUserId();

        int totalQuestions = (int) quizQuestionRepository.countByQuizIdAndDeletedFalse(quizId);
        List<UserQuizAttempt> attempts = userQuizAttemptRepository
                .findAllByQuizIdAndUserIdAndDeletedFalseOrderByCreatedAtAsc(quizId, userId);

        UserQuizAttempt lastAttempt = attempts.isEmpty() ? null : attempts.getLast();

        BigDecimal lastScore = lastAttempt != null ? lastAttempt.getScore() : BigDecimal.ZERO;
        BigDecimal bestScore = attempts.stream()
                .map(UserQuizAttempt::getScore)
                .max(BigDecimal::compareTo)
                .orElse(BigDecimal.ZERO);

        int answered = lastAttempt == null ? 0 : (int) userQuizAttemptRepository.countAnswersByUserAndQuiz(userId, quizId);

        int correct = lastAttempt == null ? 0 : (int) userQuizAttemptRepository.countCorrectAnswersByUserAndQuiz(userId, quizId);

        boolean passed = lastAttempt != null && lastAttempt.getScore().compareTo(quiz.getMinScore()) >= 0;

        return new QuizSummaryResponseDTO(
                quiz.getId(),
                quiz.getName(),
                totalQuestions,
                answered,
                correct,
                bestScore,
                lastScore,
                passed,
                attempts.size(),
                quiz.getMaxAttempts()
        );
    }

    @Transactional
    public List<UserQuizAttemptListResponseDTO> listMyAttempts(Long quizId) {
        User user = authService.getAuthenticatedUserEntity();
        findEntityOrThrow(quizId);

        return userQuizAttemptRepository
                .findAllByUserIdAndQuizIdAndDeletedFalse(user.getId(), quizId)
                .stream()
                .map(userQuizAttemptMapper::toListResponseDTO)
                .toList();
    }

    private Module findModuleOrThrow(Long id) {
        return moduleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Module not found."));
    }
}