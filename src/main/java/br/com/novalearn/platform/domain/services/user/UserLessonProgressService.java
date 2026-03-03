package br.com.novalearn.platform.domain.services.user;

import br.com.novalearn.platform.api.dtos.user.lessonprogress.UserLessonProgressListResponseDTO;
import br.com.novalearn.platform.api.dtos.user.lessonprogress.UserLessonProgressResponseDTO;
import br.com.novalearn.platform.api.mappers.user.UserLessonProgressMapper;
import br.com.novalearn.platform.core.exception.business.ResourceNotFoundException;
import br.com.novalearn.platform.domain.entities.user.UserLessonProgress;
import br.com.novalearn.platform.domain.repositories.user.UserLessonProgressRepository;
import br.com.novalearn.platform.domain.services.BaseCrudService;
import br.com.novalearn.platform.infra.time.provider.TimeProvider;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserLessonProgressService extends BaseCrudService<UserLessonProgress> {
    private final UserLessonProgressRepository userLessonProgressRepository;
    private final UserLessonProgressMapper userLessonProgressMapper;

    public UserLessonProgressService(
            UserLessonProgressRepository userLessonProgressRepository,
            UserLessonProgressMapper userLessonProgressMapper,
            TimeProvider timeProvider
    ) {
        super(userLessonProgressRepository, "User Lesson Progress", timeProvider);
        this.userLessonProgressRepository = userLessonProgressRepository;
        this.userLessonProgressMapper = userLessonProgressMapper;
    }

    @Transactional
    public UserLessonProgressResponseDTO findById(Long id) {
        UserLessonProgress entity = findEntityOrThrow(id);

        entity.ensureNotDeleted();

        return userLessonProgressMapper.toResponseDTO(entity);
    }

    @Transactional
    public List<UserLessonProgressListResponseDTO> listAllActive() {
        return userLessonProgressRepository.findAllByDeletedFalse()
                .stream()
                .map(userLessonProgressMapper::toListResponseDTO)
                .toList();
    }
}