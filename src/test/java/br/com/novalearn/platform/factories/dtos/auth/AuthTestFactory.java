package br.com.novalearn.platform.factories.dtos.auth;

import br.com.novalearn.platform.api.dtos.login.LoginRequestDTO;
import br.com.novalearn.platform.api.dtos.login.LoginResponseDTO;
import br.com.novalearn.platform.api.dtos.logout.LogoutRequestDTO;
import br.com.novalearn.platform.api.dtos.refreshtoken.RefreshTokenRequestDTO;
import br.com.novalearn.platform.api.dtos.refreshtoken.RefreshTokenResponseDTO;
import br.com.novalearn.platform.api.dtos.register.RegisterRequestDTO;
import br.com.novalearn.platform.api.dtos.register.RegisterResponseDTO;
import br.com.novalearn.platform.api.dtos.user.UserResponseDTO;
import br.com.novalearn.platform.domain.enums.UserRole;
import br.com.novalearn.platform.domain.enums.UserStatus;
import br.com.novalearn.platform.domain.valueobjects.Cpf;
import br.com.novalearn.platform.domain.valueobjects.Email;

import java.time.LocalDateTime;

public final class AuthTestFactory {
    private AuthTestFactory() {}

    public static RegisterRequestDTO registerRequest() {
        return new RegisterRequestDTO(
                "Alex",
                "Rivers",
                "alex.rivers@example.com",
                "StrongPassword123!",
                "123.456.789-00",
                "en-US",
                "+1 (555) 123-4567",
                "https://api.dicebear.com/7.x/avataaars/svg?seed=Alex"
        );
    }

    public static RegisterResponseDTO registerResponse() {
        return new RegisterResponseDTO(
                5L,
                "alex.rivers@example.com",
                true,
                LocalDateTime.now()
        );
    }

    public static LoginRequestDTO loginRequest() {
        return new LoginRequestDTO(
                "alex@email.com",
                "123456",
                true
        );
    }

    public static LoginResponseDTO loginResponse() {
        return new LoginResponseDTO(
                "access-token",
                "refresh-token",
                3600L,
                "bearer"
        );
    }

    public static RefreshTokenRequestDTO refreshRequest() {
        return new RefreshTokenRequestDTO("refresh");
    }

    public static RefreshTokenResponseDTO refreshResponse() {
        return new RefreshTokenResponseDTO(
                "new-access",
                "refresh",
                3600L,
                "bearer",
                true
        );
    }

    public static LogoutRequestDTO logoutRequest() {
        return new LogoutRequestDTO("refresh");
    }

    public static UserResponseDTO authenticatedUser() {
        LocalDateTime now = LocalDateTime.now();

        return new UserResponseDTO(
                5L,
                true,
                false,
                now.minusDays(30),
                now,
                "Premium user",
                "Alex",
                "Rivers",
                LocalDateTime.of(1992, 5, 15, 0, 0),
                "+1 (555) 123-4567",
                new Cpf("123.456.789-00").toString(),
                new Email("alex.rivers@example.com").toString(),
                UserRole.ADMIN,
                UserStatus.ACTIVE,
                true,
                now.minusHours(2),
                "Senior Engineer",
                "https://api.dicebear.com/7.x/avataaars/svg?seed=Alex",
                "en-US",
                5L,
                null
        );
    }
}