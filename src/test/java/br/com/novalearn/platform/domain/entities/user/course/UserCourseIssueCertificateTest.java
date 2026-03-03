package br.com.novalearn.platform.domain.entities.user.course;

import br.com.novalearn.platform.core.exception.business.InvalidStateException;
import br.com.novalearn.platform.domain.entities.user.UserCourse;
import br.com.novalearn.platform.domain.events.certificate.CertificateIssuedEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;

import static br.com.novalearn.platform.factories.entities.enrollment.CreateEnrollmentFactory.createInitializedEnrollment;
import static org.junit.jupiter.api.Assertions.*;

public class UserCourseIssueCertificateTest {
    private LocalDateTime now;
    private UserCourse enrollment;

    @BeforeEach
    void setUp() {
        now = LocalDateTime.now();
        enrollment = createInitializedEnrollment();
    }

    @Test
    void should_issue_certificate_when_enrollment_is_completed() {
        //given
        enrollment.complete(now);

        ReflectionTestUtils.setField(enrollment, "domainEvents", new ArrayList<>());

        //when
        enrollment.issueCertificate();

        //then
        assertTrue(enrollment.isCertificateIssued());

        assertEquals(1, enrollment.getDomainEvents().size());
        assertInstanceOf(CertificateIssuedEvent.class, enrollment.getDomainEvents().getFirst());
    }

    @Test
    void should_not_issue_certificate_when_enrollment_is_not_completed() {
        //when / then
        InvalidStateException exception = assertThrows(
                InvalidStateException.class,
                enrollment::issueCertificate
        );

        assertEquals("Certificate can only be issued after course completion.", exception.getMessage());
    }

    @Test
    void should_not_issue_certificate_twice() {
        //given
        enrollment.complete(now);
        enrollment.issueCertificate();

        //when / then
        InvalidStateException exception = assertThrows(
                InvalidStateException.class,
                enrollment::issueCertificate
        );

        assertEquals("Certificate already issued.", exception.getMessage());
    }
}