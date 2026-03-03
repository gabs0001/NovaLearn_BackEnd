package br.com.novalearn.platform.api.dtos.user;

import br.com.novalearn.platform.domain.enums.UserRole;
import br.com.novalearn.platform.domain.enums.UserStatus;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserUpdateRequestDTO {
    @Size(min = 2, max = 100, message = "First name must be between 2 and 100 characters.")
    private String firstName;

    @Size(min = 2, max = 100, message = "Last name must be between 2 and 100 characters.")
    private String lastName;

    @Past(message = "Birth date must be in the past.")
    private LocalDateTime birthDate;

    @Size(min = 8, max = 20, message = "Phone must be between 8 and 20 characters.")
    private String phone;

    @Size(min = 11, max = 11, message = "CPF must contain exactly 11 digits.")
    private String cpf;

    @Size(min = 2, max = 10, message = "Locale must be between 2 and 10 characters.")
    private String locale;

    @Size(max = 500, message = "Bio cannot exceed 500 characters.")
    private String bio;

    @Size(max = 255, message = "Avatar URL cannot exceed 255 characters.")
    private String avatarUrl;

    private UserRole role;

    private UserStatus status;

    public UserUpdateRequestDTO() {}

    public UserUpdateRequestDTO(
            String firstName,
            String lastName,
            LocalDateTime birthDate,
            String phone,
            String cpf,
            String locale,
            String bio,
            String avatarUrl,
            UserRole role,
            UserStatus status
    ) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.birthDate = birthDate;
        this.phone = phone;
        this.cpf = cpf;
        this.locale = locale;
        this.bio = bio;
        this.avatarUrl = avatarUrl;
        this.role = role;
        this.status = status;
    }

    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public LocalDateTime getBirthDate() { return birthDate; }
    public String getPhone() { return phone; }
    public String getCpf() { return cpf; }
    public String getLocale() { return locale; }
    public String getBio() { return bio; }
    public String getAvatarUrl() { return avatarUrl; }
    public UserRole getRole() { return role; }
    public UserStatus getStatus() { return status; }

    public void setFirstName(String firstName) { this.firstName = firstName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public void setBirthDate(LocalDateTime birthDate) { this.birthDate = birthDate; }
    public void setPhone(String phone) { this.phone = phone; }
    public void setCpf(String cpf) { this.cpf = cpf; }
    public void setLocale(String locale) { this.locale = locale; }
    public void setBio(String bio) { this.bio = bio; }
    public void setAvatarUrl(String avatarUrl) { this.avatarUrl = avatarUrl; }
    public void setRole(UserRole role) { this.role = role; }
    public void setStatus(UserStatus status) { this.status = status; }
}