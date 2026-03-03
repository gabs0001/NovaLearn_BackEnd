package br.com.novalearn.platform.domain.services.user;

import br.com.novalearn.platform.api.dtos.user.lessonprogress.UserLessonProgressListResponseDTO;
import br.com.novalearn.platform.api.dtos.user.lessonprogress.UserLessonProgressResponseDTO;
import br.com.novalearn.platform.api.mappers.user.UserLessonProgressMapper;
import br.com.novalearn.platform.core.exception.business.InvalidStateException;
import br.com.novalearn.platform.domain.entities.user.UserLessonProgress;
import br.com.novalearn.platform.domain.repositories.user.UserLessonProgressRepository;
import br.com.novalearn.platform.infra.time.provider.TimeProvider;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class MyLessonProgressService {
    private final UserLessonProgressRepository userLessonProgressRepository;
    private final UserLessonProgressMapper userLessonProgressMapper;
    private final TimeProvider timeProvider;

    public MyLessonProgressService(
            UserLessonProgressRepository userLessonProgressRepository,
            UserLessonProgressMapper userLessonProgressMapper,
            TimeProvider timeProvider
    ) {
        this.userLessonProgressRepository = userLessonProgressRepository;
        this.userLessonProgressMapper = userLessonProgressMapper;
        this.timeProvider = timeProvider;
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
}