package br.com.novalearn.platform.api.mappers.user;

import br.com.novalearn.platform.api.dtos.user.course.UserCourseListResponseDTO;
import br.com.novalearn.platform.api.dtos.user.course.UserCourseResponseDTO;
import br.com.novalearn.platform.domain.entities.user.UserCourse;
import org.springframework.stereotype.Component;

@Component
public class UserCourseMapper {
    public UserCourseResponseDTO toResponseDTO(UserCourse entity) {
        return new UserCourseResponseDTO(
                entity.getId(),
                entity.isActive(),
                entity.isDeleted(),
                entity.getCreatedAt(),
                entity.getUpdatedAt(),
                entity.getObservations(),
                entity.getUser() != null ? entity.getUser().getId() : null,
                entity.getCourse() != null ? entity.getCourse().getId() : null,
                entity.getEnrolledAt(),
                entity.getCompletedAt(),
                entity.getEnrollmentStatus(),
                entity.getProgressPercent(),
                entity.getPaidAmount(),
                entity.getPaymentMethod(),
                entity.isCertificateIssued(),
                entity.getCreatedBy(),
                entity.getUpdatedBy()
        );
    }

    public UserCourseListResponseDTO toListResponseDTO(UserCourse entity) {
        return new UserCourseListResponseDTO(
                entity.getId(),
                entity.getUser() != null ? entity.getUser().getId() : null,
                entity.getCourse() != null ? entity.getCourse().getId() : null,
                entity.getEnrollmentStatus(),
                entity.getProgressPercent(),
                entity.isActive(),
                entity.isDeleted()
        );
    }
}