package br.com.novalearn.platform.factories.dtos.lesson;

import br.com.novalearn.platform.api.dtos.lesson.content.LessonContentCreateRequestDTO;
import br.com.novalearn.platform.api.dtos.lesson.content.LessonContentResponseDTO;

import java.time.LocalDateTime;

public final class LessonContentFactoryTest {
    public static LessonContentResponseDTO lessonContentResponse() {
        return new LessonContentResponseDTO(
                8L,
                true,
                false,
                LocalDateTime.now(),
                null,
                "Observations",
                3L,
                "http://someurl.com",
                null,
                null,
                "Some content",
                false,
                true,
                5L,
                null
        );
    }

    public static LessonContentCreateRequestDTO lessonContentCreateRequest() {
        return new LessonContentCreateRequestDTO(
                "http://someurl.com",
                null,
                null,
                "Some content",
                false,
                true,
                "Observations"
        );
    }

    public static LessonContentCreateRequestDTO lessonContentCreateInvalidRequest() {
        return new LessonContentCreateRequestDTO(
                "http://someurl.com",
                null,
                null,
                null,
                false,
                true,
                "Observations"
        );
    }
}