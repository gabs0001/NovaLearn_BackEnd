package br.com.novalearn.platform.domain.policies;

import br.com.novalearn.platform.core.exception.business.ForbiddenOperationException;
import br.com.novalearn.platform.core.exception.business.InvalidStateException;
import br.com.novalearn.platform.domain.entities.user.UserCourse;
import br.com.novalearn.platform.domain.enums.EnrollmentStatus;
import org.springframework.stereotype.Component;

@Component
public class CertificateIssuancePolicy {
    public void validate(
            UserCourse userCourse,
            boolean hasPendingQuizAttempts
    ) {
        if(userCourse.getEnrollmentStatus() != EnrollmentStatus.COMPLETED)
            throw new ForbiddenOperationException("Certificate can only be issued for completed courses.");

        if(userCourse.isCertificateIssued()) throw new InvalidStateException("Certificate has already been issued.");

        if(hasPendingQuizAttempts) throw new ForbiddenOperationException("User has pending or failed quiz attempts.");
    }
}