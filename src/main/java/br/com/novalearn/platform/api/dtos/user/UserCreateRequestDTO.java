package br.com.novalearn.platform.api.dtos.user;

import br.com.novalearn.platform.domain.enums.UserRole;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserCreateRequestDTO {
    @NotBlank(message = "First name is required.")
    @Size(min = 2, max = 100, message = "First name must be between 2 and 100 characters.")
    private String firstName;

    @NotBlank(message = "Last name is required.")
    @Size(min = 2, max = 100, message = "Last name must be between 2 and 100 characters.")
    private String lastName;

    @Past(message = "Birth date must be in the past.")
    private LocalDateTime birthDate;

    @Size(min = 8, max = 20, message = "Phone must be between 8 and 20 characters.")
    private String phone;

    @NotBlank(message = "CPF is required.")
    @Size(min = 11, max = 11, message = "CPF must contain exactly 11 digits.")
    private String cpf;

    @NotBlank(message = "Email is required.")
    private String email;

    @NotBlank(message = "Password is required.")
    @Size(min = 8, max = 100, message = "Password must be between 8 and 100 characters.")
    private String password;

    @NotNull
    private UserRole role;

    @NotBlank(message = "Locale is required.")
    private String locale;

    @Size(max = 500, message = "Bio cannot exceed 500 characters.")
    private String bio;

    @Size(max = 255, message = "Avatar URL cannot exceed 255 characters.")
    private String avatarUrl;

    @Size(max = 500, message = "Observations cannot exceed 500 characters.")
    private String observations;

    public UserCreateRequestDTO() {}

    public UserCreateRequestDTO(
            String firstName,
            String lastName,
            LocalDateTime birthDate,
            String phone,
            String cpf,
            String email,
            String password,
            UserRole role,
            String locale,
            String bio,
            String avatarUrl,
            String observations
    ) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.birthDate = birthDate;
        this.phone = phone;
        this.cpf = cpf;
        this.email = email;
        this.password = password;
        this.role = role;
        this.locale = locale;
        this.bio = bio;
        this.avatarUrl = avatarUrl;
        this.observations = observations;
    }

    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public LocalDateTime getBirthDate() { return birthDate; }
    public String getPhone() { return phone; }
    public String getCpf() { return cpf; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }
    public UserRole getRole() { return role; }
    public String getLocale() { return locale; }
    public String getBio() { return bio; }
    public String getAvatarUrl() { return avatarUrl; }
    public String getObservations() { return observations; }

    public void setFirstName(String firstName) { this.firstName = firstName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public void setBirthDate(LocalDateTime birthDate) { this.birthDate = birthDate; }
    public void setPhone(String phone) { this.phone = phone; }
    public void setCpf(String cpf) { this.cpf = cpf; }
    public void setEmail(String email) { this.email = email; }
    public void setPasswordHash(String password) { this.password = password; }
    public void setRole(UserRole role) { this.role = role; }
    public void setLocale(String locale) { this.locale = locale; }
    public void setBio(String bio) { this.bio = bio; }
    public void setAvatarUrl(String avatarUrl) { this.avatarUrl = avatarUrl; }
    public void setObservations(String observations) { this.observations = observations; }
}