package br.com.novalearn.platform.factories.entities.user;

import br.com.novalearn.platform.domain.entities.user.User;

import java.time.LocalDateTime;

public final class CreateUserFactory {
    public static User createInitializedUser() {
        User user = User.register(
                "Alex",
                "Rivers",
                "alex.rivers@example.com",
                "12345678900",
                "en-US",
                null,
                null
        );

        user.initializeNewUser("encoded-password", LocalDateTime.now());
        return user;
    }
}