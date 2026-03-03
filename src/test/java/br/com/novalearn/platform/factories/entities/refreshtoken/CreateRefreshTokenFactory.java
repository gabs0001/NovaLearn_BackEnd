package br.com.novalearn.platform.factories.entities.refreshtoken;

import br.com.novalearn.platform.domain.entities.refreshtoken.RefreshToken;
import br.com.novalearn.platform.domain.entities.user.User;
import br.com.novalearn.platform.infra.time.provider.TimeProvider;

import java.time.LocalDateTime;

import static br.com.novalearn.platform.factories.entities.user.CreateUserFactory.createInitializedUser;

public final class CreateRefreshTokenFactory {
    private static final User user = createInitializedUser();
    private static final LocalDateTime now = LocalDateTime.now();

    private static TimeProvider fixedTime() {
        return () -> LocalDateTime.of(2026, 1,1,10,0);
    }

    public static RefreshToken createInitializedToken() {
        return RefreshToken.create(
                user,
                "hash123",
                now.plusDays(7),
                1L,
                fixedTime()
        );
    }
}