package br.com.novalearn.platform.factories.entities.lesson;

import br.com.novalearn.platform.domain.entities.lesson.Lesson;
import br.com.novalearn.platform.domain.entities.lesson.LessonContent;

import java.time.LocalDateTime;

import static br.com.novalearn.platform.factories.entities.lesson.CreateLessonFactory.createInitializedLesson;

public final class CreateContentFactory {
    public static LessonContent createInitializedContent(Lesson lesson, LocalDateTime now) {
        return LessonContent.create(
                lesson,
                "https://video.com/lesson.mp4",
                "https://transcript.com/text",
                "https://material.com/file.pdf",
                "Main content",
                true,
                true,
                "Observations",
                5L,
                now
        );
    }

    public static LessonContent createNullableLesson(Lesson lesson, LocalDateTime now) {
        return LessonContent.create(
                lesson,
                "https://video.com/lesson.mp4",
                "https://transcript.com/text",
                null,
                null,
                true,
                true,
                "Observations",
                5L,
                now
        );
    }
}