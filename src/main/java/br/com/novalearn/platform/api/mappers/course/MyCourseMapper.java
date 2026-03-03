package br.com.novalearn.platform.api.mappers.course;

import br.com.novalearn.platform.api.dtos.course.my.MyCourseResponseDTO;
import br.com.novalearn.platform.domain.entities.user.UserCourse;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class MyCourseMapper {
    public MyCourseResponseDTO toResponseDTO(UserCourse userCourse) {
        return new MyCourseResponseDTO(
                userCourse.getCourse() != null ? userCourse.getCourse().getId() : null,
                userCourse.getCourse() != null ? userCourse.getCourse().getName() : null,
                userCourse.getCourse() != null ? userCourse.getCourse().getShortDescription() : null,
                userCourse.getEnrollmentStatus(),
                userCourse.getEnrolledAt()
        );
    }

    public List<MyCourseResponseDTO> toListResponseDTO(List<UserCourse> userCourses) {
        return userCourses.stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }
}