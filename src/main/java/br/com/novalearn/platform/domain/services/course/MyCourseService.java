package br.com.novalearn.platform.domain.services.course;

import br.com.novalearn.platform.api.dtos.course.my.MyCourseResponseDTO;
import br.com.novalearn.platform.api.mappers.course.MyCourseMapper;
import br.com.novalearn.platform.domain.entities.user.User;
import br.com.novalearn.platform.domain.entities.user.UserCourse;
import br.com.novalearn.platform.domain.repositories.user.UserCourseRepository;
import br.com.novalearn.platform.core.exception.auth.UnauthorizedException;
import br.com.novalearn.platform.core.exception.business.ForbiddenOperationException;
import jakarta.transaction.Transactional;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class MyCourseService {
    private final UserCourseRepository userCourseRepository;
    private final MyCourseMapper myCourseMapper;

    public MyCourseService(UserCourseRepository userCourseRepository, MyCourseMapper myCourseMapper) {
        this.userCourseRepository = userCourseRepository;
        this.myCourseMapper = myCourseMapper;
    }

    private User getUser() { return getAuthenticatedUser(); }

    @Transactional
    public List<MyCourseResponseDTO> listMyCourses() {
        List<UserCourse> enrollments = userCourseRepository.findAllByUserIdAndDeletedFalse(getUser().getId());
        return myCourseMapper.toListResponseDTO(enrollments);
    }

    @Transactional
    public MyCourseResponseDTO getMyCourseById(Long courseId) {
        UserCourse enrollment = userCourseRepository.findByUserIdAndCourseIdAndDeletedFalse(getUser().getId(), courseId)
                .orElseThrow(() ->
                        new ForbiddenOperationException("User is not enrolled to this course.")
                );

        return myCourseMapper.toResponseDTO(enrollment);
    }

    @Transactional
    public List<MyCourseResponseDTO> listCompletedCourses() {
        List<UserCourse> enrollments = userCourseRepository
                .findAllByUserIdAndCompletedAtIsNotNullAndDeletedFalse(getUser().getId());

        return myCourseMapper.toListResponseDTO(enrollments);
    }

    @Transactional
    public List<MyCourseResponseDTO> listCoursesInProgress() {
        List<UserCourse> enrollments = userCourseRepository
                .findAllByUserIdAndCompletedAtIsNullAndDeletedFalse(getUser().getId());

        return myCourseMapper.toListResponseDTO(enrollments);
    }

    private User getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if(authentication == null) {
            throw new UnauthorizedException("Authenticated user not found");
        }

        Object principal = authentication.getPrincipal();

        if(!(principal instanceof User user)) {
            throw new UnauthorizedException("Authenticated user not found");
        }

        return user;
    }
}