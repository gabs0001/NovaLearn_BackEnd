package br.com.novalearn.platform.factories.dtos.course;

import br.com.novalearn.platform.api.dtos.course.my.MyCourseResponseDTO;
import br.com.novalearn.platform.domain.enums.EnrollmentStatus;

import java.time.LocalDateTime;

public final class MyCourseTestFactory {
    public static MyCourseResponseDTO myCourseResponse() {
        return new MyCourseResponseDTO(
                1L,
                "Java Fundamentals",
                "Java course for beginners",
                EnrollmentStatus.IN_PROGRESS,
                LocalDateTime.now().minusDays(10)
        );
    }

    public static MyCourseResponseDTO myCourseCompletedResponse() {
        return new MyCourseResponseDTO(
                1L,
                "Java Fundamentals",
                "Java course for beginners",
                EnrollmentStatus.COMPLETED,
                LocalDateTime.now().minusDays(10)
        );
    }
}