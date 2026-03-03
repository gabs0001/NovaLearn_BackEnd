package br.com.novalearn.platform.api.mappers.enrollment;

import br.com.novalearn.platform.api.dtos.enrollment.EnrollmentResponseDTO;
import br.com.novalearn.platform.domain.entities.user.UserCourse;
import org.springframework.stereotype.Component;

@Component
public class EnrollmentMapper {
    public EnrollmentResponseDTO toResponseDTO(UserCourse userCourse) {
        return new EnrollmentResponseDTO(
                userCourse.getId(),
                userCourse.getCourse() != null ?  userCourse.getCourse().getId() : null,
                userCourse.getCourse() != null ? userCourse.getCourse().getName() : null,
                userCourse.getEnrollmentStatus(),
                userCourse.getEnrolledAt(),
                userCourse.getProgressPercent()
        );
    }
}