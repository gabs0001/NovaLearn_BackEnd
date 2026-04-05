package br.com.novalearn.platform.domain.services.user;

import br.com.novalearn.platform.api.dtos.user.lessonprogress.UserLessonProgressListResponseDTO;
import br.com.novalearn.platform.api.dtos.user.lessonprogress.UserLessonProgressResponseDTO;
import br.com.novalearn.platform.api.dtos.user.lessonprogress.UserLessonProgressUpdateRequestDTO;
import br.com.novalearn.platform.api.mappers.user.UserLessonProgressMapper;
import br.com.novalearn.platform.core.exception.business.InvalidStateException;
import br.com.novalearn.platform.domain.entities.user.UserLessonProgress;
import br.com.novalearn.platform.domain.repositories.lesson.LessonRepository;
import br.com.novalearn.platform.domain.repositories.user.UserLessonProgressRepository;
import br.com.novalearn.platform.domain.repositories.user.UserRepository;
import br.com.novalearn.platform.infra.time.provider.TimeProvider;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class MyLessonProgressService {
    private final UserLessonProgressRepository userLessonProgressRepository;
    private final UserLessonProgressMapper userLessonProgressMapper;
    private final TimeProvider timeProvider;
    private final UserRepository userRepository;
    private final LessonRepository lessonRepository;

    public MyLessonProgressService(
            UserLessonProgressRepository userLessonProgressRepository,
            UserLessonProgressMapper userLessonProgressMapper,
            TimeProvider timeProvider,
            UserRepository userRepository,
            LessonRepository lessonRepository
    ) {
        this.userLessonProgressRepository = userLessonProgressRepository;
        this.userLessonProgressMapper = userLessonProgressMapper;
        this.timeProvider = timeProvider;
        this.userRepository = userRepository;
        this.lessonRepository = lessonRepository;
    }

    @Transactional
    public List<UserLessonProgressListResponseDTO> listMyProgress(Long userId) {
        return userLessonProgressRepository.findAllByUserIdAndDeletedFalse(userId)
                .stream()
                .map(userLessonProgressMapper::toListResponseDTO)
                .toList();
    }

    @Transactional
    public UserLessonProgressResponseDTO completeLesson(Long lessonId, Long userId) {
        UserLessonProgress progress = userLessonProgressRepository
                .findByUserIdAndLessonIdAndDeletedFalse(userId, lessonId)
                .orElseThrow(() -> new InvalidStateException("Lesson not started."));

        if(!progress.isCompleted()) {
            progress.updateProgress(100, timeProvider.now());
            progress.auditUpdate(userId, timeProvider.now());
        }

        return userLessonProgressMapper.toResponseDTO(progress);
    }

    @Transactional
    public UserLessonProgressResponseDTO updateProgress(
            Long lessonId,
            Long userId,
            UserLessonProgressUpdateRequestDTO request
    ) {
        LocalDateTime now = timeProvider.now();

        UserLessonProgress progress = userLessonProgressRepository
                .findByUserIdAndLessonIdAndDeletedFalse(userId, lessonId)
                .orElseGet(() -> createNewProgress(userId, lessonId, now));

        if(progress.getId() != null) {
            progress.registerView(now);
        }

        progress.updateProgress(request.getProgressPercent(), now);
        progress.auditUpdate(userId, now);

        return userLessonProgressMapper.toResponseDTO(userLessonProgressRepository.save(progress));
    }

    private UserLessonProgress createNewProgress(
            Long userId,
            Long lessonId,
            LocalDateTime now
    ) {
        var user = userRepository.findById(userId)
                .orElseThrow(() -> new InvalidStateException("User not found"));

        var lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new InvalidStateException("Lesson not found"));

        UserLessonProgress progress = UserLessonProgress.start(user, lesson, now);
        progress.auditCreate(userId, now);
        return progress;
    }
}