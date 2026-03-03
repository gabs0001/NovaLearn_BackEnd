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

public class UserLessonProgressRegisterViewTest {
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
    void should_register_new_view() {
        LocalDateTime now = LocalDateTime.now();

        int oldViews = progress.getViews();

        progress.registerView(now);

        assertEquals(now, progress.getLastViewAt());
        assertEquals(oldViews + 1, progress.getViews());
    }

    @Test
    void should_throw_when_date_is_null() {
        ValidationException ex = assertThrows(
                ValidationException.class,
                () -> progress.registerView(null)
        );

        assertEquals("View date is required.", ex.getMessage());
    }

    @Test
    void should_throw_when_date_goes_backwards() {
        LocalDateTime past = progress.getLastViewAt().minusMinutes(5);

        InvalidStateException ex = assertThrows(
                InvalidStateException.class,
                () -> progress.registerView(past)
        );

        assertEquals("View date cannot go backwards.", ex.getMessage());
    }
}