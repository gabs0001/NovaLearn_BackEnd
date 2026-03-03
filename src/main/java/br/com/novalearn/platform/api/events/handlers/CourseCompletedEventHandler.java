package br.com.novalearn.platform.api.events.handlers;

import br.com.novalearn.platform.domain.entities.user.UserCourse;
import br.com.novalearn.platform.domain.events.course.CourseCompletedEvent;
import br.com.novalearn.platform.domain.repositories.user.UserCourseRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class CourseCompletedEventHandler {
    private final UserCourseRepository userCourseRepository;

    public CourseCompletedEventHandler(UserCourseRepository userCourseRepository) {
        this.userCourseRepository = userCourseRepository;
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(CourseCompletedEvent event) {
        UserCourse enrollment = userCourseRepository
                .findByUserIdAndCourseIdAndDeletedFalse(
                        event.getUser().getId(),
                        event.getCourse().getId()
                )
                .orElseThrow();

        if (!enrollment.isCertificateIssued()) {
            enrollment.issueCertificate();
            userCourseRepository.save(enrollment);
        }
    }
}