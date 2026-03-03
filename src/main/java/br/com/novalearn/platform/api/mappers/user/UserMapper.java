package br.com.novalearn.platform.api.mappers.user;

import br.com.novalearn.platform.api.dtos.register.RegisterRequestDTO;
import br.com.novalearn.platform.api.dtos.user.UserCreateRequestDTO;
import br.com.novalearn.platform.api.dtos.user.UserListResponseDTO;
import br.com.novalearn.platform.api.dtos.user.UserResponseDTO;
import br.com.novalearn.platform.domain.entities.user.User;
import br.com.novalearn.platform.domain.valueobjects.Cpf;
import br.com.novalearn.platform.domain.valueobjects.Email;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {
    public User toEntity(UserCreateRequestDTO dto) {
        return new User(
                dto.getFirstName(),
                dto.getLastName(),
                dto.getBirthDate(),
                new Cpf(dto.getCpf()),
                dto.getPhone(),
                new Email(dto.getEmail()),
                dto.getRole(),
                dto.getLocale(),
                dto.getBio(),
                dto.getAvatarUrl(),
                dto.getObservations()
        );
    }

    public UserResponseDTO toResponseDTO(User user) {
        return new UserResponseDTO(
                user.getId(),
                user.isActive(),
                user.isDeleted(),
                user.getCreatedAt(),
                user.getUpdatedAt(),
                user.getObsProfile(),
                user.getFirstName(),
                user.getLastName(),
                user.getBirthDate(),
                user.getPhone(),
                user.getCpf().toString(),
                user.getEmail().toString(),
                user.getRole(),
                user.getStatus(),
                user.isEmailVerified(),
                user.getLastLoginAt(),
                user.getBio(),
                user.getAvatarUrl(),
                user.getLocale(),
                user.getCreatedBy(),
                user.getUpdatedBy()
        );
    }

    public UserListResponseDTO toListResponseDTO(User user) {
        return new UserListResponseDTO(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail().toString(),
                user.getRole(),
                user.getStatus(),
                user.isActive(),
                user.isDeleted()
        );
    }

    public User fromRegisterRequest(RegisterRequestDTO dto) {
        return User.register(
                dto.getFirstName(),
                dto.getLastName(),
                dto.getEmail(),
                dto.getCpf(),
                dto.getLocale(),
                dto.getPhone(),
                dto.getAvatarUrl()
        );
    }
}