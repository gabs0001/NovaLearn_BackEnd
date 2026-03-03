package br.com.novalearn.platform.domain.services.auth;

import br.com.novalearn.platform.api.dtos.login.LoginRequestDTO;
import br.com.novalearn.platform.api.dtos.login.LoginResponseDTO;
import br.com.novalearn.platform.api.dtos.password.ChangePasswordRequestDTO;
import br.com.novalearn.platform.api.dtos.register.RegisterRequestDTO;
import br.com.novalearn.platform.api.dtos.register.RegisterResponseDTO;
import br.com.novalearn.platform.api.mappers.user.UserMapper;
import br.com.novalearn.platform.core.exception.auth.InvalidCredentialsException;
import br.com.novalearn.platform.core.exception.auth.UnauthorizedException;
import br.com.novalearn.platform.core.exception.business.ConflictException;
import br.com.novalearn.platform.core.exception.business.InvalidStateException;
import br.com.novalearn.platform.core.security.JWTUtil;
import br.com.novalearn.platform.domain.entities.user.User;
import br.com.novalearn.platform.domain.repositories.refreshtoken.RefreshTokenRepository;
import br.com.novalearn.platform.domain.repositories.user.UserRepository;
import br.com.novalearn.platform.domain.valueobjects.Email;
import br.com.novalearn.platform.infra.time.provider.TimeProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JWTUtil jwtUtil;

    @Mock
    private UserMapper userMapper;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @Mock
    private TimeProvider timeProvider;

    @InjectMocks
    private AuthService authService;

    private Authentication auth;
    private User user;

    private final String emailMock = "test@email.com";

    @BeforeEach
    void setUp() {
        SecurityContextHolder.clearContext();
        auth = mock(Authentication.class);
        user = mock(User.class);
    }

    @Test
    void should_return_authenticated_user_entity() {
        //arrange
        Long userId = 5L;

        when(auth.isAuthenticated()).thenReturn(true);
        when(auth.getPrincipal()).thenReturn(userId);

        SecurityContextHolder.getContext().setAuthentication(auth);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        //act
        User result = authService.getAuthenticatedUserEntity();

        //assert
        assertThat(result).isEqualTo(user);
    }

    @Test
    void should_throw_when_not_authenticated() {
        //arrange
        SecurityContextHolder.clearContext();

        //act + assert
        assertThatThrownBy(() -> authService.getAuthenticatedUserEntity())
                .isInstanceOf(UnauthorizedException.class);
    }

    @Test
    void should_throw_when_principal_invalid() {
        when(auth.isAuthenticated()).thenReturn(true);
        when(auth.getPrincipal()).thenReturn("abc");

        SecurityContextHolder.getContext().setAuthentication(auth);

        assertThatThrownBy(() -> authService.getAuthenticatedUserEntity())
                .isInstanceOf(InvalidStateException.class);
    }

    @Test
    void should_register_user() {
        //arrange
        RegisterRequestDTO dto = mock(RegisterRequestDTO.class);

        when(dto.getEmail()).thenReturn(emailMock);
        when(dto.getFirstName()).thenReturn("John");
        when(dto.getLastName()).thenReturn("Doe");

        when(userRepository.existsByEmailAndDeletedFalse(any())).thenReturn(false);

        when(user.getEmail()).thenReturn(new Email(emailMock));
        when(user.isEmailVerified()).thenReturn(false);
        when(user.getCreatedAt()).thenReturn(LocalDateTime.now());

        when(userRepository.save(any())).thenReturn(user);

        //act
        RegisterResponseDTO response = authService.register(dto);

        //assert
        assertThat(response.getUserId()).isEqualTo(5L);
    }

    @Test
    void should_throw_when_email_exists() {
        RegisterRequestDTO dto = mock(RegisterRequestDTO.class);

        when(dto.getEmail()).thenReturn(emailMock);
        when(userRepository.existsByEmailAndDeletedFalse(any())).thenReturn(true);

        assertThatThrownBy(() -> authService.register(dto)).isInstanceOf(ConflictException.class);
    }

    @Test
    void should_login_successfully() {
        //arrange
        LoginRequestDTO dto = mock(LoginRequestDTO.class);

        when(dto.getEmail()).thenReturn(emailMock);
        when(dto.getPassword()).thenReturn("123");
        when(dto.getRememberMe()).thenReturn(false);

        when(authenticationManager.authenticate(any())).thenReturn(auth);

        Email email = new Email(emailMock);

        when(user.getId()).thenReturn(1L);
        when(userRepository.findByEmailAndDeletedFalse(email)).thenReturn(Optional.of(user));
        when(jwtUtil.generateAccessToken(user)).thenReturn("access");
        when(jwtUtil.generateRefreshToken(any(), anyLong())).thenReturn("refresh");
        when(jwtUtil.hashToken(any())).thenReturn("hash");
        when(jwtUtil.getAccessTokenExpirationSeconds()).thenReturn(3600L);
        when(jwtUtil.getRefreshTokenExpirationDefaultSeconds()).thenReturn(7200L);
        when(timeProvider.now()).thenReturn(LocalDateTime.now());

        //act
        LoginResponseDTO response = authService.login(dto);

        //assert
        assertThat(response.getAccessToken()).isEqualTo("access");
        assertThat(response.getRefreshToken()).isEqualTo("refresh");
    }

    @Test
    void should_throw_when_invalid_credentials() {
        LoginRequestDTO dto = mock(LoginRequestDTO.class);

        when(authenticationManager.authenticate(any())).thenThrow(new BadCredentialsException("Invalid credentials"));

        assertThatThrownBy(() -> authService.login(dto))
                .isInstanceOf(InvalidCredentialsException.class);
    }

    @Test
    void should_change_password() {
        //arrange
        when(user.getPasswordHash()).thenReturn("old");

        when(auth.isAuthenticated()).thenReturn(true);
        when(auth.getPrincipal()).thenReturn(1L);

        SecurityContextHolder.getContext().setAuthentication(auth);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        ChangePasswordRequestDTO dto = mock(ChangePasswordRequestDTO.class);

        when(dto.getCurrentPassword()).thenReturn("123");
        when(dto.getNewPassword()).thenReturn("456");
        when(passwordEncoder.matches(any(), any())).thenReturn(true);
        when(passwordEncoder.encode("456")).thenReturn("newHash");
        when(timeProvider.now()).thenReturn(LocalDateTime.now());

        //act
        authService.changePassword(dto);

        //assert
        verify(user).changePassword("newHash");
        verify(userRepository).save(user);
    }

    @Test
    void should_throw_when_current_password_invalid() {
        when(user.getPasswordHash()).thenReturn("old");

        when(auth.isAuthenticated()).thenReturn(true);
        when(auth.getPrincipal()).thenReturn(1L);

        SecurityContextHolder.getContext().setAuthentication(auth);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        ChangePasswordRequestDTO dto = mock(ChangePasswordRequestDTO.class);

        when(passwordEncoder.matches(any(), any())).thenReturn(false);

        assertThatThrownBy(() -> authService.changePassword(dto))
                .isInstanceOf(InvalidCredentialsException.class);
    }
}