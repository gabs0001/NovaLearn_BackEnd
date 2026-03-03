package br.com.novalearn.platform.domain.entities.refreshtoken;

import br.com.novalearn.platform.core.exception.business.ForbiddenOperationException;
import br.com.novalearn.platform.infra.time.provider.TimeProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static br.com.novalearn.platform.factories.entities.refreshtoken.CreateRefreshTokenFactory.createInitializedToken;
import static org.junit.jupiter.api.Assertions.*;

public class RefreshTokenValidationTest {
    private LocalDateTime now;
    private RefreshToken token;

    @BeforeEach
    void setUp() {
        now = LocalDateTime.now();
        token = createInitializedToken();
    }

    private TimeProvider fixedTime() {
        return () -> LocalDateTime.of(2026, 1,1,10,0);
    }

    @Test
    void should_fail_when_rotating_deleted_token() {
        token.markAsDeleted();

        assertThrows(
                ForbiddenOperationException.class,
                () -> token.rotate(
                        "new",
                        now.plusDays(1),
                        1L,
                        now
                )
        );
    }

    @Test
    void should_fail_when_revoking_deleted_token() {
        token.markAsDeleted();

        assertThrows(
                ForbiddenOperationException.class,
                () -> token.revoke(1L, now)
        );
    }
}