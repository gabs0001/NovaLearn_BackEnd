package br.com.novalearn.platform.domain.entities.lesson.content;

import br.com.novalearn.platform.core.exception.business.ValidationException;
import br.com.novalearn.platform.domain.entities.lesson.Lesson;
import br.com.novalearn.platform.domain.entities.lesson.LessonContent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import static br.com.novalearn.platform.factories.entities.lesson.CreateContentFactory.*;
import static br.com.novalearn.platform.factories.entities.lesson.CreateLessonFactory.createInitializedLesson;
import static org.junit.jupiter.api.Assertions.*;

public class LessonContentCreateTest {
    private Lesson lesson;
    private LessonContent content;
    private LocalDateTime now;

    @BeforeEach
    void setUp() {
        now  =LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS);
        lesson = createInitializedLesson();
        content = createInitializedContent(lesson, now);
    }

    @Test
    void should_create_lesson_content_with_valid_data() {
        assertNotNull(content);

        assertEquals(lesson, content.getLesson());

        assertEquals("https://video.com/lesson.mp4", content.getVideoUrl());
        assertEquals("https://transcript.com/text", content.getTranscriptUrl());
        assertEquals("https://material.com/file.pdf", content.getMaterialUrl());
        assertEquals("Main content", content.getContent());

        assertTrue(content.isHasQuiz());
        assertTrue(content.isMainContent());

        assertTrue(content.isActive());
        assertFalse(content.isDeleted());

        assertEquals(5L, content.getCreatedBy());
        assertEquals(now, content.getCreatedAt());
    }

    @Test
    void should_create_with_only_video_content() {
        LessonContent content = createNullableLesson(lesson, now);

        assertNotNull(content);

        assertEquals("https://video.com/lesson.mp4", content.getVideoUrl());

        assertNull(content.getContent());
        assertNull(content.getMaterialUrl());
    }

    @Test
    void should_throw_when_no_content_is_provided() {
        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> LessonContent.create(
                        lesson,
                        null,
                        null,
                        null,
                        null,
                        false,
                        false,
                        null,
                        5L,
                        now
                )
        );

        assertEquals("Lesson Content must have at least one type of content.", exception.getMessage());
    }

    @Test
    void should_throw_when_lesson_is_null() {
        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> LessonContent.create(
                        null,
                        "video",
                        null,
                        null,
                        null,
                        false,
                        false,
                        null,
                        5L,
                        now
                )
        );

        assertEquals("Lesson cannot be null.", exception.getMessage());
    }

    @Test
    void should_trim_all_text_fields() {
        assertEquals("https://video.com/lesson.mp4", content.getVideoUrl());
        assertEquals("https://transcript.com/text", content.getTranscriptUrl());
        assertEquals("https://material.com/file.pdf", content.getMaterialUrl());
        assertEquals("Main content", content.getContent());
        assertEquals("Observations", content.getObservations());
    }
}