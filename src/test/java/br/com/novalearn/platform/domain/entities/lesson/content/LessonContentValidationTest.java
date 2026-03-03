package br.com.novalearn.platform.domain.entities.lesson.content;

import br.com.novalearn.platform.core.exception.business.ValidationException;
import br.com.novalearn.platform.domain.entities.category.Category;
import br.com.novalearn.platform.domain.entities.lesson.Lesson;
import br.com.novalearn.platform.domain.entities.lesson.LessonContent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static br.com.novalearn.platform.factories.entities.lesson.CreateContentFactory.createInitializedContent;
import static br.com.novalearn.platform.factories.entities.lesson.CreateLessonFactory.createInitializedLesson;
import static org.junit.jupiter.api.Assertions.*;

public class LessonContentValidationTest {
    private Lesson lesson;
    private LessonContent content;

    @BeforeEach
    void setUp() {
        lesson = createInitializedLesson();
        content = createInitializedContent(lesson, LocalDateTime.now());
    }

    @Test
    void should_throw_exception_when_lesson_is_null() {
        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> LessonContent.create(
                        null,
                        "url",
                        null,
                        null,
                        null,
                        false,
                        false,
                        null,
                        5L,
                        LocalDateTime.now()
                )
        );

        assertEquals("Lesson cannot be null.", exception.getMessage());
    }

    @Test
    void should_throw_exception_when_no_content_is_provided() {
        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> LessonContent.create(
                        lesson,
                        null,
                        null,
                        null,
                        "   ",
                        false,
                        false,
                        null,
                        5L,
                        LocalDateTime.now()
                )
        );

        assertEquals("Lesson Content must have at least one type of content.", exception.getMessage());
    }

    @Test
    void should_not_allow_re_attaching_to_another_lesson() {
        Lesson anotherLesson = createInitializedLesson();

        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> content.attachToLesson(anotherLesson)
        );

        assertEquals("LessonContent is already attached to a Lesson.", exception.getMessage());
    }

    @Test
    void should_allow_creation_with_text_content_only() {
        LessonContent content = LessonContent.create(
                lesson,
                null,
                null,
                null,
                "Some text",
                false,
                false,
                null,
                5L,
                LocalDateTime.now()
        );

        assertNotNull(content);
        assertEquals("Some text", content.getContent());
    }

    @Test
    void should_allow_creation_with_material_only() {
        LessonContent content = LessonContent.create(
                lesson,
                null,
                null,
                "https://pdf.com/file",
                null,
                false,
                false,
                null,
                5L,
                LocalDateTime.now()
        );

        assertNotNull(content);
        assertNotNull(content.getMaterialUrl());
    }
}