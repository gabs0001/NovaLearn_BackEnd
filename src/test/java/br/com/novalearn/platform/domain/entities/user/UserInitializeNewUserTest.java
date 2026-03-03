package br.com.novalearn.platform.domain.entities.user;

import br.com.novalearn.platform.core.exception.business.ValidationException;
import br.com.novalearn.platform.domain.enums.UserStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;

import static br.com.novalearn.platform.factories.entities.user.CreateUserFactory.createInitializedUser;
import static org.assertj.core.api.Assertions.*;

public class UserInitializeNewUserTest {
    private User user;

    @BeforeEach
    void setUp() {
        user = createInitializedUser();
        user.clearDomainEvents();
    }

    @Test
    void should_initialize_new_user_with_valid_password_and_timestamp() {
        //given
        String encodedPassword = "hashed-password";
        LocalDateTime now = LocalDateTime.now();

        //when
        user.initializeNewUser(encodedPassword, now);

        //then
        assertThat(user.getPasswordHash()).isEqualTo(encodedPassword);
        assertThat(user.getStatus()).isEqualTo(UserStatus.INACTIVE);
        assertThat(user.isEmailVerified()).isFalse();
        assertThat(user.isActive()).isTrue();
        assertThat(user.getCreatedAt()).isEqualTo(now);
    }

    @Test
    void should_throw_exception_when_initializing_user_with_null_password() {
        //when / then
        assertThatThrownBy(() -> user.initializeNewUser(null, LocalDateTime.now()))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Password");
    }

    @Test
    void should_throw_exception_when_initializing_user_with_blank_password() {
        //when / then
        assertThatThrownBy(() -> user.initializeNewUser("  ", LocalDateTime.now()))
                .isInstanceOf(ValidationException.class);
    }

    @Test
    void should_not_register_domain_events_when_initializing_user() {
        ReflectionTestUtils.setField(user, "domainEvents", new ArrayList<>());

        //when
        user.initializeNewUser("hashed-password", LocalDateTime.now());

        //then
        assertThat(user.getDomainEvents()).isEmpty();
    }
}