package br.com.novalearn.platform.api.dtos.user;

import br.com.novalearn.platform.api.dtos.BaseDTO;
import br.com.novalearn.platform.domain.enums.UserRole;
import br.com.novalearn.platform.domain.enums.UserStatus;
import br.com.novalearn.platform.domain.valueobjects.Cpf;
import br.com.novalearn.platform.domain.valueobjects.Email;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserResponseDTO extends BaseDTO {
    private String firstName;
    private String lastName;
    private LocalDateTime birthDate;
    private String phone;
    private String cpf;
    private String email;
    private UserRole role;
    private UserStatus status;
    private Boolean emailVerified;
    private LocalDateTime lastLoginAt;
    private String bio;
    private String avatarUrl;
    private String locale;
    private Long createdBy;
    private Long updatedBy;

    public UserResponseDTO() {}

    public UserResponseDTO(
            Long id,
            Boolean active,
            Boolean deleted,
            LocalDateTime createdAt,
            LocalDateTime updatedAt,
            String observations,
            String firstName,
            String lastName,
            LocalDateTime birthDate,
            String phone,
            String cpf,
            String email,
            UserRole role,
            UserStatus status,
            Boolean emailVerified,
            LocalDateTime lastLoginAt,
            String bio,
            String avatarUrl,
            String locale,
            Long createdBy,
            Long updatedBy
    ) {
        super(id, active, deleted, createdAt, updatedAt, observations);
        this.firstName = firstName;
        this.lastName = lastName;
        this.birthDate = birthDate;
        this.phone = phone;
        this.cpf = cpf;
        this.email = email;
        this.role = role;
        this.status = status;
        this.emailVerified = emailVerified;
        this.lastLoginAt = lastLoginAt;
        this.bio = bio;
        this.avatarUrl = avatarUrl;
        this.locale = locale;
        this.createdBy = createdBy;
        this.updatedBy = updatedBy;
    }

    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public LocalDateTime getBirthDate() { return birthDate; }
    public String getPhone() { return phone; }
    public String getCpf() { return cpf; }
    public String getEmail() { return email; }
    public UserRole getRole() { return role; }
    public UserStatus getStatus() { return status; }
    public Boolean getEmailVerified() { return emailVerified; }
    public LocalDateTime getLastLoginAt() { return lastLoginAt; }
    public String getBio() { return bio; }
    public String getAvatarUrl() { return avatarUrl; }
    public String getLocale() { return locale; }
    public Long getCreatedBy() { return createdBy; }
    public Long getUpdatedBy() { return updatedBy; }
}