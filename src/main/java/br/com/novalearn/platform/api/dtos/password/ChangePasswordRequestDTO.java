package br.com.novalearn.platform.api.dtos.password;

import br.com.novalearn.platform.core.exception.business.ValidationException;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ChangePasswordRequestDTO {
    @NotBlank(message = "Current password is required.")
    private String currentPassword;

    @NotBlank(message = "New password is required.")
    @Size(min = 8, max = 100)
    private String newPassword;

    @NotBlank(message = "Password confirmation is required.")
    private String confirmNewPassword;

    protected ChangePasswordRequestDTO() {}

    public ChangePasswordRequestDTO(
            String currentPassword,
            String newPassword,
            String confirmNewPassword
    ) {
        this.currentPassword = currentPassword;
        this.newPassword = newPassword;
        this.confirmNewPassword = confirmNewPassword;
    }

    public void validate() {
        if(!newPassword.equals(confirmNewPassword)) {
            throw new ValidationException("Password confirmation does not match.");
        }
    }

    public String getCurrentPassword() { return currentPassword; }
    public String getNewPassword() { return newPassword; }
    public String getConfirmNewPassword() { return confirmNewPassword; }
}