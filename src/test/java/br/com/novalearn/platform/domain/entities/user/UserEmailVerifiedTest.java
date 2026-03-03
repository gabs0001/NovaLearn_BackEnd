package br.com.novalearn.platform.domain.entities.user;

import br.com.novalearn.platform.core.exception.business.ForbiddenOperationException;
import br.com.novalearn.platform.core.exception.business.InvalidStateException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static br.com.novalearn.platform.factories.entities.user.CreateUserFactory.createInitializedUser;
import static org.assertj.core.api.Assertions.*;

public class UserEmailVerifiedTest {
    private User user;

    @BeforeEach
    void setUp() { user = createInitializedUser(); }

    @Test
    void should_verify_email_when_not_verified_yet() {
        //given
        assertThat(user.isEmailVerified()).isFalse();

        //when
        user.verifyEmail();

        //then
        assertThat(user.isEmailVerified()).isTrue();
    }

    @Test
    void should_throw_exception_when_email_is_already_verified() {
        //given
        user.verifyEmail();

        //when / then
        assertThatThrownBy(user::verifyEmail)
                .isInstanceOf(InvalidStateException.class)
                .hasMessageContaining("Email already verified");
    }

    @Test
    void should_throw_exception_when_verifying_email_of_deleted_user() {
        // Given
        user.delete();

        // When / Then
        assertThatThrownBy(user::verifyEmail)
                .isInstanceOf(ForbiddenOperationException.class);
    }

    @Test
    void should_not_change_email_verified_flag_when_verification_fails() {
        user.verifyEmail();

        assertThatThrownBy(user::verifyEmail).isInstanceOf(InvalidStateException.class);
        assertThat(user.isEmailVerified()).isTrue();
    }
}