package br.com.novalearn.platform.api.mappers.enrollment.history;

import br.com.novalearn.platform.api.dtos.enrollment.history.EnrollmentHistoryResponseDTO;
import br.com.novalearn.platform.domain.entities.user.UserCourse;
import org.springframework.stereotype.Component;

@Component
public class EnrollmentHistoryMapper {
    public EnrollmentHistoryResponseDTO toResponseDTO(UserCourse entity) {
        return new EnrollmentHistoryResponseDTO(
                entity.getCourse() != null ? entity.getCourse().getId() : null,
                entity.getCourse() != null ? entity.getCourse().getName() : null,
                entity.getCourse() != null ? entity.getCourse().getShortDescription() : null,
                entity.getEnrollmentStatus(),
                entity.getEnrolledAt(),
                entity.getCompletedAt(),
                entity.getProgressPercent(),
                entity.isCertificateIssued()
        );
    }
}