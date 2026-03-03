package br.com.novalearn.platform.factories.entities.enrollment;

import br.com.novalearn.platform.domain.entities.course.Course;
import br.com.novalearn.platform.domain.entities.user.User;
import br.com.novalearn.platform.domain.entities.user.UserCourse;

import java.time.LocalDateTime;

import static br.com.novalearn.platform.factories.entities.course.CreateCourseFactory.createInitializedCourse;
import static br.com.novalearn.platform.factories.entities.user.CreateUserFactory.createInitializedUser;

public final class CreateEnrollmentFactory {
    private static final User user = createInitializedUser();
    private static final Course course = createInitializedCourse();
    private static final LocalDateTime now = LocalDateTime.now();

    public static UserCourse createInitializedEnrollment() {
        return UserCourse.enroll(
                user,
                course,
                now
        );
    }
}