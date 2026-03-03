package br.com.novalearn.platform.domain.repositories.refreshtoken;

import br.com.novalearn.platform.domain.entities.refreshtoken.RefreshToken;
import br.com.novalearn.platform.domain.entities.user.User;
import br.com.novalearn.platform.infra.time.provider.TimeProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
public class RefreshTokenRepositoryTest {
    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private TestEntityManager entityManager;

    private LocalDateTime now;

    @Mock
    private TimeProvider provider;

    private User user;
    private User otherUser;

    private RefreshToken activeToken;
    private RefreshToken revokedToken;

    @BeforeEach
    void setUp() {
        now = LocalDateTime.now();

        user = createUser("a@test.com", "11111111111");
        otherUser = createUser("b@test.com", "22222222222");

        entityManager.persist(user);
        entityManager.persist(otherUser);

        activeToken = createToken(user, "hash-123", false);
        activeToken.setCreatedAt(now);

        revokedToken = createToken(user, "hash-456", true);
        revokedToken.setCreatedAt(now);

        persistAll();
    }

    private void persistAll() {
        entityManager.persist(activeToken);
        entityManager.persist(revokedToken);
    }

    private User createUser(String email, String cpf) {
        User user = User.register(
                "João",
                "Silva",
                email,
                cpf,
                "pt-BR",
                null,
                null
        );

        user.initializeNewUser("encoded", now);

        return user;
    }

    private RefreshToken createToken(User user, String tokenHash, boolean revoked) {
        RefreshToken token = RefreshToken.create(
                user,
                tokenHash,
                now.plusDays(7),
                5L,
                provider
        );

        if(revoked) token.revoke(5L, now);

        return token;
    }

    @Test
    void should_find_by_token_hash() {
        Optional<RefreshToken> result = refreshTokenRepository.findByTokenHash("hash-123");

        assertThat(result).isPresent();
        assertThat(result.get().getUser().getId()).isEqualTo(user.getId());
    }

    @Test
    void should_return_empty_when_token_hash_not_found() {
        Optional<RefreshToken> result = refreshTokenRepository.findByTokenHash("invalid-hash");

        assertThat(result).isEmpty();
    }

    @Test
    void should_find_all_active_tokens_by_user() {
        List<RefreshToken> result = refreshTokenRepository.findAllByUserAndRevokedFalse(user);

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().isRevoked()).isFalse();
    }

    @Test
    void should_not_return_tokens_from_other_users() {
        RefreshToken otherToken = createToken(otherUser, "hash-789", false);
        otherToken.setCreatedAt(now);

        entityManager.persist(otherToken);

        List<RefreshToken> result = refreshTokenRepository.findAllByUserAndRevokedFalse(user);

        assertThat(result).hasSize(1);
    }

    @Test
    void should_not_return_revoked_tokens() {
        List<RefreshToken> result = refreshTokenRepository.findAllByUserAndRevokedFalse(user);
        assertThat(result).allMatch(token -> !token.isRevoked());
    }

    @Test
    void should_delete_all_tokens_by_user_id() {
        RefreshToken otherUserToken = createToken(otherUser, "hash-999", false);
        otherUserToken.setCreatedAt(now);

        entityManager.persist(otherUserToken);

        refreshTokenRepository.deleteAllByUserId(user.getId());

        entityManager.flush();
        entityManager.clear();

        List<RefreshToken> userTokens = refreshTokenRepository.findAllByUserAndRevokedFalse(user);

        List<RefreshToken> otherUserTokens = refreshTokenRepository.findAllByUserAndRevokedFalse(otherUser);

        assertThat(userTokens).isEmpty();
        assertThat(otherUserTokens).hasSize(1);
    }
}