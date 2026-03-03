package br.com.novalearn.platform.domain.services.user;

import br.com.novalearn.platform.api.dtos.password.ChangePasswordRequestDTO;
import br.com.novalearn.platform.api.dtos.user.UserCreateRequestDTO;
import br.com.novalearn.platform.api.dtos.user.UserResponseDTO;
import br.com.novalearn.platform.api.dtos.user.UserUpdateRequestDTO;
import br.com.novalearn.platform.api.dtos.user.activity.UserActivityResponseDTO;
import br.com.novalearn.platform.api.mappers.user.UserMapper;
import br.com.novalearn.platform.core.exception.auth.InvalidCredentialsException;
import br.com.novalearn.platform.core.exception.business.ConflictException;
import br.com.novalearn.platform.core.exception.business.ValidationException;
import br.com.novalearn.platform.domain.entities.user.User;
import br.com.novalearn.platform.domain.enums.UserRole;
import br.com.novalearn.platform.domain.repositories.user.UserCourseRepository;
import br.com.novalearn.platform.domain.repositories.user.UserQuizAttemptRepository;
import br.com.novalearn.platform.domain.repositories.user.UserRepository;
import br.com.novalearn.platform.domain.valueobjects.Email;
import br.com.novalearn.platform.infra.time.provider.TimeProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private UserCourseRepository userCourseRepository;

    @Mock
    private UserQuizAttemptRepository userQuizAttemptRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private TimeProvider timeProvider;

    @InjectMocks
    private UserService userService;

    private User user;
    private UserCreateRequestDTO dto;
    private ChangePasswordRequestDTO changePasswordDTO;

    private final String emailMock = "test@email.com";

    @BeforeEach
    void setUp() {
        SecurityContextHolder.clearContext();
        user = mock(User.class);
        dto = mock(UserCreateRequestDTO.class);
        changePasswordDTO = mock(ChangePasswordRequestDTO.class);
    }

    @Test
    void should_create_user() {
        //arrange
        when(dto.getEmail()).thenReturn(emailMock);
        when(dto.getCpf()).thenReturn("12345678900");
        when(dto.getPassword()).thenReturn("123");

        when(userRepository.existsByEmailAndDeletedFalse(any())).thenReturn(false);
        when(userRepository.existsByCpfAndDeletedFalse(any())).thenReturn(false);

        when(userMapper.toEntity(dto)).thenReturn(user);
        when(passwordEncoder.encode("123")).thenReturn("hash");
        when(timeProvider.now()).thenReturn(LocalDateTime.now());
        when(userRepository.save(user)).thenReturn(user);

        UserResponseDTO response = mock(UserResponseDTO.class);

        when(userMapper.toResponseDTO(user)).thenReturn(response);

        //act
        UserResponseDTO result = userService.create(dto);

        //assert
        verify(user).initializeNewUser(eq("hash"), any());
        assertThat(result).isEqualTo(response);
    }

    @Test
    void should_throw_when_email_exists_on_create() {
        when(dto.getEmail()).thenReturn(emailMock);
        when(dto.getCpf()).thenReturn("12345678901");

        when(userRepository.existsByEmailAndDeletedFalse(any())).thenReturn(true);

        assertThatThrownBy(() -> userService.create(dto))
                .isInstanceOf(ConflictException.class);
    }

    @Test
    void should_throw_when_cpf_exists_on_create() {
        when(dto.getEmail()).thenReturn(emailMock);
        when(dto.getCpf()).thenReturn("12345678901");

        when(userRepository.existsByEmailAndDeletedFalse(any())).thenReturn(false);
        when(userRepository.existsByCpfAndDeletedFalse(any())).thenReturn(true);

        assertThatThrownBy(() -> userService.create(dto)).isInstanceOf(ConflictException.class);
    }

    @Test
    void should_update_user() {
        when(userRepository.findById(5L)).thenReturn(Optional.of(user));

        UserUpdateRequestDTO dto = mock(UserUpdateRequestDTO.class);

        when(timeProvider.now()).thenReturn(LocalDateTime.now());
        when(userRepository.save(user)).thenReturn(user);

        UserResponseDTO response = mock(UserResponseDTO.class);

        when(userMapper.toResponseDTO(user)).thenReturn(response);

        //act
        UserResponseDTO result = userService.update(5L, dto, 5L);

        //assert
        verify(user).updateProfile(dto);
        verify(user).setUpdatedBy(5L);
        verify(user).setUpdatedAt(any());

        assertThat(result).isEqualTo(response);
    }

    @Test
    void should_change_my_password() {
        when(user.getPasswordHash()).thenReturn("old");
        when(userRepository.findById(5L)).thenReturn(Optional.of(user));

        when(changePasswordDTO.getCurrentPassword()).thenReturn("123");
        when(changePasswordDTO.getNewPassword()).thenReturn("456");
        when(changePasswordDTO.getConfirmNewPassword()).thenReturn("456");

        when(passwordEncoder.matches("123", "old")).thenReturn(true);
        when(passwordEncoder.encode("456")).thenReturn("hash");
        when(timeProvider.now()).thenReturn(LocalDateTime.now());

        //act
        userService.changeMyPassword(5L, changePasswordDTO);

        //assert
        verify(user).changePassword("hash");
        verify(userRepository).save(user);
    }

    @Test
    void should_throw_when_current_password_invalid() {
        when(user.getPasswordHash()).thenReturn("old");
        when(userRepository.findById(5L)).thenReturn(Optional.of(user));

        when(passwordEncoder.matches(any(), any())).thenReturn(false);

        assertThatThrownBy(() -> userService.changeMyPassword(5L, changePasswordDTO))
                .isInstanceOf(InvalidCredentialsException.class);
    }

    @Test
    void should_throw_when_passwords_do_not_match() {
        when(user.getPasswordHash()).thenReturn("old");
        when(userRepository.findById(5L)).thenReturn(Optional.of(user));

        when(changePasswordDTO.getNewPassword()).thenReturn("456");
        when(changePasswordDTO.getConfirmNewPassword()).thenReturn("999");
        when(passwordEncoder.matches(any(), any())).thenReturn(true);

        assertThatThrownBy(() -> userService.changeMyPassword(5L, changePasswordDTO))
                .isInstanceOf(ValidationException.class);
    }

    @Test
    void should_reset_password() {
        when(userRepository.findById(5L)).thenReturn(Optional.of(user));
        when(passwordEncoder.encode(any())).thenReturn("hash");
        when(timeProvider.now()).thenReturn(LocalDateTime.now());

        //act
        userService.resetPassword(5L, 5L);

        //assert
        verify(user).resetPassword("hash");
        verify(user).auditUpdate(eq(5L), any());
        verify(userRepository).save(user);
    }

    @Test
    void should_return_user_activity_with_last_attempt() {
        when(user.getId()).thenReturn(5L);
        when(userRepository.findById(5L)).thenReturn(Optional.of(user));

        LocalDateTime last = LocalDateTime.now();

        when(userQuizAttemptRepository
                .findLastActivityAtByUserId(5L))
                .thenReturn(Optional.of(last));

        when(userCourseRepository
                .countByUserIdAndDeletedFalse(5L))
                .thenReturn(5L);

        when(userCourseRepository
                .countCompletedByUserId(5L))
                .thenReturn(3L);

        when(userQuizAttemptRepository
                .countByUserIdAndDeletedFalse(5L))
                .thenReturn(10L);

        when(userQuizAttemptRepository
                .countByUserIdAndPassedTrueAndDeletedFalse(5L))
                .thenReturn(7L);

        //act
        UserActivityResponseDTO result = userService.getUserActivity(5L);

        //assert
        assertThat(result.getLastActivityAt()).isEqualTo(last);
    }

    @Test
    void should_load_user_by_username() {
        when(user.getEmail()).thenReturn(new Email("a@a.com"));
        when(user.getPasswordHash()).thenReturn("hash");
        when(user.getRole()).thenReturn(UserRole.ADMIN);
        when(user.isActive()).thenReturn(true);
        when(user.isDeleted()).thenReturn(false);

        when(userRepository.findByEmailAndDeletedFalse(any()))
                .thenReturn(Optional.of(user));

        UserDetails details = userService.loadUserByUsername("a@a.com");

        assertThat(details.getUsername()).isEqualTo("a@a.com");
    }

    @Test
    void should_throw_when_user_not_found_on_load() {
        when(userRepository.findByEmailAndDeletedFalse(any()))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.loadUserByUsername("x@x.com"))
                .isInstanceOf(UsernameNotFoundException.class);
    }
}