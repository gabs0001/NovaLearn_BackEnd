package br.com.novalearn.platform.domain.services.user;

import br.com.novalearn.platform.api.dtos.password.ChangePasswordRequestDTO;
import br.com.novalearn.platform.api.dtos.user.UserCreateRequestDTO;
import br.com.novalearn.platform.api.dtos.user.UserListResponseDTO;
import br.com.novalearn.platform.api.dtos.user.UserResponseDTO;
import br.com.novalearn.platform.api.dtos.user.UserUpdateRequestDTO;
import br.com.novalearn.platform.api.dtos.user.activity.UserActivityResponseDTO;
import br.com.novalearn.platform.api.mappers.user.UserMapper;
import br.com.novalearn.platform.core.exception.auth.InvalidCredentialsException;
import br.com.novalearn.platform.core.exception.business.ConflictException;
import br.com.novalearn.platform.core.exception.business.ResourceNotFoundException;
import br.com.novalearn.platform.core.exception.business.ValidationException;
import br.com.novalearn.platform.domain.entities.user.User;
import br.com.novalearn.platform.domain.repositories.user.UserCourseRepository;
import br.com.novalearn.platform.domain.repositories.user.UserQuizAttemptRepository;
import br.com.novalearn.platform.domain.repositories.user.UserRepository;
import br.com.novalearn.platform.domain.services.BaseCrudService;
import br.com.novalearn.platform.domain.utils.PasswordGenerator;
import br.com.novalearn.platform.domain.valueobjects.Cpf;
import br.com.novalearn.platform.domain.valueobjects.Email;
import br.com.novalearn.platform.infra.time.provider.TimeProvider;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class UserService extends BaseCrudService<User> implements UserDetailsService {
    private final UserRepository userRepository;
    private final UserCourseRepository userCourseRepository;
    private final UserQuizAttemptRepository userQuizAttemptRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final TimeProvider timeProvider;

    public UserService(
            UserRepository userRepository,
            UserCourseRepository userCourseRepository,
            UserQuizAttemptRepository userQuizAttemptRepository,
            UserMapper userMapper,
            PasswordEncoder passwordEncoder,
            TimeProvider timeProvider
    ) {
        super(userRepository, "User", timeProvider);
        this.userRepository = userRepository;
        this.userCourseRepository = userCourseRepository;
        this.userQuizAttemptRepository = userQuizAttemptRepository;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
        this.timeProvider = timeProvider;
    }

    @Transactional
    public UserResponseDTO create(UserCreateRequestDTO dto) {
        validateCreate(dto);

        User user = userMapper.toEntity(dto);
        user.initializeNewUser(
                passwordEncoder.encode(dto.getPassword()),
                timeProvider.now()
        );

        User saved = userRepository.save(user);
        return userMapper.toResponseDTO(saved);
    }

    @Transactional
    public UserResponseDTO update(Long id, UserUpdateRequestDTO dto, Long actorId) {
        User user = findEntityOrThrow(id);

        user.updateProfile(dto);

        if(dto.getRole() != null) user.changeRole(dto.getRole());
        if(dto.getStatus() != null) user.changeStatus(dto.getStatus());

        applyAuditUpdate(user, actorId);

        return userMapper.toResponseDTO(userRepository.save(user));
    }

    @Transactional
    public void changeMyPassword(Long userId, ChangePasswordRequestDTO dto) {
        User user = findActiveUser(userId);

        if(!passwordEncoder.matches(dto.getCurrentPassword(), user.getPasswordHash())) {
            throw new InvalidCredentialsException("Current password is incorrect.");
        }

        if(!dto.getNewPassword().equals(dto.getConfirmNewPassword())) {
            throw new ValidationException("Passwords do not match.");
        }

        user.changePassword(passwordEncoder.encode(dto.getNewPassword()));
        applyAudit(user, userId);

        userRepository.save(user);
    }

    public void resetPassword(Long targetUserId, Long adminUserId) {
        User user = findActiveUser(targetUserId);

        String tempPassword = PasswordGenerator.generate();
        user.resetPassword(passwordEncoder.encode(tempPassword));

        applyAudit(user, adminUserId);
        userRepository.save(user);

        // envio de e-mail futuramente
    }

    @Transactional
    public UserResponseDTO findById(Long id) {
        return userMapper.toResponseDTO(findActiveUser(id));
    }

    @Transactional
    public Page<UserListResponseDTO> list(Pageable pageable) {
        return userRepository.findAllByDeletedFalse(pageable)
                .map(userMapper::toListResponseDTO);
    }

    @Transactional
    public UserActivityResponseDTO getUserActivity(Long userId) {
        User user = findEntityOrThrow(userId);

        LocalDateTime lastActivity =
                userQuizAttemptRepository
                        .findLastActivityAtByUserId(userId)
                        .orElse(user.getLastKnownActivity(timeProvider.now()));

        return new UserActivityResponseDTO(
                user.getId(),
                userCourseRepository.countByUserIdAndDeletedFalse(userId),
                userCourseRepository.countCompletedByUserId(userId),
                userQuizAttemptRepository.countByUserIdAndDeletedFalse(userId),
                userQuizAttemptRepository.countByUserIdAndPassedTrueAndDeletedFalse(userId),
                user.getLastLoginAt(),
                lastActivity
        );
    }

    private void validateCreate(UserCreateRequestDTO dto) {
        Email emailVO = new Email(dto.getEmail());
        Cpf cpfVO = new Cpf(dto.getCpf());

        if(userRepository.existsByEmailAndDeletedFalse(emailVO)) {
            throw new ConflictException("Email already registered");
        }

        if(dto.getCpf() != null && userRepository.existsByCpfAndDeletedFalse(cpfVO)) {
            throw new ConflictException("CPF already registered");
        }
    }

    public User findByIdOrThrow(Long id) {
        return userRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found."));
    }

    private User findActiveUser(Long id) {
        User user = findEntityOrThrow(id);
        user.ensureNotDeleted();
        return user;
    }

    private void applyAudit(User user, Long actorId) {
        user.auditUpdate(actorId, timeProvider.now());
    }

    @Override
    public UserDetails loadUserByUsername(String email) {
        User user = userRepository.findByEmailAndDeletedFalse(new Email(email))
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return org.springframework.security.core.userdetails.User
                .withUsername(user.getEmail().toString())
                .password(user.getPasswordHash())
                .authorities("ROLE_" + user.getRole())
                .accountLocked(!user.isActive())
                .disabled(user.isDeleted())
                .build();
    }
}