package br.com.novalearn.platform.domain.entities.user;

import br.com.novalearn.platform.core.exception.business.ForbiddenOperationException;
import br.com.novalearn.platform.core.exception.business.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static br.com.novalearn.platform.factories.entities.user.CreateUserFactory.createInitializedUser;
import static org.assertj.core.api.Assertions.*;

public class UserChangePasswordTest {
    private User user;

    @BeforeEach
    void setUp() { user = createInitializedUser(); }

    @Test
    void should_change_password_when_user_is_valid() {
        //given
        String newPassword = "new-encoded-password";

        //when
        user.changePassword(newPassword);

        //then
        assertThat(user.getPasswordHash()).isEqualTo(newPassword);
    }

    @Test
    void should_throw_exception_when_changing_password_with_null_value() {
        assertThatThrownBy(() -> user.changePassword(null))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Password");
    }

    @Test
    void should_throw_exception_when_changing_password_with_blank_value() {
        assertThatThrownBy(() -> user.changePassword("  "))
                .isInstanceOf(ValidationException.class);
    }

    @Test
    void should_throw_exception_when_changing_password_of_deleted_user() {
        //given
        user.delete();

        //when / then
        assertThatThrownBy(() -> user.changePassword("new-password"))
                .isInstanceOf(ForbiddenOperationException.class);
    }

    @Test
    void should_not_change_password_when_validation_fails() {
        String originalPassword = user.getPasswordHash();

        assertThatThrownBy(() -> user.changePassword(" "))
                .isInstanceOf(ValidationException.class);

        assertThat(user.getPasswordHash()).isEqualTo(originalPassword);
    }
}