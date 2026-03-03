package br.com.novalearn.platform.domain.entities.refreshtoken;

import br.com.novalearn.platform.core.exception.auth.TokenExpiredException;
import br.com.novalearn.platform.core.exception.business.ForbiddenOperationException;
import br.com.novalearn.platform.domain.entities.user.User;
import br.com.novalearn.platform.infra.time.provider.TimeProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static br.com.novalearn.platform.factories.entities.refreshtoken.CreateRefreshTokenFactory.createInitializedToken;
import static br.com.novalearn.platform.factories.entities.user.CreateUserFactory.createInitializedUser;
import static org.junit.jupiter.api.Assertions.*;

public class RefreshTokenAssertUsableTest {
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
    void should_allow_usage_when_token_is_valid() {
        assertDoesNotThrow(() -> token.assertUsable(now));
    }

    @Test
    void should_fail_when_token_is_revoked() {
        token.revoke(1L, now);

        assertThrows(
                TokenExpiredException.class,
                () -> token.assertUsable(now)
        );
    }

    @Test
    void should_fail_when_token_is_expired() {
        RefreshToken token = RefreshToken.create(
                user,
                "hash",
                now.minusMinutes(1),
                1L,
                fixedTime()
        );

        assertThrows(
                TokenExpiredException.class,
                () -> token.assertUsable(now)
        );
    }

    @Test
    void should_fail_when_token_is_deleted() {
        token.markAsDeleted();

        assertThrows(
                ForbiddenOperationException.class,
                () -> token.assertUsable(now)
        );
    }
}