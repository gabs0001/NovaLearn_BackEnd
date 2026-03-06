package br.com.novalearn.platform.domain.entities.user;

import br.com.novalearn.platform.api.dtos.user.UserUpdateRequestDTO;
import br.com.novalearn.platform.api.events.AggregateRoot;
import br.com.novalearn.platform.core.exception.business.InvalidStateException;
import br.com.novalearn.platform.core.exception.business.ValidationException;
import br.com.novalearn.platform.domain.converters.BooleanToYNConverter;
import br.com.novalearn.platform.domain.enums.UserRole;
import br.com.novalearn.platform.domain.enums.UserStatus;
import br.com.novalearn.platform.domain.events.user.UserRegisteredEvent;
import br.com.novalearn.platform.infra.database.sequences.DatabaseSequences;
import br.com.novalearn.platform.infra.jpa.converter.objects.CpfConverter;
import br.com.novalearn.platform.infra.jpa.converter.objects.EmailConverter;
import br.com.novalearn.platform.infra.jpa.converter.role.UserRoleConverter;
import br.com.novalearn.platform.infra.jpa.converter.status.UserStatusConverter;
import jakarta.persistence.*;
import br.com.novalearn.platform.domain.valueobjects.Email;
import br.com.novalearn.platform.domain.valueobjects.Cpf;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@Entity
@Table(name = "T_SINL_USER")
@SequenceGenerator(
        name = DatabaseSequences.USER_SEQ,
        sequenceName = DatabaseSequences.USER_SEQ,
        allocationSize = 1
)
public class User extends AggregateRoot {
    @Id
    @Column(name = "cod_user", nullable = false)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = DatabaseSequences.USER_SEQ)
    private Long id;

    @NotBlank
    @Size(max = 120)
    @Column(name = "nom_first", length = 120, nullable = false)
    private String firstName;

    @Column(name = "nom_last", length = 120)
    private String lastName;

    @Column(name = "dat_birth")
    private LocalDateTime birthDate;

    @Convert(converter = CpfConverter.class)
    @Column(name = "num_cpf", length = 11)
    private Cpf cpf;

    @Size(max = 20)
    @Column(name = "num_phone", length = 20)
    private String phone;

    @Convert(converter = EmailConverter.class)
    @Column(name = "txt_email", nullable = false, length = 255, unique = true)
    private Email email;

    @NotBlank
    @Column(name = "txt_password_hash", nullable = false)
    private String passwordHash;

    @Convert(converter = UserStatusConverter.class)
    @Column(name = "sta_user", length = 30)
    private UserStatus status;

    @NotNull
    @Convert(converter = UserRoleConverter.class)
    @Column(name = "sig_role", length = 30, nullable = false)
    private UserRole role;

    @Size(max = 10)
    @Column(name = "sig_locale", length = 10)
    private String locale;

    @Convert(converter = BooleanToYNConverter.class)
    @Column(name = "ind_email_verified", length = 1, nullable = false)
    private boolean emailVerified;

    @Column(name = "dat_last_login")
    private LocalDateTime lastLoginAt;

    @Lob
    @Column(name = "txt_bio")
    private String bio;

    @Column(name = "val_avatar_url", length = 500)
    private String avatarUrl;

    @Column(name = "obs_profile", length = 500)
    private String obsProfile;

    protected User() {}

    public User(
            String firstName,
            String lastName,
            LocalDateTime birthDate,
            Cpf cpf,
            String phone,
            Email email,
            UserRole role,
            String locale,
            String bio,
            String avatarUrl,
            String obsProfile
    ) {
        if(email == null) throw new ValidationException("Email is required");
        if(role == null) throw new ValidationException("Role is required");

        this.firstName = firstName;
        this.lastName = lastName;
        this.birthDate = birthDate;
        this.cpf = cpf;
        this.phone = phone;
        this.email = email;
        this.role = role;
        this.locale = locale;
        this.bio = bio;
        this.avatarUrl = avatarUrl;
        this.obsProfile = obsProfile;
    }

    public static User register(
            String firstName,
            String lastName,
            String email,
            String cpf,
            String locale,
            String phone,
            String avatarUrl
    ) {
        LocalDateTime now = LocalDateTime.now();

        User user = new User(
                firstName,
                lastName,
                null,
                cpf != null ? new Cpf(cpf) : null,
                phone,
                new Email(email),
                UserRole.STUDENT,
                locale,
                null,
                avatarUrl,
                null
        );

        user.status = UserStatus.PENDING;
        user.emailVerified = false;
        user.active = true;
        user.deleted = false;

        user.registerEvent(new UserRegisteredEvent(
                user.getEmail().toString(),
                now
        ));

        return user;
    }

    public void initializeNewUser(String encodedPassword, LocalDateTime now) {
        validatePassword(encodedPassword);

        this.passwordHash = encodedPassword;
        this.status = UserStatus.INACTIVE;
        this.emailVerified = false;

        this.activate();
        this.setCreatedAt(now);
    }

    public void updateProfile(UserUpdateRequestDTO dto) {
        ensureNotDeleted();

        if(dto == null) throw new ValidationException("Update profile data is required");

        if(dto.getFirstName() != null) this.firstName = dto.getFirstName();
        if(dto.getLastName() != null) this.lastName = dto.getLastName();
        if(dto.getPhone() != null) this.phone = dto.getPhone();
        if(dto.getCpf() != null) this.cpf = new Cpf(dto.getCpf());
        if(dto.getBio() != null) this.bio = dto.getBio();
        if(dto.getAvatarUrl() != null) this.avatarUrl = dto.getAvatarUrl();
        if(dto.getLocale() != null) this.locale = dto.getLocale();
    }

    public void changePassword(String encodedPassword) {
        ensureNotDeleted();

        validatePassword(encodedPassword);
        this.passwordHash = encodedPassword;
    }

    public void resetPassword(String encodedPassword) {
        changePassword(encodedPassword);
    }

    public void verifyEmail() {
        ensureNotDeleted();

        if(emailVerified) throw new InvalidStateException("Email already verified");
        this.emailVerified = true;
    }

    public void markEmailUnverified() {
        ensureNotDeleted();
        this.emailVerified = false;
    }

    public void updateLastLogin(LocalDateTime now) {
        ensureNotDeleted();

        if(now == null) throw new ValidationException("Login time is required");

        this.lastLoginAt = now;
    }

    public void changeStatus(UserStatus newStatus) {
        ensureNotDeleted();

        if(newStatus == null) throw new ValidationException("User status is required");

        this.status = newStatus;
        this.setActive(newStatus == UserStatus.ACTIVE);
    }

    public void changeRole(UserRole newRole) {
        ensureNotDeleted();

        if(newRole == null) throw new ValidationException("User role is required");

        this.role = newRole;
    }

    private void validatePassword(String password) {
        if(password == null || password.isBlank())
            throw new ValidationException("Password cannot be empty");
    }

    public LocalDateTime getLastKnownActivity(LocalDateTime fallback) {
        return lastLoginAt != null ? lastLoginAt : fallback;
    }

    public Long getId() { return id; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public LocalDateTime getBirthDate() { return birthDate; }
    public Cpf getCpf() { return cpf; }
    public String getPhone() { return phone; }
    public Email getEmail() { return email; }
    public String getPasswordHash() { return passwordHash; }
    public UserStatus getStatus() { return status; }
    public UserRole getRole() { return role; }
    public String getLocale() { return locale; }
    public boolean isEmailVerified() { return emailVerified; }
    public LocalDateTime getLastLoginAt() { return lastLoginAt; }
    public String getBio() { return bio; }
    public String getAvatarUrl() { return avatarUrl; }
    public String getObsProfile() { return obsProfile; }

    @Override
    public String toString() {
        return "User{id=" + id +
                ", email=" + email +
                ", role=" + role +
                ", status=" + status +
                ", active=" + isActive() +
                ", deleted=" + isDeleted() +
                '}';
    }
}