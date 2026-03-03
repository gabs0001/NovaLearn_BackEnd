package br.com.novalearn.platform.api.dtos.register;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class RegisterRequestDTO {
    @NotBlank(message = "First name is required.")
    @Size(max = 100)
    private String firstName;

    @Size(max = 100)
    private String lastName;

    @NotBlank(message = "Email is required.")
    @Email(message = "Email must be valid.")
    @Size(max = 150)
    private String email;

    @NotBlank(message = "Password is required.")
    @Size(min = 8, max = 100)
    private String password;

    @Size(max = 14)
    private String cpf;

    @Size(max = 10)
    private String locale;

    @Size(max = 20)
    private String phone;

    @Size(max = 500)
    private String avatarUrl;

    protected RegisterRequestDTO() {}

    public RegisterRequestDTO(
            String firstName,
            String lastName,
            String email,
            String password,
            String cpf,
            String locale,
            String phone,
            String avatarUrl
    ) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.cpf = cpf;
        this.locale = locale;
        this.phone = phone;
        this.avatarUrl = avatarUrl;
    }

    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }
    public String getCpf() { return cpf; }
    public String getLocale() { return locale; }
    public String getPhone() { return phone; }
    public String getAvatarUrl() { return avatarUrl; }
}