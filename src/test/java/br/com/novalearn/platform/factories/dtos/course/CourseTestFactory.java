package br.com.novalearn.platform.factories.dtos.course;

import br.com.novalearn.platform.api.dtos.course.*;
import br.com.novalearn.platform.api.dtos.course.progress.CourseProgressResponseDTO;
import br.com.novalearn.platform.api.dtos.module.ModuleListResponseDTO;
import br.com.novalearn.platform.domain.enums.CourseStatus;
import br.com.novalearn.platform.domain.enums.EnrollmentStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public final class CourseTestFactory {
    public static CourseResponseDTO courseResponse() {
        return new CourseResponseDTO(
                1L,
                true,
                false,
                LocalDateTime.now(),
                null,
                "Observations",
                5L,
                10L,
                "Java Fundamentals",
                "Java course for beginners",
                "This is a long description for java fundamentals",
                new BigDecimal("99.9"),
                true,
                "http://someurl.com",
                0,
                0,
                0,
                0,
                0,
                LocalDateTime.now().minusDays(7),
                "java-fundamentals",
                CourseStatus.PUBLISHED,
                5L,
                null
        );
    }

    public static CourseListResponseDTO courseListResponse() {
        return new CourseListResponseDTO(
                1L,
                5L,
                10L,
                "Java Fundamentals",
                "Java course for beginners",
                CourseStatus.PUBLISHED,
                "http://someurl.com",
                new BigDecimal("99.9"),
                true,
                0,
                0,
                0,
                "java-fundamentals",
                true,
                false
        );
    }

    public static CourseCreateRequestDTO courseCreateRequest() {
        return new CourseCreateRequestDTO(
                10L,
                "Java Fundamentals",
                "Java course for beginners",
                "This is a long description for java fundamentals",
                "http://someurl.com"
        );
    }

    public static CourseCreateRequestDTO invalidCourseCreateRequest() {
        return new CourseCreateRequestDTO(
                null,
                "",
                "",
                "",
                ""
        );
    }

    public static CourseUpdateRequestDTO courseUpdateRequest() {
        return new CourseUpdateRequestDTO(
                "Java Advanced",
                "Advanced course",
                "This is a long description for java advanced course",
                new BigDecimal("129.9"),
                true,
                "http://newurl.com",
                10L,
                "java-advanced",
                "observations",
                true,
                false
        );
    }

    public static ModuleListResponseDTO moduleListResponse() {
        return new ModuleListResponseDTO(
                6L,
                1L,
                "Backend basics",
                1,
                true,
                false
        );
    }

    public static CourseProgressResponseDTO courseProgressResponse() {
        return new CourseProgressResponseDTO(
                1L,
                "Java Fundamentals",
                0,
                0,
                5,
                EnrollmentStatus.IN_PROGRESS,
                LocalDateTime.now().minusDays(10),
                null
        );
    }
}