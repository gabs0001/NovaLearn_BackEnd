package br.com.novalearn.platform.api.mappers.user;

import br.com.novalearn.platform.api.dtos.register.RegisterRequestDTO;
import br.com.novalearn.platform.api.dtos.user.UserCreateRequestDTO;
import br.com.novalearn.platform.api.dtos.user.UserListResponseDTO;
import br.com.novalearn.platform.api.dtos.user.UserResponseDTO;
import br.com.novalearn.platform.domain.entities.user.User;
import br.com.novalearn.platform.domain.enums.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static br.com.novalearn.platform.factories.dtos.auth.AuthTestFactory.registerRequest;
import static br.com.novalearn.platform.factories.dtos.user.UserTestFactory.createRequest;
import static br.com.novalearn.platform.factories.entities.user.CreateUserFactory.createInitializedUser;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class UserMapperTest {
    private UserMapper mapper;
    private User user;
    private LocalDateTime now;

    @BeforeEach
    void setUp() {
        now = LocalDateTime.now();
        mapper = new UserMapper();
        user = createInitializedUser();
    }

    @Test
    void should_map_user_create_request_dto_to_entity() {
        UserCreateRequestDTO dto = createRequest();

        User user = mapper.toEntity(dto);

        assertThat(user.getFirstName()).isEqualTo("Alex");
        assertThat(user.getLastName()).isEqualTo("Rivers");
        assertThat(user.getBirthDate()).isEqualTo(LocalDateTime.of(1992, 5, 15, 0, 0));
        assertThat(user.getCpf().getValue()).isEqualTo("12345678900");
        assertThat(user.getPhone()).isEqualTo("+1 (555) 123-4567");
        assertThat(user.getEmail().getValue()).isEqualTo("alex.rivers@example.com");
        assertThat(user.getRole()).isEqualTo(UserRole.ADMIN);
        assertThat(user.getLocale()).isEqualTo("en-US");
        assertThat(user.getBio()).isEqualTo("Senior Engineer");
        assertThat(user.getAvatarUrl()).isEqualTo("https://api.dicebear.com/7.x/avataaars/svg?seed=Alex");
        assertThat(user.getObsProfile()).isEqualTo("Observations");
    }

    @Test
    void should_map_user_to_user_response_dto() {
        user.auditCreate(5L, now);
        user.auditUpdate(5L, now);



        UserResponseDTO dto = mapper.toResponseDTO(user);



        assertThat(dto.getId()).isEqualTo(user.getId());
        assertThat(dto.getActive()).isEqualTo(user.isActive());
        assertThat(dto.getDeleted()).isEqualTo(user.isDeleted());
        assertThat(dto.getCreatedAt()).isEqualTo(user.getCreatedAt());
        assertThat(dto.getUpdatedAt()).isEqualTo(user.getUpdatedAt());

        assertThat(dto.getFirstName()).isEqualTo(user.getFirstName());
        assertThat(dto.getLastName()).isEqualTo(user.getLastName());
        assertThat(dto.getBirthDate()).isEqualTo(user.getBirthDate());
        assertThat(dto.getPhone()).isEqualTo(user.getPhone());

        assertThat(dto.getCpf()).isEqualTo(user.getCpf().toString());
        assertThat(dto.getEmail()).isEqualTo(user.getEmail().toString());

        assertThat(dto.getRole()).isEqualTo(user.getRole());
        assertThat(dto.getStatus()).isEqualTo(user.getStatus());

        assertThat(dto.getEmailVerified()).isEqualTo(user.isEmailVerified());
        assertThat(dto.getLastLoginAt()).isEqualTo(user.getLastLoginAt());

        assertThat(dto.getBio()).isEqualTo(user.getBio());
        assertThat(dto.getAvatarUrl()).isEqualTo(user.getAvatarUrl());
        assertThat(dto.getLocale()).isEqualTo(user.getLocale());

        assertThat(dto.getCreatedBy()).isEqualTo(user.getCreatedBy());
        assertThat(dto.getUpdatedBy()).isEqualTo(user.getUpdatedBy());
    }

    @Test
    void should_map_user_to_user_list_response_dto() {
        UserListResponseDTO dto = mapper.toListResponseDTO(user);

        assertThat(dto.getId()).isEqualTo(user.getId());
        assertThat(dto.getFirstName()).isEqualTo(user.getFirstName());
        assertThat(dto.getLastName()).isEqualTo(user.getLastName());
        assertThat(dto.getEmail()).isEqualTo(user.getEmail().toString());
        assertThat(dto.getRole()).isEqualTo(user.getRole());
        assertThat(dto.getStatus()).isEqualTo(user.getStatus());
        assertThat(dto.getActive()).isEqualTo(user.isActive());
        assertThat(dto.getDeleted()).isEqualTo(user.isDeleted());
    }

    @Test
    void should_map_register_request_to_user_entity() {
        RegisterRequestDTO dto = registerRequest();

        User user = mapper.fromRegisterRequest(dto);

        assertThat(user.getFirstName()).isEqualTo("Alex");
        assertThat(user.getLastName()).isEqualTo("Rivers");
        assertThat(user.getEmail().getValue()).isEqualTo("alex.rivers@example.com");
        assertThat(user.getCpf().getValue()).isEqualTo("12345678900");
        assertThat(user.getLocale()).isEqualTo("en-US");
        assertThat(user.getPhone()).isEqualTo("+1 (555) 123-4567");
        assertThat(user.getAvatarUrl()).isEqualTo("https://api.dicebear.com/7.x/avataaars/svg?seed=Alex");
    }
}