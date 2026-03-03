package br.com.novalearn.platform.api.mappers.course;

import br.com.novalearn.platform.api.dtos.course.CourseListResponseDTO;
import br.com.novalearn.platform.api.dtos.course.CourseResponseDTO;
import br.com.novalearn.platform.domain.entities.course.Course;
import org.springframework.stereotype.Component;

@Component
public class CourseMapper {
    public CourseResponseDTO toResponseDTO(Course entity) {
        return new CourseResponseDTO(
                entity.getId(),
                entity.isActive(),
                entity.isDeleted(),
                entity.getCreatedAt(),
                entity.getUpdatedAt(),
                entity.getObservations(),
                entity.getInstructor() != null ? entity.getInstructor().getId() : null,
                entity.getCategory() != null ? entity.getCategory().getId() : null,
                entity.getName(),
                entity.getShortDescription(),
                entity.getLongDescription(),
                entity.getPrice(),
                entity.isPaid(),
                entity.getThumbnailUrl(),
                entity.getDuration(),
                entity.getNumLessons(),
                entity.getNumStudents(),
                entity.getNumRatingTotal(),
                entity.getNumRatingCount(),
                entity.getPublishedAt(),
                entity.getSlug(),
                entity.getStatus(),
                entity.getCreatedBy(),
                entity.getUpdatedBy()
        );
    }

    public CourseListResponseDTO toListResponseDTO(Course entity) {
        return new CourseListResponseDTO(
                entity.getId(),
                entity.getInstructor() != null ? entity.getInstructor().getId() : null,
                entity.getCategory() != null ? entity.getCategory().getId() : null,
                entity.getName(),
                entity.getShortDescription(),
                entity.getStatus(),
                entity.getThumbnailUrl(),
                entity.getPrice(),
                entity.isPaid(),
                entity.getNumStudents(),
                entity.getNumRatingTotal(),
                entity.getNumRatingCount(),
                entity.getSlug(),
                entity.isActive(),
                entity.isDeleted()
        );
    }
}