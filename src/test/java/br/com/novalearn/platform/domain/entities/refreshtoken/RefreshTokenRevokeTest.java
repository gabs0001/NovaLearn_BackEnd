package br.com.novalearn.platform.domain.entities.refreshtoken;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static br.com.novalearn.platform.factories.entities.refreshtoken.CreateRefreshTokenFactory.createInitializedToken;
import static org.junit.jupiter.api.Assertions.*;

public class RefreshTokenRevokeTest {
    private LocalDateTime now;
    private RefreshToken token;

    @BeforeEach
    void setUp() {
        now = LocalDateTime.now();
        token = createInitializedToken();
    }

    @Test
    void should_revoke_token() {
        token.revoke(1L, now);

        assertTrue(token.isRevoked());
        assertNotNull(token.getRevokedAt());
    }

    @Test
    void should_ignore_when_revoking_twice() {
        token.revoke(1L, now);
        assertDoesNotThrow(() -> token.revoke(1L, now));
    }
}