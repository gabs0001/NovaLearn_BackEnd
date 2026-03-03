package br.com.novalearn.platform.domain.entities.user.lessonprogress;

import br.com.novalearn.platform.core.exception.business.InvalidStateException;
import br.com.novalearn.platform.domain.entities.lesson.Lesson;
import br.com.novalearn.platform.domain.entities.user.User;
import br.com.novalearn.platform.domain.entities.user.UserLessonProgress;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static br.com.novalearn.platform.factories.entities.lesson.CreateLessonFactory.createInitializedLesson;
import static br.com.novalearn.platform.factories.entities.user.CreateUserFactory.createInitializedUser;
import static org.junit.jupiter.api.Assertions.*;

public class UserLessonProgressCompleteTest {
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
    void should_complete_lesson() {
        LocalDateTime now = LocalDateTime.now();

        progress.complete(now);

        assertTrue(progress.isCompleted());
        assertEquals(100, progress.getProgressPercent());
        assertEquals(now, progress.getCompletedAt());
    }

    @Test
    void should_throw_when_already_completed() {
        progress.complete(LocalDateTime.now());

        InvalidStateException ex = assertThrows(
                InvalidStateException.class,
                () -> progress.complete(LocalDateTime.now())
        );

        assertEquals("Lesson already completed.", ex.getMessage());
    }

    @Test
    void should_throw_when_date_is_null() {
        NullPointerException ex = assertThrows(
                NullPointerException.class,
                () -> progress.complete(null)
        );

        assertEquals("Completion date is required.", ex.getMessage());
    }
}