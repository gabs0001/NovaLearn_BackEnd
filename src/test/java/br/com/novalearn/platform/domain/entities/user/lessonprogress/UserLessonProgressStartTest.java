package br.com.novalearn.platform.domain.entities.user.lessonprogress;

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

public class UserLessonProgressStartTest {
    private User user;
    private Lesson lesson;

    @BeforeEach
    void setUp() {
        user = createInitializedUser();
        lesson = createInitializedLesson();
    }

    @Test
    void should_start_progress_correctly() {
        LocalDateTime now = LocalDateTime.now();
        UserLessonProgress progress = UserLessonProgress.start(user, lesson, now);

        assertNotNull(progress);

        assertEquals(user, progress.getUser());
        assertEquals(lesson, progress.getLesson());

        assertEquals(now, progress.getFirstViewAt());
        assertEquals(now, progress.getLastViewAt());

        assertEquals(0, progress.getProgressPercent());
        assertEquals(1, progress.getViews());

        assertFalse(progress.isCompleted());
        assertNull(progress.getCompletedAt());
    }

    @Test
    void should_throw_when_user_is_null() {
        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> UserLessonProgress.start(null, lesson, LocalDateTime.now())
        );

        assertEquals("User cannot be null.", exception.getMessage());
    }

    @Test
    void should_throw_when_lesson_is_null() {
        ValidationException ex = assertThrows(
                ValidationException.class,
                () -> UserLessonProgress.start(user, null, LocalDateTime.now())
        );

        assertEquals("Lesson cannot be null.", ex.getMessage());
    }

    @Test
    void should_throw_when_date_is_null() {
        ValidationException ex = assertThrows(
                ValidationException.class,
                () -> UserLessonProgress.start(user, lesson, null)
        );

        assertEquals("Start date is required.", ex.getMessage());
    }
}