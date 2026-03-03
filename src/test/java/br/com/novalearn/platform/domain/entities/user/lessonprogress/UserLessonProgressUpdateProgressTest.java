package br.com.novalearn.platform.domain.entities.user.lessonprogress;

import br.com.novalearn.platform.core.exception.business.InvalidStateException;
import br.com.novalearn.platform.core.exception.business.ValidationException;
import br.com.novalearn.platform.domain.entities.lesson.Lesson;
import br.com.novalearn.platform.domain.entities.user.User;
import br.com.novalearn.platform.domain.entities.user.UserLessonProgress;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static br.com.novalearn.platform.factories.entities.lesson.CreateLessonFactory.createInitializedLesson;
import static br.com.novalearn.platform.factories.entities.user.CreateUserFactory.createInitializedUser;
import static org.junit.jupiter.api.Assertions.*;

public class UserLessonProgressUpdateProgressTest {
    private User user;
    private Lesson lesson;
    private UserLessonProgress progress;

    @BeforeEach
    void setUp() {
        user = createInitializedUser();
        lesson = createInitializedLesson();
        progress = createStartedProgress();
    }

    private UserLessonProgress createStartedProgress() {
        return UserLessonProgress.start(
                user,
                lesson,
                LocalDateTime.now().minusMinutes(10)
        );
    }

    @Test
    void should_update_progress() {
        LocalDateTime now = LocalDateTime.now();

        progress.updateProgress(50, now);

        assertEquals(50, progress.getProgressPercent());
        assertEquals(now, progress.getLastViewAt());
        assertFalse(progress.isCompleted());
    }

    @Test
    void should_complete_when_progress_is_100() {
        LocalDateTime now = LocalDateTime.now();

        progress.updateProgress(100, now);

        assertTrue(progress.isCompleted());
        assertEquals(100, progress.getProgressPercent());
        assertEquals(now, progress.getCompletedAt());
    }

    @Test
    void should_throw_when_invalid_progress() {
        ValidationException ex = assertThrows(
                ValidationException.class,
                () -> progress.updateProgress(120, LocalDateTime.now())
        );

        assertEquals("Progress must be between 0 and 100.", ex.getMessage());
    }

    @Test
    void should_throw_when_completed() {
        progress.complete(LocalDateTime.now());

        InvalidStateException ex = assertThrows(
                InvalidStateException.class,
                () -> progress.updateProgress(50, LocalDateTime.now())
        );

        assertEquals("Completed lesson cannot have progress updated.", ex.getMessage());
    }

    @Test
    void should_throw_when_date_is_null() {
        ValidationException ex = assertThrows(
                ValidationException.class,
                () -> progress.updateProgress(50, null)
        );

        assertEquals("Progress update date is required.", ex.getMessage());
    }
}