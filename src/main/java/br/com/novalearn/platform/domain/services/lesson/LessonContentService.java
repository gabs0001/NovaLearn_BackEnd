package br.com.novalearn.platform.domain.services.lesson;

import br.com.novalearn.platform.api.dtos.lesson.content.LessonContentCreateRequestDTO;
import br.com.novalearn.platform.api.dtos.lesson.content.LessonContentResponseDTO;
import br.com.novalearn.platform.api.mappers.lesson.LessonContentMapper;
import br.com.novalearn.platform.core.exception.business.ResourceNotFoundException;
import br.com.novalearn.platform.core.exception.business.ValidationException;
import br.com.novalearn.platform.domain.entities.lesson.Lesson;
import br.com.novalearn.platform.domain.entities.lesson.LessonContent;
import br.com.novalearn.platform.domain.repositories.lesson.LessonContentRepository;
import br.com.novalearn.platform.domain.repositories.lesson.LessonRepository;
import br.com.novalearn.platform.domain.services.BaseCrudService;
import br.com.novalearn.platform.infra.time.provider.TimeProvider;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class LessonContentService extends BaseCrudService<LessonContent> {
    private final LessonContentRepository lessonContentRepository;
    private final LessonRepository lessonRepository;
    private final LessonContentMapper lessonContentMapper;

    public LessonContentService(
            LessonContentRepository lessonContentRepository,
            LessonRepository lessonRepository,
            LessonContentMapper lessonContentMapper,
            TimeProvider timeProvider
    ) {
        super(lessonContentRepository, "Lesson Content", timeProvider);
        this.lessonContentRepository = lessonContentRepository;
        this.lessonRepository = lessonRepository;
        this.lessonContentMapper = lessonContentMapper;
    }

    @Transactional
    public LessonContentResponseDTO create(Long lessonId, LessonContentCreateRequestDTO dto, Long userId) {
        Lesson lesson = findLessonOrThrow(lessonId);
        lesson.ensureNotDeleted();

        if(dto.getMainContent() && lessonContentRepository.existsByLessonIdAndMainContentTrue(lessonId)) {
            throw new ValidationException("Lesson already has a main content.");
        }

        LessonContent entity = LessonContent.create(
                lesson,
                dto.getVideoUrl(),
                dto.getTranscriptUrl(),
                dto.getMaterialUrl(),
                dto.getContent(),
                dto.getHasQuiz(),
                dto.getMainContent(),
                dto.getObservations(),
                userId,
                timeProvider.now()
        );

        applyAuditCreate(entity, userId);

        return lessonContentMapper.toResponseDTO(lessonContentRepository.save(entity));
    }

    @Transactional
    public LessonContentResponseDTO findByLesson(Long lessonId) {
        Lesson lesson = findLessonOrThrow(lessonId);
        lesson.ensureNotDeleted();

        LessonContent content = lessonContentRepository.findByLessonIdAndDeletedFalse(lessonId)
                .orElseThrow(
                        () -> new ResourceNotFoundException("Lesson Content not found.")
                );

        return lessonContentMapper.toResponseDTO(content);
    }

    private Lesson findLessonOrThrow(Long id) {
        return lessonRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Lesson not found")
        );
    }
}