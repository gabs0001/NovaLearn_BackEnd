package br.com.novalearn.platform.domain.entities.user;

import br.com.novalearn.platform.core.exception.business.ForbiddenOperationException;
import br.com.novalearn.platform.core.exception.business.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static br.com.novalearn.platform.factories.entities.user.CreateUserFactory.createInitializedUser;
import static org.assertj.core.api.Assertions.*;

public class UserUpdateLastLoginTest {
    private User user;

    @BeforeEach
    void setUp() { user = createInitializedUser(); }

    @Test
    void should_update_last_login_time() {
        //given
        LocalDateTime now = LocalDateTime.now();

        //when
        user.updateLastLogin(now);

        //then
        assertThat(user.getLastLoginAt()).isEqualTo(now);
    }

    @Test
    void should_throw_exception_when_login_time_is_null() {
        //when / then
        assertThatThrownBy(() -> user.updateLastLogin(null))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Login time is required");
    }

    @Test
    void should_throw_exception_when_updating_last_login_of_deleted_user() {
        //given
        user.delete();

        //when / then
        assertThatThrownBy(() -> user.updateLastLogin(LocalDateTime.now()))
                .isInstanceOf(ForbiddenOperationException.class);
    }
}