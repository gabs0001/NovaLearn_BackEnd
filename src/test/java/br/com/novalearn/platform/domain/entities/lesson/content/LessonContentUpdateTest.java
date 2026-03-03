package br.com.novalearn.platform.domain.entities.lesson.content;

import br.com.novalearn.platform.core.exception.business.ValidationException;
import br.com.novalearn.platform.domain.entities.lesson.Lesson;
import br.com.novalearn.platform.domain.entities.lesson.LessonContent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static br.com.novalearn.platform.factories.entities.lesson.CreateContentFactory.createInitializedContent;
import static br.com.novalearn.platform.factories.entities.lesson.CreateLessonFactory.createInitializedLesson;
import static org.junit.jupiter.api.Assertions.*;

public class LessonContentUpdateTest {
    private Lesson lesson;
    private LessonContent content;

    @BeforeEach
    void setUp() {
        lesson = createInitializedLesson();
        content = createInitializedContent(lesson, LocalDateTime.now());
    }

    @Test
    void should_change_video_url() {
        content.changeVideoUrl("https://video.com/2");
        assertEquals("https://video.com/2", content.getVideoUrl());
    }

    @Test
    void should_remove_video_when_other_content_exists() {
        content.changeContent("Text content");
        content.changeVideoUrl(null);

        assertNull(content.getVideoUrl());
        assertEquals("Text content", content.getContent());
    }

    @Test
    void should_change_material_url() {
        content.changeMaterialUrl("https://material.com/1");
        assertEquals("https://material.com/1", content.getMaterialUrl());
    }

    @Test
    void should_change_transcript_url() {
        content.changeTranscriptUrl("https://transcript.com/1");
        assertEquals("https://transcript.com/1", content.getTranscriptUrl());
    }

    @Test
    void should_change_text_content() {
        content.changeContent("New content");
        assertEquals("New content", content.getContent());
    }

    @Test
    void should_allow_switching_between_content_types() {
        // remove video
        content.changeContent("Text here");
        content.changeVideoUrl(null);

        assertNull(content.getVideoUrl());
        assertEquals("Text here", content.getContent());

        content.changeMaterialUrl("https://material.com/1");
        content.changeContent(null);

        assertNull(content.getContent());
        assertEquals("https://material.com/1", content.getMaterialUrl());
    }

    @Test
    void should_throw_when_all_contents_are_blank() {
        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> {
                    content.changeVideoUrl(" ");
                    content.changeContent(" ");
                    content.changeMaterialUrl(" ");
                }
        );

        assertEquals("Lesson Content must have at least one type of content.", exception.getMessage());
    }
}