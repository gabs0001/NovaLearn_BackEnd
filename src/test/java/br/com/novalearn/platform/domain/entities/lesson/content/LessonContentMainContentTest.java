package br.com.novalearn.platform.domain.entities.lesson.content;

import br.com.novalearn.platform.core.exception.business.ValidationException;
import br.com.novalearn.platform.domain.entities.lesson.Lesson;
import br.com.novalearn.platform.domain.entities.lesson.LessonContent;
import br.com.novalearn.platform.domain.repositories.lesson.LessonContentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;

import static br.com.novalearn.platform.factories.entities.lesson.CreateContentFactory.createInitializedContent;
import static br.com.novalearn.platform.factories.entities.lesson.CreateLessonFactory.createInitializedLesson;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class LessonContentMainContentTest {
    @Mock
    private LessonContentRepository repository;

    private Lesson lesson;
    private LessonContent content;

    @BeforeEach
    void setUp() {
        lesson = createInitializedLesson();
        content = createInitializedContent(lesson, LocalDateTime.now());
    }

    @Test
    void should_mark_as_main_when_no_other_main_content_exists() {
        Long lessonId = content.getLesson().getId();

        when(repository.existsByLessonIdAndMainContentTrue(lessonId))
                .thenReturn(false);

        content.markAsMain(repository);

        assertTrue(content.isMainContent());

        verify(repository).existsByLessonIdAndMainContentTrue(lessonId);
    }

    @Test
    void should_throw_exception_when_another_main_content_already_exists() {
        Long lessonId = content.getLesson().getId();

        when(repository.existsByLessonIdAndMainContentTrue(lessonId))
                .thenReturn(true);

        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> content.markAsMain(repository)
        );

        assertEquals("Lesson already has a main content.", exception.getMessage());

        assertTrue(content.isMainContent());

        verify(repository).existsByLessonIdAndMainContentTrue(lessonId);
    }

    @Test
    void should_fail_if_lesson_is_null_when_marking_as_main() {
        ReflectionTestUtils.setField(content, "lesson", null);

        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> content.markAsMain(repository)
        );

        assertNotNull(exception);
    }
}