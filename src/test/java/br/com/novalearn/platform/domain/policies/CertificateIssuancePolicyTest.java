package br.com.novalearn.platform.domain.policies;

import br.com.novalearn.platform.core.exception.business.ForbiddenOperationException;
import br.com.novalearn.platform.core.exception.business.InvalidStateException;
import br.com.novalearn.platform.domain.entities.user.UserCourse;
import br.com.novalearn.platform.domain.enums.EnrollmentStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CertificateIssuancePolicyTest {
    private CertificateIssuancePolicy policy;

    @BeforeEach
    void setUp() { policy = new CertificateIssuancePolicy(); }

    private UserCourse mockUserCourse(EnrollmentStatus status, boolean certificateIssued) {
        UserCourse userCourse = mock(UserCourse.class);

        when(userCourse.getEnrollmentStatus()).thenReturn(status);
        lenient().when(userCourse.isCertificateIssued()).thenReturn(certificateIssued);

        return userCourse;
    }

    @Test
    void should_allow_certificate_issuance_when_course_is_completed_and_no_certificate_issued_and_no_pending_quizzes() {
        UserCourse userCourse = mockUserCourse(
                EnrollmentStatus.COMPLETED,
                false
        );

        assertThatCode(() -> policy.validate(userCourse, false))
                .doesNotThrowAnyException();
    }

    @Test
    void should_throw_forbidden_exception_when_course_is_not_completed() {
        UserCourse userCourse = mockUserCourse(
                EnrollmentStatus.IN_PROGRESS,
                false
        );

        assertThatThrownBy(() -> policy.validate(userCourse, false))
                .isInstanceOf(ForbiddenOperationException.class)
                .hasMessage("Certificate can only be issued for completed courses.");
    }

    @Test
    void should_throw_invalid_state_exception_when_certificate_already_issued() {
        UserCourse userCourse = mockUserCourse(
                EnrollmentStatus.COMPLETED,
                true
        );

        assertThatThrownBy(() -> policy.validate(userCourse, false))
                .isInstanceOf(InvalidStateException.class)
                .hasMessage("Certificate has already been issued.");
    }

    @Test
    void should_throw_forbidden_exception_when_user_has_pending_quiz_attempts() {
        UserCourse userCourse = mockUserCourse(
                EnrollmentStatus.COMPLETED,
                false
        );

        assertThatThrownBy(() -> policy.validate(userCourse, true))
                .isInstanceOf(ForbiddenOperationException.class)
                .hasMessage("User has pending or failed quiz attempts.");
    }
}