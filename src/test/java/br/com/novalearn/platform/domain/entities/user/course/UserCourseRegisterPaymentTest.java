package br.com.novalearn.platform.domain.entities.user.course;

import br.com.novalearn.platform.core.exception.business.ForbiddenOperationException;
import br.com.novalearn.platform.core.exception.business.InvalidStateException;
import br.com.novalearn.platform.core.exception.business.ValidationException;
import br.com.novalearn.platform.domain.entities.user.UserCourse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static br.com.novalearn.platform.factories.entities.enrollment.CreateEnrollmentFactory.createInitializedEnrollment;
import static org.assertj.core.api.Assertions.*;

public class UserCourseRegisterPaymentTest {
    private LocalDateTime now;
    private UserCourse userCourse;

    @BeforeEach
    void setUp() {
        now = LocalDateTime.now();
        userCourse = createInitializedEnrollment();
    }

    @Test
    void should_register_payment_successfully() {
        BigDecimal amount = BigDecimal.valueOf(199.90);
        String method = "PIX";

        userCourse.registerPayment(amount, method, now);

        assertThat(userCourse.getPaidAmount()).isEqualByComparingTo(amount);
        assertThat(userCourse.getPaymentMethod()).isEqualTo(method);
    }

    @Test
    void should_not_allow_null_amount() {
        assertThatThrownBy(() -> userCourse.registerPayment(null, "PIX", now))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("amount");
    }

    @Test
    void should_not_allow_negative_amount() {
        BigDecimal negative = BigDecimal.valueOf(-10);

        assertThatThrownBy(() -> userCourse.registerPayment(negative, "PIX", now)).
                isInstanceOf(ValidationException.class);
    }

    @Test
    void should_not_allow_blank_method() {
        assertThatThrownBy(() ->
                userCourse.registerPayment(
                        BigDecimal.TEN,
                        "   ",
                        now
                )
        ).isInstanceOf(ValidationException.class);
    }

    @Test
    void should_not_allow_double_payment() {
        userCourse.registerPayment(
                BigDecimal.TEN,
                "PIX",
                now
        );

        assertThatThrownBy(() ->
                userCourse.registerPayment(
                        BigDecimal.ONE,
                        "CARD",
                        now
                )
        ).isInstanceOf(InvalidStateException.class);
    }

    @Test
    void should_not_allow_payment_if_deleted() {
        userCourse.delete();

        assertThatThrownBy(() ->
                userCourse.registerPayment(
                        BigDecimal.TEN,
                        "PIX",
                        now
                )
        ).isInstanceOf(ForbiddenOperationException.class);
    }
}