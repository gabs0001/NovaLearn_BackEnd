package br.com.novalearn.platform.api.dtos.login;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class LoginRequestDTO {
    @NotBlank(message = "Email is required.")
    @Email(message = "Email must be valid.")
    private String email;

    @NotBlank(message = "Password is required.")
    private String password;

    private Boolean rememberMe;

    protected LoginRequestDTO() {}

    public LoginRequestDTO(String email, String password, Boolean rememberMe) {
        this.email = email;
        this.password = password;
        this.rememberMe = rememberMe;
    }

    public String getEmail() { return email; }
    public String getPassword() { return password; }
    public Boolean getRememberMe() { return rememberMe; }
}