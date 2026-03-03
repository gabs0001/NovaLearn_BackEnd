package br.com.novalearn.platform.domain.entities.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static br.com.novalearn.platform.factories.entities.user.CreateUserFactory.createInitializedUser;
import static org.assertj.core.api.Assertions.*;

public class UserGetLastKnowActivityTest {
    private User user;

    @BeforeEach
    void setUp() { user = createInitializedUser(); }

    @Test
    void should_return_last_login_when_exists() {
        //given
        LocalDateTime loginTime = LocalDateTime.now().minusDays(1);
        user.updateLastLogin(loginTime);

        //when
        LocalDateTime result = user.getLastKnownActivity(LocalDateTime.now());

        //then
        assertThat(result).isEqualTo(loginTime);
    }

    @Test
    void should_return_fallback_when_last_login_does_not_exist() {
        // Given
        LocalDateTime fallback = LocalDateTime.now();

        // When
        LocalDateTime result = user.getLastKnownActivity(fallback);

        // Then
        assertThat(result).isEqualTo(fallback);
    }

    @Test
    void should_return_null_when_last_login_and_fallback_are_null() {
        // When
        LocalDateTime result = user.getLastKnownActivity(null);

        // Then
        assertThat(result).isNull();
    }
}