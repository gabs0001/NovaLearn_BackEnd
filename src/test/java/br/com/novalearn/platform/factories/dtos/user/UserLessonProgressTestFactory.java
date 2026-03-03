package br.com.novalearn.platform.factories.dtos.user;

import br.com.novalearn.platform.api.dtos.user.lessonprogress.UserLessonProgressListResponseDTO;
import br.com.novalearn.platform.api.dtos.user.lessonprogress.UserLessonProgressResponseDTO;

import java.time.LocalDateTime;

public final class UserLessonProgressTestFactory {
    public static UserLessonProgressListResponseDTO listResponse() {
        return new UserLessonProgressListResponseDTO(
                1L,
                5L,
                3L,
                false,
                25,
                100,
                true,
                false
        );
    }

    public static UserLessonProgressResponseDTO response() {
        return new UserLessonProgressResponseDTO(
                1L,
                true,
                false,
                LocalDateTime.now(),
                null,
                "Observations",
                5L,
                3L,
                false,
                null,
                25,
                20,
                LocalDateTime.now().minusDays(5),
                LocalDateTime.now().minusHours(4),
                5L,
                null
        );
    }
}