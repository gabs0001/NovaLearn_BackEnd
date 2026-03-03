package br.com.novalearn.platform.api.mappers.user;

import br.com.novalearn.platform.api.dtos.user.lessonprogress.UserLessonProgressListResponseDTO;
import br.com.novalearn.platform.api.dtos.user.lessonprogress.UserLessonProgressResponseDTO;
import br.com.novalearn.platform.domain.entities.lesson.Lesson;
import br.com.novalearn.platform.domain.entities.user.User;
import br.com.novalearn.platform.domain.entities.user.UserLessonProgress;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static br.com.novalearn.platform.factories.entities.lesson.CreateLessonFactory.createInitializedLesson;
import static br.com.novalearn.platform.factories.entities.user.CreateUserFactory.createInitializedUser;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class UserLessonProgressMapperTest {
    private LocalDateTime now;
    private UserLessonProgressMapper mapper;
    private User user;
    private Lesson lesson;

    @BeforeEach
    void setUp() {
        now = LocalDateTime.now();
        mapper = new UserLessonProgressMapper();
        user = createInitializedUser();
        lesson = createInitializedLesson();
    }

    @Test
    void should_map_entity_to_response_dto() {
        UserLessonProgress progress = UserLessonProgress.start(user, lesson, now);

        progress.updateProgress(75, now.plusMinutes(5));
        progress.complete(now.plusMinutes(10));

        progress.auditCreate(1L, now);
        progress.auditUpdate(2L, now.plusMinutes(15));

        UserLessonProgressResponseDTO dto = mapper.toResponseDTO(progress);

        assertThat(dto).isNotNull();

        assertThat(dto.getId()).isEqualTo(progress.getId());
        assertThat(dto.getActive()).isEqualTo(progress.isActive());
        assertThat(dto.getDeleted()).isEqualTo(progress.isDeleted());

        assertThat(dto.getCreatedAt()).isEqualTo(progress.getCreatedAt());
        assertThat(dto.getUpdatedAt()).isEqualTo(progress.getUpdatedAt());
        assertThat(dto.getObservations()).isEqualTo(progress.getObservations());

        assertThat(dto.getUserId()).isEqualTo(user.getId());
        assertThat(dto.getLessonId()).isEqualTo(lesson.getId());

        assertThat(dto.getCompleted()).isEqualTo(progress.isCompleted());
        assertThat(dto.getCompletedAt()).isEqualTo(progress.getCompletedAt());

        assertThat(dto.getProgressPercent()).isEqualTo(progress.getProgressPercent());
        assertThat(dto.getViews()).isEqualTo(progress.getViews());

        assertThat(dto.getFirstViewAt()).isEqualTo(progress.getFirstViewAt());
        assertThat(dto.getLastViewAt()).isEqualTo(progress.getLastViewAt());

        assertThat(dto.getCreatedBy()).isEqualTo(progress.getCreatedBy());
        assertThat(dto.getUpdatedBy()).isEqualTo(progress.getUpdatedBy());
    }

    @Test
    void should_map_entity_to_list_response_dto() {
        UserLessonProgress progress = UserLessonProgress.start(user, lesson, now);

        progress.updateProgress(40, now.plusMinutes(3));

        progress.auditCreate(1L, now);

        UserLessonProgressListResponseDTO dto = mapper.toListResponseDTO(progress);

        assertThat(dto).isNotNull();

        assertThat(dto.getId()).isEqualTo(progress.getId());

        assertThat(dto.getUserId()).isEqualTo(user.getId());
        assertThat(dto.getLessonId()).isEqualTo(lesson.getId());

        assertThat(dto.getCompleted()).isEqualTo(progress.isCompleted());
        assertThat(dto.getProgressPercent()).isEqualTo(progress.getProgressPercent());
        assertThat(dto.getViews()).isEqualTo(progress.getViews());

        assertThat(dto.getActive()).isEqualTo(progress.isActive());
        assertThat(dto.getDeleted()).isEqualTo(progress.isDeleted());
    }
}