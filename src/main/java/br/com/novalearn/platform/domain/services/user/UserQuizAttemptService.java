package br.com.novalearn.platform.domain.services.user;

import br.com.novalearn.platform.api.dtos.user.quizattempt.UserQuizAttemptCreateRequestDTO;
import br.com.novalearn.platform.api.dtos.user.quizattempt.UserQuizAttemptListResponseDTO;
import br.com.novalearn.platform.api.dtos.user.quizattempt.UserQuizAttemptResponseDTO;
import br.com.novalearn.platform.api.dtos.user.quizattempt.UserQuizAttemptUpdateRequestDTO;
import br.com.novalearn.platform.api.mappers.user.UserQuizAttemptMapper;
import br.com.novalearn.platform.core.exception.business.InvalidStateException;
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
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserQuizAttemptService {
    private final AuthService authService;
    private final UserQuizAttemptRepository userQuizAttemptRepository;
    private final QuizRepository quizRepository;
    private final UserQuizAttemptMapper userQuizAttemptMapper;
    private final TimeProvider timeProvider;

    public UserQuizAttemptService(
            AuthService authService,
            UserQuizAttemptRepository userQuizAttemptRepository,
            QuizRepository quizRepository,
            UserQuizAttemptMapper userQuizAttemptMapper,
            TimeProvider timeProvider
    ) {
        this.authService = authService;
        this.userQuizAttemptRepository = userQuizAttemptRepository;
        this.quizRepository = quizRepository;
        this.userQuizAttemptMapper = userQuizAttemptMapper;
        this.timeProvider = timeProvider;
    }

    @Transactional
    public UserQuizAttemptResponseDTO startAttempt(UserQuizAttemptCreateRequestDTO dto, Long userId) {
        User user = authService.getAuthenticatedUserEntity();
        Quiz quiz = findQuizOrThrow(dto.getQuizId());

        validateStartAttempt(userId, quiz);

        int seq = (int) userQuizAttemptRepository.countByUserIdAndQuizIdAndDeletedFalse(userId, quiz.getId()) + 1;

        UserQuizAttempt attempt = UserQuizAttempt.start(user, quiz, seq, userId, timeProvider.now());

        UserQuizAttempt saved = userQuizAttemptRepository.save(attempt);

        return userQuizAttemptMapper.toResponseDTO(saved);
    }

    private void validateStartAttempt(Long userId, Quiz quiz) {
        if (
                userQuizAttemptRepository.existsByUserIdAndQuizIdAndStatusAndDeletedFalse(
                        userId, quiz.getId(), QuizAttemptStatus.STARTED
                )
        ) {
            throw new InvalidStateException("There is already an active attempt for this quiz.");
        }

        if(quiz.getMaxAttempts() != null) {
            long attempts =
                    userQuizAttemptRepository.countByUserIdAndQuizIdAndDeletedFalse(userId, quiz.getId());

            if(attempts >= quiz.getMaxAttempts()) {
                throw new ValidationException("Maximum number of attempts reached.");
            }
        }
    }

    @Transactional
    public UserQuizAttemptResponseDTO finishAttempt(
            Long id,
            UserQuizAttemptUpdateRequestDTO dto,
            Long userId
    ) {
        UserQuizAttempt attempt = findMyAttemptOrThrow(id, userId);

        attempt.finish(
                dto.getScore(),
                dto.getMaxScore(),
                timeProvider.now(),
                userId,
                timeProvider.now()
        );

        //attempt.setObservations(dto.getObservations());

        UserQuizAttempt saved = userQuizAttemptRepository.save(attempt);

        return userQuizAttemptMapper.toResponseDTO(saved);
    }

    @Transactional
    public List<UserQuizAttemptListResponseDTO> listMyAttempts(Long userId) {
        return userQuizAttemptRepository.findAllByUserIdAndDeletedFalseOrderByStartedAtDesc(userId)
                .stream()
                .map(userQuizAttemptMapper::toListResponseDTO)
                .toList();
    }

    @Transactional
    public UserQuizAttemptResponseDTO findMyAttemptById(Long id, Long userId) {
        return userQuizAttemptMapper.toResponseDTO(
                findMyAttemptOrThrow(id, userId)
        );
    }

    @Transactional
    public void deleteMyAttempt(Long id, Long userId) {
        UserQuizAttempt attempt = findMyAttemptOrThrow(id, userId);
        attempt.delete();
    }

    @Transactional
    public void restoreMyAttempt(Long id, Long userId) {
        UserQuizAttempt attempt = userQuizAttemptRepository.findByIdAndUserId(id, userId).orElseThrow(() -> new ResourceNotFoundException("Attempt not found."));
        attempt.restore();
    }

    @Transactional
    public List<UserQuizAttemptListResponseDTO> listMyAttemptsByQuiz(Long quizId, Long userId) {
        return userQuizAttemptRepository
                .findAllByUserIdAndQuizIdAndDeletedFalse(userId, quizId)
                .stream()
                .map(userQuizAttemptMapper::toListResponseDTO)
                .toList();
    }


    private UserQuizAttempt findMyAttemptOrThrow(Long id, Long userId) {
        return userQuizAttemptRepository.findByIdAndUserIdAndDeletedFalse(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Quiz attempt not found."));
    }

    private Quiz findQuizOrThrow(Long id) {
        return quizRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Quiz not found."));
    }
}