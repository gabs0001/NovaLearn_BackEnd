package br.com.novalearn.platform.factories.dtos.lesson;

import br.com.novalearn.platform.api.dtos.lesson.*;

import java.time.LocalDateTime;
import java.util.List;

public final class LessonTestFactory {
    public static LessonCreateRequestDTO lessonCreateRequest() {
        return new LessonCreateRequestDTO(
                6L,
                "Java introduction",
                "This is the first lesson",
                1,
                300,
                false,
                true,
                "http://previewurl.com",
                "Some notes",
                "Observations"
        );
    }

    public static LessonUpdateRequestDTO lessonUpdateRequest() {
        return new LessonUpdateRequestDTO(
                "Java new introduction",
                "This is the new description",
                1,
                300,
                false,
                true,
                "http://previewurl.com",
                "New notes",
                "Observations"
        );
    }

    public static LessonListResponseDTO lessonListResponse() {
        return new LessonListResponseDTO(
                3L,
                6L,
                "Java introduction",
                1,
                300,
                true,
                true
        );
    }

    public static LessonResponseDTO lessonResponse() {
        return new LessonResponseDTO(
                3L,
                true,
                false,
                LocalDateTime.now(),
                null,
                "Observations",
                6L,
                "Java introduction",
                "This is the first lesson",
                1,
                300,
                false,
                true,
                "http://previewurl.com",
                "Some notes",
                5L,
                null
        );
    }

    public static List<Long> reorderIds() {
        return List.of(3L, 1L, 2L);
    }
}