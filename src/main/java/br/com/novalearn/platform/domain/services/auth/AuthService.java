package br.com.novalearn.platform.domain.services.auth;

import br.com.novalearn.platform.api.dtos.login.LoginRequestDTO;
import br.com.novalearn.platform.api.dtos.login.LoginResponseDTO;
import br.com.novalearn.platform.api.dtos.logout.LogoutRequestDTO;
import br.com.novalearn.platform.api.dtos.password.ChangePasswordRequestDTO;
import br.com.novalearn.platform.api.dtos.refreshtoken.RefreshTokenRequestDTO;
import br.com.novalearn.platform.api.dtos.refreshtoken.RefreshTokenResponseDTO;
import br.com.novalearn.platform.api.dtos.register.RegisterRequestDTO;
import br.com.novalearn.platform.api.dtos.register.RegisterResponseDTO;
import br.com.novalearn.platform.api.dtos.user.UserResponseDTO;
import br.com.novalearn.platform.api.mappers.user.UserMapper;
import br.com.novalearn.platform.core.exception.auth.InvalidCredentialsException;
import br.com.novalearn.platform.core.exception.auth.TokenExpiredException;
import br.com.novalearn.platform.core.exception.auth.UnauthorizedException;
import br.com.novalearn.platform.core.exception.business.ConflictException;
import br.com.novalearn.platform.core.exception.business.InvalidStateException;
import br.com.novalearn.platform.core.exception.business.ResourceNotFoundException;
import br.com.novalearn.platform.core.security.JWTUtil;
import br.com.novalearn.platform.domain.entities.refreshtoken.RefreshToken;
import br.com.novalearn.platform.domain.entities.user.User;
import br.com.novalearn.platform.domain.repositories.refreshtoken.RefreshTokenRepository;
import br.com.novalearn.platform.domain.repositories.user.UserRepository;
import br.com.novalearn.platform.domain.valueobjects.Email;
import br.com.novalearn.platform.infra.time.provider.TimeProvider;
import jakarta.transaction.Transactional;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JWTUtil jwtUtil;
    private final UserMapper userMapper;
    private final TimeProvider timeProvider;
    private final ApplicationEventPublisher eventPublisher;

    public AuthService(
            UserRepository userRepository,
            RefreshTokenRepository refreshTokenRepository,
            PasswordEncoder passwordEncoder,
            AuthenticationManager authenticationManager,
            JWTUtil jwtUtil,
            UserMapper userMapper,
            TimeProvider timeProvider,
            ApplicationEventPublisher eventPublisher
    ) {
        this.userRepository = userRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.userMapper = userMapper;
        this.timeProvider = timeProvider;
        this.eventPublisher = eventPublisher;
    }

    public void checkAuthentication() {
        getAuthenticatedUserEntity();
    }

    public Long getAuthenticatedUserId() {
        return getAuthenticatedUserEntity().getId();
    }

    public UserResponseDTO getAuthenticatedUser() {
        return userMapper.toResponseDTO(getAuthenticatedUserEntity());
    }

    public User getAuthenticatedUserEntity() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if(auth == null || !auth.isAuthenticated()) {
            throw new UnauthorizedException("User is not authenticated.");
        }

        if(!(auth.getPrincipal() instanceof Long userId)) {
            throw new InvalidStateException("Invalid authentication principal.");
        }

        return userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("Authenticated user not found."));
    }

    @Transactional
    public RegisterResponseDTO register(RegisterRequestDTO dto) {
        Long userId = 5L;

        Email emailVo = new Email(dto.getEmail());

        if(userRepository.existsByEmailAndDeletedFalse(emailVo)) {
            throw new ConflictException("Email already registered.");
        }

        User user = User.register(
                dto.getFirstName(),
                dto.getLastName(),
                dto.getEmail(),
                dto.getCpf(),
                dto.getLocale(),
                dto.getPhone(),
                dto.getAvatarUrl()
        );

        User saved = userRepository.save(user);

        saved.getDomainEvents().forEach(eventPublisher::publishEvent);
        saved.clearEvents();

        return new RegisterResponseDTO(
                userId,
                saved.getEmail().toString(),
                saved.isEmailVerified(),
                saved.getCreatedAt()
        );
    }

    @Transactional
    public LoginResponseDTO login(LoginRequestDTO dto) {
        authenticate(dto);

        Email emailVo = new Email(dto.getEmail());

        User user = userRepository.findByEmailAndDeletedFalse(emailVo)
                .orElseThrow(() -> new ResourceNotFoundException("User not found."));

        boolean rememberMe = Boolean.TRUE.equals(dto.getRememberMe());

        long refreshSeconds = rememberMe
                ? jwtUtil.getRefreshTokenExpirationRememberMeSeconds()
                : jwtUtil.getRefreshTokenExpirationDefaultSeconds();

        String accessToken = jwtUtil.generateAccessToken(user);
        String refreshToken = jwtUtil.generateRefreshToken(user, refreshSeconds);

        RefreshToken entity = RefreshToken.create(
                user,
                jwtUtil.hashToken(refreshToken),
                timeProvider.now().plusSeconds(refreshSeconds),
                user.getId(),
                timeProvider
        );

        refreshTokenRepository.save(entity);

        return new LoginResponseDTO(
                accessToken,
                refreshToken,
                jwtUtil.getAccessTokenExpirationSeconds(),
                "Bearer"
        );
    }

    @Transactional
    public RefreshTokenResponseDTO refreshToken(RefreshTokenRequestDTO dto) {
        String hash = jwtUtil.hashToken(dto.getRefreshToken());

        RefreshToken token = refreshTokenRepository.findByTokenHash(hash)
                .orElseThrow(() -> new TokenExpiredException("Invalid refresh token."));

        token.assertUsable(timeProvider.now());

        User user = token.getUser();

        String newAccess = jwtUtil.generateAccessToken(user);
        String newRefresh = jwtUtil.generateRefreshToken(
                user,
                jwtUtil.getRefreshTokenExpirationDefaultSeconds()
        );

        token.rotate(
                jwtUtil.hashToken(newRefresh),
                timeProvider.now().plusSeconds(jwtUtil.getRefreshTokenExpirationDefaultSeconds()),
                user.getId(),
                LocalDateTime.now()
        );

        return new RefreshTokenResponseDTO(
                newAccess,
                newRefresh,
                jwtUtil.getAccessTokenExpirationSeconds(),
                "Bearer",
                false
        );
    }

    @Transactional
    public void logout(LogoutRequestDTO dto) {
        String hash = jwtUtil.hashToken(dto.getRefreshToken());

        refreshTokenRepository.findByTokenHash(hash)
                .ifPresent(token ->
                        token.revoke(getAuthenticatedUserId(), LocalDateTime.now())
                );
    }

    @Transactional
    public void logoutAll() {
        User user = getAuthenticatedUserEntity();

        List<RefreshToken> tokens = refreshTokenRepository.findAllByUserAndRevokedFalse(user);

        tokens.forEach(token -> token.revoke(user.getId(), LocalDateTime.now()));
    }

    @Transactional
    public void changePassword(ChangePasswordRequestDTO dto) {
        User user = getAuthenticatedUserEntity();

        if(!passwordEncoder.matches(dto.getCurrentPassword(), user.getPasswordHash())) {
            throw new InvalidCredentialsException("Current password is incorrect.");
        }

        String newHash = passwordEncoder.encode(dto.getNewPassword());

        user.changePassword(newHash);

        user.setUpdatedAt(timeProvider.now());

        userRepository.save(user);

        logoutAll();
    }

    private void authenticate(LoginRequestDTO dto) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            dto.getEmail(),
                            dto.getPassword()
                    )
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);
        } catch (AuthenticationException ex) {
            throw new InvalidCredentialsException("Invalid email or password.");
        }
    }
}