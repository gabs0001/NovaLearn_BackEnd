package br.com.novalearn.platform.factories.dtos.user;

import br.com.novalearn.platform.api.dtos.user.UserCreateRequestDTO;
import br.com.novalearn.platform.api.dtos.user.UserListResponseDTO;
import br.com.novalearn.platform.api.dtos.user.UserResponseDTO;
import br.com.novalearn.platform.api.dtos.user.UserUpdateRequestDTO;
import br.com.novalearn.platform.api.dtos.user.activity.UserActivityResponseDTO;
import br.com.novalearn.platform.domain.enums.UserRole;
import br.com.novalearn.platform.domain.enums.UserStatus;
import br.com.novalearn.platform.domain.valueobjects.Cpf;
import br.com.novalearn.platform.domain.valueobjects.Email;

import java.time.LocalDateTime;

public final class UserTestFactory {
    private UserTestFactory() {}

    public static UserCreateRequestDTO createRequest() {
        return new UserCreateRequestDTO(
                "Alex",
                "Rivers",
                LocalDateTime.of(1992, 5, 15, 0, 0),
                "+1 (555) 123-4567",
                "12345678900",
                "alex.rivers@example.com",
                "StrongPassword123!",
                UserRole.ADMIN,
                "en-US",
                "Senior Engineer",
                "https://api.dicebear.com/7.x/avataaars/svg?seed=Alex",
                "Observations"
        );
    }

    public static UserCreateRequestDTO createInvalidRequest() {
        return new UserCreateRequestDTO(
                "",
                "",
                null,
                "",
                "",
                "",
                "",
                null,
                "",
                "",
                "",
                ""
        );
    }

    public static UserUpdateRequestDTO updateRequest() {
        return new UserUpdateRequestDTO(
                "Alex",
                "Rivers",
                LocalDateTime.of(1992, 5, 15, 0, 0),
                "+1 (555) 456-7890",
                "12345678900",
                "en-US",
                "Senior Engineer",
                "https://api.dicebear.com/7.x/avataaars/svg?seed=Alex",
                UserRole.ADMIN,
                UserStatus.ACTIVE
        );
    }

    public static UserListResponseDTO listResponse() {
        return new UserListResponseDTO(
                5L,
                "Alex",
                "Rivers",
                "alex.rivers@example.com",
                UserRole.ADMIN,
                UserStatus.ACTIVE,
                true,
                false
        );
    }

    public static UserResponseDTO response() {
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
                "12345678900",
                "alex.rivers@example.com",
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

    public static UserActivityResponseDTO activityResponse() {
        LocalDateTime now = LocalDateTime.now();

        return new UserActivityResponseDTO(
                5L,
                5,
                3,
                2,
                2,
                now.minusDays(2),
                now.minusHours(3)
        );
    }
}