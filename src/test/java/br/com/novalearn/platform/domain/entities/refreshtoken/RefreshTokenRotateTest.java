package br.com.novalearn.platform.domain.entities.refreshtoken;

import br.com.novalearn.platform.core.exception.auth.TokenExpiredException;
import br.com.novalearn.platform.domain.entities.user.User;
import br.com.novalearn.platform.infra.time.provider.TimeProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static br.com.novalearn.platform.factories.entities.refreshtoken.CreateRefreshTokenFactory.createInitializedToken;
import static br.com.novalearn.platform.factories.entities.user.CreateUserFactory.createInitializedUser;
import static org.junit.jupiter.api.Assertions.*;

public class RefreshTokenRotateTest {
    private LocalDateTime now;
    private User user;
    private RefreshToken token;

    @BeforeEach
    void setUp() {
        now = LocalDateTime.now();
        user = createInitializedUser();
        token = createInitializedToken();
    }

    private TimeProvider fixedTime() {
        return () -> LocalDateTime.of(2026, 1,1,10,0);
    }

    @Test
    void should_rotate_token_successfully() {
        LocalDateTime newExp = now.plusDays(10);

        token.rotate(
                "newHash",
                newExp,
                2L,
                now
        );

        assertEquals("newHash", token.getTokenHash());
        assertEquals(newExp, token.getExpiresAt());
    }

    @Test
    void should_fail_when_rotating_revoked_token() {
        token.revoke(1L, now);

        assertThrows(
                TokenExpiredException.class,
                () -> token.rotate(
                        "new",
                        now.plusDays(1),
                        1L,
                        now
                )
        );
    }

    @Test
    void should_fail_when_rotating_expired_token() {
        RefreshToken token = RefreshToken.create(
                user,
                "hash",
                now.minusDays(1),
                1L,
                fixedTime()
        );

        assertThrows(
                TokenExpiredException.class,
                () -> token.rotate(
                        "new",
                        now.plusDays(1),
                        1L,
                        now
                )
        );
    }
}