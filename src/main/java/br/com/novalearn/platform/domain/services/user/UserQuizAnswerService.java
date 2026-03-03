package br.com.novalearn.platform.domain.services.user;

import br.com.novalearn.platform.api.dtos.user.quizanswer.UserQuizAnswerCreateRequestDTO;
import br.com.novalearn.platform.api.dtos.user.quizanswer.UserQuizAnswerListResponseDTO;
import br.com.novalearn.platform.api.dtos.user.quizanswer.UserQuizAnswerResponseDTO;
import br.com.novalearn.platform.api.dtos.user.quizanswer.UserQuizAnswerUpdateRequestDTO;
import br.com.novalearn.platform.api.mappers.user.UserQuizAnswerMapper;
import br.com.novalearn.platform.core.exception.business.*;
import br.com.novalearn.platform.domain.entities.quiz.QuizAnswerOption;
import br.com.novalearn.platform.domain.entities.quiz.QuizQuestion;
import br.com.novalearn.platform.domain.entities.user.User;
import br.com.novalearn.platform.domain.entities.user.UserQuizAnswer;
import br.com.novalearn.platform.domain.enums.EnrollmentStatus;
import br.com.novalearn.platform.domain.repositories.quiz.QuizAnswerOptionRepository;
import br.com.novalearn.platform.domain.repositories.quiz.QuizQuestionRepository;
import br.com.novalearn.platform.domain.repositories.user.UserCourseRepository;
import br.com.novalearn.platform.domain.repositories.user.UserQuizAnswerRepository;
import br.com.novalearn.platform.domain.repositories.user.UserRepository;
import br.com.novalearn.platform.domain.services.BaseCrudService;
import br.com.novalearn.platform.domain.services.auth.AuthService;
import br.com.novalearn.platform.infra.time.provider.TimeProvider;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserQuizAnswerService extends BaseCrudService<UserQuizAnswer> {
    private final AuthService authService;
    private final UserQuizAnswerRepository userQuizAnswerRepository;
    private final UserRepository userRepository;
    private final UserCourseRepository userCourseRepository;
    private final QuizQuestionRepository quizQuestionRepository;
    private final QuizAnswerOptionRepository quizAnswerOptionRepository;
    private final UserQuizAnswerMapper userQuizAnswerMapper;
    private final TimeProvider timeProvider;

    public UserQuizAnswerService(
            AuthService authService,
            UserQuizAnswerRepository userQuizAnswerRepository,
            UserRepository userRepository,
            UserCourseRepository userCourseRepository,
            QuizQuestionRepository quizQuestionRepository,
            QuizAnswerOptionRepository quizAnswerOptionRepository,
            UserQuizAnswerMapper userQuizAnswerMapper,
            TimeProvider timeProvider
    ) {
        super(userQuizAnswerRepository, "User Quiz Answer", timeProvider);
        this.authService = authService;
        this.userQuizAnswerRepository = userQuizAnswerRepository;
        this.userRepository = userRepository;
        this.userCourseRepository = userCourseRepository;
        this.quizQuestionRepository = quizQuestionRepository;
        this.quizAnswerOptionRepository = quizAnswerOptionRepository;
        this.userQuizAnswerMapper = userQuizAnswerMapper;
        this.timeProvider = timeProvider;
    }

    @Transactional
    public UserQuizAnswerResponseDTO answerQuestion(
            UserQuizAnswerCreateRequestDTO dto,
            Long userId
    ) {
        if(!dto.getUserId().equals(userId)) {
            throw new ForbiddenOperationException("User mismatch.");
        }

        User user = findUserOrThrow(userId);
        QuizQuestion question = findQuizQuestionOrThrow(dto.getQuizQuestionId());
        QuizAnswerOption option = findQuizAnswerOptionOrThrow(dto.getQuizAnswerOptionId());

        validateUserHasAccessToQuestion(userId, question);

        userQuizAnswerRepository
                .findByUserIdAndQuizQuestionIdAndDeletedFalse(userId, question.getId())
                .ifPresent(a -> {
                    throw new InvalidStateException("Question already answered.");
                });

        UserQuizAnswer answer = UserQuizAnswer.answer(user, question, option, timeProvider.now(), timeProvider.now());

        return userQuizAnswerMapper.toResponseDTO(userQuizAnswerRepository.save(answer));
    }

    @Transactional
    public UserQuizAnswerResponseDTO changeAnswer(
            Long answerId,
            Long optionId
    ) {
        Long userId = authService.getAuthenticatedUserId();

        UserQuizAnswer answer = userQuizAnswerRepository
                .findByIdAndUserIdAndDeletedFalse(answerId, userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Answer not found.")
                );

        QuizAnswerOption option = findQuizAnswerOptionOrThrow(optionId);

        answer.changeAnswer(option, timeProvider.now());
        applyAuditUpdate(answer, userId);

        return userQuizAnswerMapper.toResponseDTO(userQuizAnswerRepository.save(answer));
    }

    @Transactional
    public List<UserQuizAnswerListResponseDTO> listMyAnswersByQuiz(Long quizId) {
        Long userId = authService.getAuthenticatedUserId();

        return userQuizAnswerRepository
                .findAllByUserIdAndQuizQuestionQuizIdAndDeletedFalse(userId, quizId)
                .stream()
                .map(userQuizAnswerMapper::toListResponseDTO)
                .toList();
    }

    @Transactional
    public UserQuizAnswerResponseDTO findMyAnswerByQuestion(Long questionId) {
        Long userId = authService.getAuthenticatedUserId();

        QuizQuestion question = findQuizQuestionOrThrow(questionId);
        validateUserHasAccessToQuestion(userId, question);

        UserQuizAnswer answer = userQuizAnswerRepository
                .findByUserIdAndQuizQuestionIdAndDeletedFalse(userId, questionId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Answer not found for this question.")
                );

        return userQuizAnswerMapper.toResponseDTO(answer);
    }

    @Transactional
    public UserQuizAnswerResponseDTO findMyAnswerById(Long id, Long userId) {
        UserQuizAnswer entity = userQuizAnswerRepository
                .findByIdAndUserIdAndDeletedFalse(id, userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User quiz answer not found.")
                );

        return userQuizAnswerMapper.toResponseDTO(entity);
    }

    @Transactional
    public List<UserQuizAnswerListResponseDTO> listAllActive() {
        return userQuizAnswerRepository.findAllByDeletedFalse()
                .stream()
                .map(userQuizAnswerMapper::toListResponseDTO)
                .toList();
    }

    private void validateUserHasAccessToQuestion(Long userId, QuizQuestion question) {
        Long courseId = question.getQuiz().getModule().getCourse().getId();

        boolean enrolled = userCourseRepository
                .existsByUserIdAndCourseIdAndDeletedFalse(
                        userId,
                        courseId
                );

        if(!enrolled) throw new ForbiddenOperationException("User is not enrolled in this course.");
    }

    private User findUserOrThrow(Long id) {
        return userRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("User not found")
        );
    }

    private QuizQuestion findQuizQuestionOrThrow(Long id) {
        return quizQuestionRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Quiz Question not found")
        );
    }

    private QuizAnswerOption findQuizAnswerOptionOrThrow(Long id) {
        return quizAnswerOptionRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Quiz Answer Option not found")
        );
    }
}