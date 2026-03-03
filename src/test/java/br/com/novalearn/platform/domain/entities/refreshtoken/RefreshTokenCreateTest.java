package br.com.novalearn.platform.domain.entities.refreshtoken;

import br.com.novalearn.platform.domain.entities.user.User;
import br.com.novalearn.platform.infra.time.provider.TimeProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static br.com.novalearn.platform.factories.entities.user.CreateUserFactory.createInitializedUser;
import static org.junit.jupiter.api.Assertions.*;

public class RefreshTokenCreateTest {
    private User user;

    @BeforeEach
    void setUp() { user = createInitializedUser(); }

    @Test
    void should_create_refresh_token_successfully() {
        LocalDateTime now = LocalDateTime.of(2026,1,1,10,0);

        TimeProvider time = () -> now;

        RefreshToken token = RefreshToken.create(
                user,
                "hash",
                now.plusDays(5),
                10L,
                time
        );

        assertNotNull(token);
        assertEquals(user, token.getUser());
        assertEquals("hash", token.getTokenHash());
        assertEquals(now.plusDays(5), token.getExpiresAt());
        assertFalse(token.isRevoked());
        assertEquals(now, token.getCreatedAt());
        assertEquals(10L, token.getCreatedBy());
        assertTrue(token.isActive());
        assertFalse(token.isDeleted());
    }
}